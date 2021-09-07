package org.jetbrains.plugins.template.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.jetbrains.php.lang.PhpFileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendInPyzAction extends AnAction
{
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        // dateiname = e.getData(PlatformDataKeys.VIRTUAL_FILE).getName();
        // ordner = e.getData(PlatformDataKeys.VIRTUAL_FILE).getParent().getCanonicalPath()
        // projektordner = project.getBasePath()

        String originalPath = getClassPath(e);
        String targetPath = project.getBasePath() + "/src/Pyz/" + originalPath;

        try {
            VirtualFile pyzDirectory = VfsUtil.createDirectoryIfMissing(targetPath);
            Messages.showMessageDialog(project,"Directory added" + targetPath , "Greeting", Messages.getInformationIcon());

            final PsiFileFactory factory = PsiFileFactory.getInstance(project);
            final PsiFile file = factory.createFileFromText(e.getData(PlatformDataKeys.VIRTUAL_FILE).getName(), PhpFileType.INSTANCE, "charSequence");

            PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(pyzDirectory);
            baseDir.add(file);

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

   /* @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            e.getPresentation().setVisible(false);
            return;
        }

        if(vFile.getFileType() == UnknownFileType.INSTANCE || getClassPath(e) == "") {
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setVisible(true);
        }
    }*/
}
