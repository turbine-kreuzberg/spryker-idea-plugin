package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeceptionHelperGotoHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) {
            return null;
        }
        
        Project project = sourceElement.getProject();
        if (!SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.CODECEPTION_HELPER_NAVIGATION)) {
            return null;
        }

        String helperClassName = resolveHelperClassName(sourceElement);
        if (helperClassName == null) {
            return null;
        }

        String mainNamespace = this.extractNamespace(sourceElement.getText());

        PsiFile[] filteredFiles = {};

        PsiFile[] files = FilenameIndex.getFilesByName(
                project,
                helperClassName,
                GlobalSearchScope.allScope(project)
        );
        for (PsiFile file : files) {
            if (file.getVirtualFile().getCanonicalPath().contains(mainNamespace)) {
                filteredFiles = ArrayUtil.append(filteredFiles, file);
            }
        }

        return filteredFiles;
    }

    private @Nullable String resolveHelperClassName(PsiElement sourceElement) {
        if (sourceElement.getContainingFile().getName().endsWith(".yml") && sourceElement.getContainingFile().getVirtualFile().getPath().contains("/tests/") && sourceElement.getText().endsWith("Helper")) {
            return StringUtils.substringAfterLast(sourceElement.getText(), "\\") + ".php";
        }

        return null;
    }

    private String extractNamespace(String input) {
        if (input.startsWith("\\")) {
            input = input.substring(1);
        }

        String[] parts = input.split("\\\\");

        return parts[0];
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
