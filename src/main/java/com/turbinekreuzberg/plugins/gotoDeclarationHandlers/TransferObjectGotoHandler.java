package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.cache.CacheManager;
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

        if (!this.isResolvable(sourceElement)) {
            return null;
        }

        String searchTerm = sourceElement.getText().replace("Transfer", "");
        PsiFile[] psiFiles = CacheManager.getInstance(sourceElement.getProject()).getFilesWithWord(
                "transfer name=\"" + searchTerm + "\"",
                UsageSearchContext.ANY,
                GlobalSearchScope.allScope(sourceElement.getProject()),
                false
        );

        PsiFile[] resolvedFiles = new PsiFile[0];
        for (PsiFile psiFile:psiFiles) {
            if (psiFile.getFileType() instanceof XmlFileType && psiFile.getName().endsWith(".transfer.xml")) {
                resolvedFiles = ArrayUtil.append(resolvedFiles, psiFile);
            }
        }

        PsiFile[] classFile = FilenameIndex.getFilesByName(
            sourceElement.getProject(),
            sourceElement.getText() + ".php",
            GlobalSearchScope.allScope(sourceElement.getProject())
        );

        return ArrayUtil.mergeArrays(classFile, resolvedFiles);
    }

    private boolean isResolvable(PsiElement sourceElement) {
        if (sourceElement.getContainingFile().getFileType() != PhpFileType.INSTANCE) {
            return false;
        }

        if (sourceElement.getText().equals("AbstractTransfer")) {
            return false;
        }

        if (!sourceElement.getText().endsWith("Transfer")) {
            return false;
        }

        if (sourceElement.getParent() == null) {
            return false;
        }

        if ((sourceElement.getParent() instanceof ClassReferenceImpl) || (sourceElement.getParent() instanceof PhpDocTypeImpl) || (sourceElement.getPrevSibling().getPrevSibling().getText().equals("class"))) {
            return true;
        }

        return false;
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
