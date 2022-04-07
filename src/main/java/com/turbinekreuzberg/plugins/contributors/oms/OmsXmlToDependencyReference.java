package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class OmsXmlToDependencyReference extends PsiReferenceBase {
    public OmsXmlToDependencyReference(@NotNull PsiElement element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public OmsXmlToDependencyReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public OmsXmlToDependencyReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    public OmsXmlToDependencyReference(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public @NotNull TextRange getAbsoluteRange() {
        return super.getAbsoluteRange();
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }

    @Override
    public @Nullable PsiElement resolve() {
        String commandName = "'" + getCanonicalText() + "'";

        String filePath = AppSettingsState.getInstance().pyzDirectory + "Zed/Oms/OmsDependencyProvider.php";
        Path pyzPath = Paths.get(getElement().getProject().getBasePath() + filePath);
        VirtualFile virtualFile = VfsUtil.findFile(pyzPath, true);

        if (virtualFile != null) {
            PsiManager psiManager = PsiManager.getInstance(getElement().getProject());
            PsiFile targetFile = psiManager.findFile(virtualFile);
            StringLiteralExpressionImpl result = this.searchForUsageInFile(commandName, targetFile);
            if (result != null) {
                return result;
            }
        }

        PsiFile[] injectorPsiFiles =  FilenameIndex.getFilesByName(getElement().getProject(), "OmsDependencyInjector.php", getElement().getResolveScope());
        for (PsiFile injectorPsiFile:injectorPsiFiles) {
            StringLiteralExpressionImpl result = this.searchForUsageInFile(commandName, injectorPsiFile);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private @Nullable StringLiteralExpressionImpl searchForUsageInFile(String searchString, PsiFile targetFile) {
        Collection <StringLiteralExpressionImpl> usageCollection = PsiTreeUtil.findChildrenOfType(targetFile, StringLiteralExpressionImpl.class);
        for (StringLiteralExpressionImpl usage: usageCollection) {
            if (searchString.equals(usage.getText())) {
                return usage;
            }
        }

        return null;
    }
}
