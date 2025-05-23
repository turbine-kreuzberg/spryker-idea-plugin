package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocTypeImpl;
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

public class TransferObjectGotoHandler implements GotoDeclarationHandler {
    // Key for storing navigation offset
    private static final Key<Integer> TRANSFER_OFFSET_KEY = Key.create("PYZ_TRANSFER_DEFINITION_OFFSET");
    
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null) {
            return null;
        }
        
        Project project = sourceElement.getProject();
        if (!SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.TRANSFER_OBJECT_GOTO_HANDLING)) {
            return null;
        }

        String rawTransferObjectName = resolveTransferObjectName(sourceElement);
        if (rawTransferObjectName == null) {
            return null;
        }

        String transferObjectName = rawTransferObjectName.replace("Transfer", "").replace("[]", "");
        String searchTerm = "transfer name=\"" + transferObjectName + "\"";

        PsiFile[] psiFiles = CacheManager.getInstance(project).getFilesWithWord(
                searchTerm,
                UsageSearchContext.ANY,
                GlobalSearchScope.allScope(project),
                true
        );

        List<PsiElement> resolvedElements = new ArrayList<>();
        
        for (PsiFile psiFile : psiFiles) {
            if (psiFile.getFileType() instanceof XmlFileType && psiFile.getName().endsWith(".transfer.xml")) {
                // the already resolved files are found in a tokenized index - here we make sure that our complete search term matches
                if (psiFile.getContainingFile() != null && psiFile.getContainingFile().getText().contains(searchTerm)) {
                    // Find the offset of the transfer definition
                    String text = psiFile.getText();
                    int definitionOffset = text.indexOf(searchTerm);
                    
                    if (definitionOffset >= 0) {
                        // Create a reference to the file that shows nice information in the popup
                        TransferFileReference fileRef = new TransferFileReference(psiFile, transferObjectName);
                        // Store the navigation offset in the user data
                        fileRef.putUserData(TRANSFER_OFFSET_KEY, definitionOffset);
                        resolvedElements.add(fileRef);
                    } else {
                        resolvedElements.add(psiFile);
                    }
                }
            }
        }

        PsiFile[] classFile = FilenameIndex.getFilesByName(
            project,
            transferObjectName + "Transfer.php",
            GlobalSearchScope.allScope(project)
        );
        
        for (PsiFile file : classFile) {
            resolvedElements.add(file);
        }

        return resolvedElements.toArray(new PsiElement[0]);
    }

    /**
     * A custom PsiElement that represents a transfer definition file.
     * This element supports navigation to the exact offset of a transfer definition
     * while showing the filename in the chooser popup.
     */
    private static class TransferFileReference extends FakePsiElement {
        private final SmartPsiElementPointer<PsiFile> filePointer;
        private final String transferName;
        
        public TransferFileReference(PsiFile file, String transferName) {
            this.filePointer = SmartPointerManager.getInstance(file.getProject()).createSmartPsiElementPointer(file);
            this.transferName = transferName;
        }
        
        @Nullable
        private PsiFile getFile() {
            return filePointer.getElement();
        }
        
        @Override
        public PsiElement getParent() {
            PsiFile file = getFile();
            return file != null ? file.getParent() : null;
        }
        
        @Override
        public void navigate(boolean requestFocus) {
            PsiFile file = getFile();
            if (file == null || !file.isValid()) return;
            
            Integer offset = getUserData(TRANSFER_OFFSET_KEY);
            if (offset != null) {
                // Navigate to the file first
                file.navigate(requestFocus);
                
                // Use editor to scroll to the specific position
                Editor editor = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(file.getProject())
                    .getSelectedTextEditor();
                if (editor != null) {
                    editor.getCaretModel().moveToOffset(offset);
                    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                }
            } else {
                // Fall back to normal navigation
                file.navigate(requestFocus);
            }
        }
        
        @Override
        public boolean canNavigate() {
            PsiFile file = getFile();
            return file != null && file.canNavigate();
        }
        
        @Override
        public boolean canNavigateToSource() {
            PsiFile file = getFile();
            return file != null && file.canNavigateToSource();
        }
        
        @Override
        public @NotNull Project getProject() {
            return filePointer.getProject();
        }
        
        @Override
        public ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    PsiFile file = getFile();
                    return file != null ? file.getName() : "";
                }
                
                @Override
                public String getLocationString() {
                    PsiFile file = getFile();
                    if (file != null && file.getVirtualFile() != null) {
                        String path = file.getVirtualFile().getPath();

                        int vendorIndex = path.indexOf("/vendor/");
                        if (vendorIndex >= 0) {
                            String vendorPath = path.substring(vendorIndex + 1);
                            String[] parts = vendorPath.split("/");
                            if (parts.length >= 3) {
                                // Return vendor/namespace/module (e.g. vendor/spryker/product)
                                return parts[0] + "/" + parts[1] + "/" + parts[2];
                            }
                            return vendorPath;
                        }

                        int srcIndex = path.indexOf("/src/");
                        if (srcIndex >= 0) {
                            String namespace = SettingsManager.getPyzNamespace(file.getProject());
                            if (namespace == null || namespace.isEmpty()) {
                                namespace = "Pyz";
                            }

                            // Extract module name - it's between Shared/ and /Transfer
                            String modulePattern = "/Shared/";
                            int sharedIndex = path.indexOf(modulePattern, srcIndex);
                            if (sharedIndex > 0) {
                                int moduleStart = sharedIndex + modulePattern.length();
                                int moduleEnd = path.indexOf("/", moduleStart);
                                if (moduleEnd > 0) {
                                    return "src/" + namespace + "/Shared/" + path.substring(moduleStart, moduleEnd);
                                }
                            }
                        }

                        // Fallback - return truncated path if very long
                        if (path.length() > 60) {
                            return "..." + path.substring(path.length() - 60);
                        }
                        return path;
                    }
                    return "";
                }

                @Override
                public Icon getIcon(boolean unused) {
                    return XmlFileType.INSTANCE.getIcon();
                }
            };
        }

        @Override
        public String toString() {
            PsiFile file = getFile();
            return file != null ? file.getName() : "Unknown";
        }
    }

    private @Nullable String resolveTransferObjectName(PsiElement sourceElement) {
        if (sourceElement.getContainingFile().getName().endsWith(".transfer.xml") && StringUtil.isCapitalized(sourceElement.getText())) {
            if (isDefinition(sourceElement) || isUsage(sourceElement)) {
                return sourceElement.getText();
            }
        }

        if (sourceElement.getContainingFile().getFileType() != PhpFileType.INSTANCE) {
            return null;
        }

        if (sourceElement.getText().equals("AbstractTransfer")) {
            return null;
        }

        if (!sourceElement.getText().endsWith("Transfer")) {
            return null;
        }

        if (sourceElement.getParent() == null) {
            return null;
        }

        if ((sourceElement.getParent() instanceof ClassReferenceImpl)
                || (sourceElement.getParent() instanceof PhpDocTypeImpl)
                || isClassName(sourceElement)) {
            return sourceElement.getText();
        }

        return null;
    }

    private boolean isDefinition(PsiElement sourceElement) {
        if (sourceElement.getParent() != null && sourceElement.getParent().getParent() != null) {
            return ((XmlAttributeImpl) sourceElement.getParent().getParent()).getName().equals("name")
                    && ((XmlTagImpl) sourceElement.getParent().getParent().getParent()).getName().equals("transfer");
        }

        return false;
    }

    private boolean isUsage(PsiElement sourceElement) {
        if (sourceElement.getParent() != null && sourceElement.getParent().getParent() != null) {
            return ((XmlAttributeImpl) sourceElement.getParent().getParent()).getName().equals("type");
        }

        return false;
    }

    private boolean isClassName(PsiElement sourceElement) {
        if (sourceElement.getPrevSibling() == null) {
            return false;
        }

        if (sourceElement.getPrevSibling().getPrevSibling() == null) {
            return false;
        }

        return sourceElement.getPrevSibling().getPrevSibling().getText().equals("class");
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return null;
    }
}
