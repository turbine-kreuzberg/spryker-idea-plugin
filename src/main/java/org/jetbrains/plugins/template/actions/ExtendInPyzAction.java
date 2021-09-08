package org.jetbrains.plugins.template.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.util.ResourceUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFileImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpNamespaceImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ExtendInPyzAction extends AnAction
{
    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();

        VirtualFile currentFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        String originalPath = getClassPath(currentFile);
        String targetPath = project.getBasePath() + "/src/Pyz/" + originalPath;

        String renderedContent = getRenderedContent(currentFile, originalPath, project);
        PsiFile pyzFile = createFile(currentFile, project, renderedContent);

        try {
            VirtualFile virtualPyzDirectory = VfsUtil.createDirectoryIfMissing(targetPath);
            PsiDirectory pyzDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(virtualPyzDirectory);
            pyzDirectory.add(pyzFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

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
        return vFile.getFileType() == UnknownFileType.INSTANCE || getClassPath(vFile) == "";
    }

    @NotNull
    private PsiFile createFile(@NotNull VirtualFile sourceFile, Project project, String renderedContent) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        return factory.createFileFromText(sourceFile.getName(), PhpFileType.INSTANCE, renderedContent);
    }

    @NotNull
    private String getRenderedContent(@NotNull VirtualFile file, String originalPath, Project project) {
        InputStream inputStream = ResourceUtil.getResourceAsStream(getClass().getClassLoader(), "templates", "phpClass.txt");
        String content = null;
        try {
            content = ResourceUtil.loadText(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contentWithClassName = content.replace("{{className}}", file.getNameWithoutExtension());
        String namespace = originalPath.replace("/", "\\");

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile originalFile = psiManager.findFile(file);

        String sprykerNamespace = ((PhpNamespaceImpl) ((PhpFileImpl) originalFile).getFirstChild().getLastChild()).getPresentation().getPresentableText();

        contentWithClassName = contentWithClassName.replace("{{sprykerNamespace}}", sprykerNamespace);

        return contentWithClassName.replace("{{namespace}}", namespace);
    }

    private String getClassPath(@NotNull VirtualFile file) {
        String[] regexArray = file.getParent().getCanonicalPath().split("(vendor\\/spryker[a-z-]*\\/[a-z-]*\\/src\\/Spryker[A-z]*\\/)");
        if(regexArray.length == 2) {
            return regexArray[1];
        }

        return "";
    }
}
