package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TwigMoleculeGotoHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) {
            return new PsiElement[0];
        }
        if (sourceElement.getText().equals("product-content")) {
            if (sourceElement.getPrevSibling().getPrevSibling().getPrevSibling().getText().equals("molecule")) {
                String path = "/src/Pyz/Client/Cart/CartClient.php";

                Path pyzPath = Paths.get(sourceElement.getProject().getBasePath() + path);
                VirtualFile virtualFile = VfsUtil.findFile(pyzPath, true);

                if (virtualFile != null) {
                    PsiManager psiManager = PsiManager.getInstance(sourceElement.getProject());
                    PsiElement targetFile = psiManager.findFile(virtualFile);

                    return new PsiElement[]{targetFile};
                }
                return new PsiElement[0];
            }
        }

        return new PsiElement[0];
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
