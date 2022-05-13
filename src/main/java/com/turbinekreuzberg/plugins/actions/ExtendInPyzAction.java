package com.turbinekreuzberg.plugins.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.jetbrains.php.lang.PhpFileType;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import com.turbinekreuzberg.plugins.utils.SprykerRelativeClassPathCreator;
import com.turbinekreuzberg.plugins.utils.PhpContentCreator;
import com.turbinekreuzberg.plugins.utils.GenericContentCreator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendInPyzAction extends AnAction {

    SprykerRelativeClassPathCreator sprykerRelativeClassPathCreator;
    public ExtendInPyzAction() {
        sprykerRelativeClassPathCreator = new SprykerRelativeClassPathCreator();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();

        VirtualFile[] selectedVirtualFiles = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);

        assert selectedVirtualFiles != null;

        for (VirtualFile selectedVirtualFile : selectedVirtualFiles) {
            processFile(project, selectedVirtualFile);
        }
    }

    private void processFile(Project project, VirtualFile selectedVirtualFile) {
        String relativeClassPath = sprykerRelativeClassPathCreator.getRelativeClassPath(selectedVirtualFile);
        String targetPath = project.getBasePath() + AppSettingsState.getInstance().pyzDirectory + relativeClassPath;

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile selectedFile = psiManager.findFile(selectedVirtualFile);

        String renderedContent = getRenderedContent(selectedFile, relativeClassPath);
        PsiFile pyzFile = createFile(selectedVirtualFile, project, renderedContent);

        ApplicationManager.getApplication().runWriteAction(() -> {
            VirtualFile virtualPyzDirectory = null;
            try {
                virtualPyzDirectory = VfsUtil.createDirectoryIfMissing(targetPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (virtualPyzDirectory != null) {
                PsiDirectory pyzDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(virtualPyzDirectory);
                if (pyzDirectory.findFile(pyzFile.getName()) == null) {
                    pyzDirectory.add(pyzFile);
                }
                findFileInDirectoryAndOpenInEditor(project, pyzDirectory, pyzFile.getName());
            }
        });
    }

    private void findFileInDirectoryAndOpenInEditor(Project project, PsiDirectory pyzDirectory, String fileName) {
        PsiFile foundFile = pyzDirectory.findFile(fileName);
        new OpenFileDescriptor(project, foundFile.getVirtualFile()).navigate(true);
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        if (!AppSettingsState.getInstance().extendInPyzFeatureActive) {
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
    private PsiFile createFile(@NotNull VirtualFile sourceFile, Project project, String renderedContent) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        return factory.createFileFromText(sourceFile.getName(), PhpFileType.INSTANCE, renderedContent);
    }

    @NotNull
    private String getRenderedContent(@NotNull PsiFile file, String relativePath) {
        if (file.getFileType() == PhpFileType.INSTANCE) {
            return new PhpContentCreator().create(file, relativePath);
        }

        return new GenericContentCreator().create(file);
    }
}
