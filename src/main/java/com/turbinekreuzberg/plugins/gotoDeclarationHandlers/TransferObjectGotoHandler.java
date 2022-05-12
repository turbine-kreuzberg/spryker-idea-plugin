package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.util.ArrayUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocTypeImpl;
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransferObjectGotoHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) {
            return null;
        }

        String rawTransferObjectName = resolveTransferObjectName(sourceElement);
        if (rawTransferObjectName == null) {
            return null;
        }

        String transferObjectName = rawTransferObjectName.replace("Transfer", "").replace("[]", "");
        String searchTerm = "transfer name=\"" + transferObjectName + "\"";

        PsiFile[] psiFiles = CacheManager.getInstance(sourceElement.getProject()).getFilesWithWord(
                searchTerm,
                UsageSearchContext.ANY,
                GlobalSearchScope.allScope(sourceElement.getProject()),
                true
        );

        PsiFile[] resolvedFiles = new PsiFile[0];
        for (PsiFile psiFile:psiFiles) {
            if (psiFile.getFileType() instanceof XmlFileType && psiFile.getName().endsWith(".transfer.xml")) {
                // the already resolved files are found in a tokenized index - here we make sure that our complete search term matches
                if (psiFile.getContainingFile() != null && psiFile.getContainingFile().getText().contains(searchTerm)) {
                    resolvedFiles = ArrayUtil.append(resolvedFiles, psiFile);
                }
            }
        }

        PsiFile[] classFile = FilenameIndex.getFilesByName(
            sourceElement.getProject(),
                transferObjectName + "Transfer.php",
            GlobalSearchScope.allScope(sourceElement.getProject())
        );

        return ArrayUtil.mergeArrays(classFile, resolvedFiles);
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
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
