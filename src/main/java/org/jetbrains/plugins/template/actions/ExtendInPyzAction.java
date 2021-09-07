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

        String[] array = e.getData(PlatformDataKeys.VIRTUAL_FILE).getParent().getCanonicalPath().split("/");

        int position = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].compareTo("vendor") == 0) {
                position = i;
            }
        }

        String targetPath = "/src/Pyz/";
        for (int i = position + 5; i < array.length; i++) {
            targetPath += array[i] + "/";
        }

        try {
            VirtualFile myDir = VfsUtil.createDirectoryIfMissing(project.getBasePath() + targetPath);
            Messages.showMessageDialog(project,"Directory added" + targetPath , "Greeting", Messages.getInformationIcon());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
