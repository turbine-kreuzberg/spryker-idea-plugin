package com.turbinekreuzberg.plugins.actions;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.jetbrains.php.lang.PhpFileType;
import com.turbinekreuzberg.plugins.utils.PhpContentCreator;
import com.turbinekreuzberg.plugins.utils.XmlContentCreator;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class ExtendInPyzAction extends AnAction
{
    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();

        VirtualFile selectedVirtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        String relativeClassPath = getRelativeClassPath(selectedVirtualFile);
        String targetPath = project.getBasePath() + "/src/Pyz/" + relativeClassPath;

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile selectedFile = psiManager.findFile(selectedVirtualFile);

        String renderedContent = getRenderedContent(selectedFile, relativeClassPath);
        PsiFile pyzFile = createFile(selectedVirtualFile, project, renderedContent);

        try {
            VirtualFile virtualPyzDirectory = VfsUtil.createDirectoryIfMissing(targetPath);
            PsiDirectory pyzDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(virtualPyzDirectory);

            if (pyzDirectory.findFile(pyzFile.getName()) == null) {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    pyzDirectory.add(pyzFile);
                });
            }

            findFileInDirectoryAndOpenInEditor(project, pyzDirectory, pyzFile.getName());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void findFileInDirectoryAndOpenInEditor(Project project, PsiDirectory pyzDirectory, String fileName) {
        PsiFile foundFile = pyzDirectory.findFile(fileName);
        new OpenFileDescriptor(project, foundFile.getVirtualFile()).navigate(true);
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        VirtualFile vFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        if(isFileInSprykerVendor(vFile)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        actionEvent.getPresentation().setVisible(true);
    }

    private boolean isFileInSprykerVendor(@NotNull VirtualFile vFile) {
        return vFile.getFileType() == UnknownFileType.INSTANCE || getRelativeClassPath(vFile) == "";
    }

    @NotNull
    private PsiFile createFile(@NotNull VirtualFile sourceFile, Project project, String renderedContent) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        return factory.createFileFromText(sourceFile.getName(), PhpFileType.INSTANCE, renderedContent);
    }

    @NotNull
    private String getRenderedContent(@NotNull PsiFile file, String relativePath) {
        if (file.getFileType() == XmlFileType.INSTANCE) {
            return new XmlContentCreator().create(file);
        }

        return new PhpContentCreator().create(file, relativePath);
    }

    private String getRelativeClassPath(@NotNull VirtualFile file) {
        String[] regexArray = file.getParent().getCanonicalPath().split("(vendor\\/spryker[a-z-]*\\/[a-z-]*\\/src\\/Spryker[A-z]*\\/)");
        if(regexArray.length == 2) {
            return regexArray[1];
        }

        return "";
    }
}
