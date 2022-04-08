package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        String xmlFilePath = this.getXmlFilePath();

        if (xmlFilePath.contains("/")) {
            String[] filePathParts = xmlFilePath.split("/");
            String fileName = ArrayUtil.getLastElement(filePathParts);
            PsiFile[] psiFiles = FilenameIndex.getFilesByName(getElement().getProject(), fileName, getElement().getResolveScope());

            return ArrayUtil.getLastElement(psiFiles);
        }

        return psiDirectory.findFile(xmlFilePath);
    }

    private String getXmlFilePath() {
        if (!getCanonicalText().contains(".xml")) {
            return getCanonicalText() + ".xml";
        }

        return getCanonicalText();
    }
}
