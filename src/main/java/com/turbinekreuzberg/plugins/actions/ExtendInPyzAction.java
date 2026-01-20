package com.turbinekreuzberg.plugins.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import com.turbinekreuzberg.plugins.utils.SprykerRelativeClassPathCreator;
import com.turbinekreuzberg.plugins.utils.PhpContentCreator;
import com.turbinekreuzberg.plugins.utils.GenericContentCreator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendInPyzAction extends AnAction {

    SprykerRelativeClassPathCreator sprykerRelativeClassPathCreator;
    public ExtendInPyzAction() {
        sprykerRelativeClassPathCreator = new SprykerRelativeClassPathCreator();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();

        String selectedMethod = "";
        if (actionEvent.getData(PlatformDataKeys.PSI_ELEMENT) instanceof MethodImpl) {
            selectedMethod = actionEvent.getData(PlatformDataKeys.PSI_ELEMENT).getText();
        }

        VirtualFile[] selectedVirtualFiles = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);

        assert selectedVirtualFiles != null;

        for (VirtualFile selectedVirtualFile : selectedVirtualFiles) {
            processFile(project, selectedVirtualFile, selectedMethod);
        }
    }

    private void processFile(Project project, VirtualFile selectedVirtualFile, String method) {
        String relativeClassPath = sprykerRelativeClassPathCreator.getRelativeClassPath(selectedVirtualFile);

        String pyzDirectoryPath;
        if (selectedVirtualFile.getPath().contains("/tests/")) {
            pyzDirectoryPath = SettingsManager.getPyzTestDirectory(project);
        } else {
            pyzDirectoryPath = SettingsManager.getPyzDirectory(project);
        }

        String targetPath = project.getBasePath() + pyzDirectoryPath + relativeClassPath;

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile selectedFile = psiManager.findFile(selectedVirtualFile);

        String renderedContent = getRenderedContent(selectedFile, relativeClassPath, method);
        PsiFile pyzFile = createFile(selectedVirtualFile.getName(), project, renderedContent);

        ApplicationManager.getApplication().runWriteAction(() -> {
            VirtualFile virtualPyzDirectory = null;
            try {
                virtualPyzDirectory = VfsUtil.createDirectoryIfMissing(targetPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (virtualPyzDirectory != null) {
                PsiDirectory pyzDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(virtualPyzDirectory);
                PsiFile existingFile = pyzDirectory.findFile(pyzFile.getName());
                if (existingFile == null) {
                    pyzDirectory.add(pyzFile);
                } else if (!method.isEmpty()) {
                    addToFile(project, method, existingFile, pyzDirectory);
                }

                findFileInDirectoryAndOpenInEditor(project, pyzDirectory, pyzFile.getName());
            }
        });
    }

    private void addToFile(Project project, String method, PsiFile existingFile, PsiDirectory pyzDirectory) {
        Method phpmethod = PhpPsiElementFactory.createMethod(project, method);
        if (existingFile.getText().contains("function " + phpmethod.getName())) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = PsiDocumentManager.getInstance(project).getDocument(existingFile);
            if (document != null) {
                int offset = StringUtils.substringBeforeLast(existingFile.getText(), "}").length();
                document.insertString(offset, method);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            }

            CodeStyleManager.getInstance(project).reformat(existingFile);
        });
    }

    private void findFileInDirectoryAndOpenInEditor(Project project, PsiDirectory pyzDirectory, String fileName) {
        PsiFile foundFile = pyzDirectory.findFile(fileName);
        new OpenFileDescriptor(project, foundFile.getVirtualFile(), foundFile.getFileDocument().getLineCount(), 0).navigate(true);
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null || !SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.EXTEND_IN_PYZ)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        VirtualFile vFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        if (isNotFileInSprykerVendor(vFile)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        actionEvent.getPresentation().setVisible(true);
    }

    private boolean isNotFileInSprykerVendor(@NotNull VirtualFile vFile) {
        return vFile.getFileType() == UnknownFileType.INSTANCE || !sprykerRelativeClassPathCreator.isLocatedInSprykerVendor(vFile);
    }

    @NotNull
    private PsiFile createFile(@NotNull String fileName, Project project, String renderedContent) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        return factory.createFileFromText(fileName, PhpFileType.INSTANCE, renderedContent);
    }

    @NotNull
    private String getRenderedContent(@NotNull PsiFile file, String relativePath, String method) {
        if (file.getFileType() == PhpFileType.INSTANCE) {
            return new PhpContentCreator().create(file, relativePath, method);
        }

        return new GenericContentCreator().create(file);
    }
}
