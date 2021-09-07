package org.jetbrains.plugins.template.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtendInPyzAction extends AnAction
{
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        final PsiFileFactory factory = PsiFileFactory.getInstance(project);
        final PsiFile file = factory.createFileFromText("test.txt", "charSequence");


        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        // baseDir.add(file);

        // dateiname = e.getData(PlatformDataKeys.VIRTUAL_FILE).getName();
        // ordner = e.getData(PlatformDataKeys.VIRTUAL_FILE).getParent().getCanonicalPath()
        // projektordner = project.getBasePath()

        String originalPath = getClassPath(e);
        String targetPath = project.getBasePath() + "/src/Pyz/" + originalPath;

        try {
            VirtualFile myDir = VfsUtil.createDirectoryIfMissing(targetPath);
            Messages.showMessageDialog(project,"Directory added" + targetPath , "Greeting", Messages.getInformationIcon());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getClassPath(@NotNull AnActionEvent e) {
        String[] regexArray = e.getData(PlatformDataKeys.VIRTUAL_FILE).getParent().getCanonicalPath().split("(vendor\\/spryker[a-z-]*\\/[a-z-]*\\/src\\/Spryker[A-z]*\\/)");
        if(regexArray.length == 2) {
            return regexArray[1];
        }

        return "";
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if( vFile.getFileType() == null && getClassPath(e) == "") {
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setVisible(true);
        }
    }
}
