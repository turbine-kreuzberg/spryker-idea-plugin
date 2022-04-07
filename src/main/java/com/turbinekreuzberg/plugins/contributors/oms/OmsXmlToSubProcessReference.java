package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class OmsXmlToSubProcessReference extends PsiReferenceBase {
    public OmsXmlToSubProcessReference(@NotNull PsiElement element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public OmsXmlToSubProcessReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public OmsXmlToSubProcessReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    public OmsXmlToSubProcessReference(@NotNull PsiElement element) {
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
        PsiDirectory psiDirectory = getElement().getContainingFile().getContainingDirectory();

        String pathToXmlFile = getCanonicalText();

        if (pathToXmlFile.contains("/")) {
            String[] filePathParts = pathToXmlFile.split("/");
            String fileName = ArrayUtil.getLastElement(filePathParts);
            PsiFile[] injectorPsiFiles = FilenameIndex.getFilesByName(getElement().getProject(), fileName, getElement().getResolveScope());

            return ArrayUtil.getLastElement(injectorPsiFiles);
        }

        return psiDirectory.findFile(pathToXmlFile);
    }
}
