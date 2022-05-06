package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.jetbrains.twig.TwigFileType;
import com.jetbrains.twig.elements.TwigElementTypes;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TwigGotoHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) {
            return null;
        }

        if (!this.isResolvable(sourceElement)) {
            return null;
        }

        String includedFileName = sourceElement.getText();

        if (includedFileName.contains("Widget")) {
            PsiFile[] foundPhpFiles = FilenameIndex.getFilesByName(
                    sourceElement.getProject(),
                    includedFileName + ".php",
                    GlobalSearchScope.allScope(sourceElement.getProject())
            );

            String twigFileName = String.join("-", StringUtils.splitByCharacterTypeCamelCase(includedFileName.replace("Widget", ""))).toLowerCase();
            PsiFile[] foundTwigFiles = FilenameIndex.getFilesByName(
                    sourceElement.getProject(),
                    twigFileName + ".twig",
                    GlobalSearchScope.allScope(sourceElement.getProject())
            );

            return ArrayUtil.mergeArrays(foundPhpFiles, foundTwigFiles);
        }

        PsiFile[] foundTwigFiles = FilenameIndex.getFilesByName(
            sourceElement.getProject(),
            includedFileName + ".twig",
            GlobalSearchScope.allScope(sourceElement.getProject())
        );

        if (foundTwigFiles.length > 0) {
            PsiFile[] resolvedFiles = new PsiFile[0];
            for (PsiFile psiFile:foundTwigFiles) {
                if (psiFile.getContainingDirectory() != sourceElement.getContainingFile().getContainingDirectory()) {
                    resolvedFiles = ArrayUtil.append(resolvedFiles, psiFile);
                }
            }

            return resolvedFiles;
        }

        return null;
    }

    private boolean isResolvable(PsiElement sourceElement) {
        if (sourceElement.getContainingFile().getFileType() != TwigFileType.INSTANCE) {
            return false;
        }

        IElementType parentElementType = ((LeafPsiElement) sourceElement).getTreeParent().getElementType();
        IElementType[] resolvableParentElementTypes = {TwigElementTypes.INCLUDE_TAG, TwigElementTypes.EMBED_TAG, TwigElementTypes.EXTENDS_TAG, TwigElementTypes.TAG};
        if (!ArrayUtil.contains(parentElementType, resolvableParentElementTypes)) {
            return false;
        }

        if (sourceElement.getPrevSibling() == null) {
            return false;
        }

        if (sourceElement.getPrevSibling().getPrevSibling() == null) {
            return false;
        }

        if (sourceElement.getPrevSibling().getPrevSibling().getPrevSibling() == null) {
            return false;
        }

        String includedFileType = sourceElement.getPrevSibling().getPrevSibling().getPrevSibling().getText();
        String[] resolvableIncludedFileTypes = {"molecule", "atom", "organism", "view", "model", "template", "widget"};
        if (!ArrayUtil.contains(includedFileType, resolvableIncludedFileTypes)) {
            return false;
        }

        return true;
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
