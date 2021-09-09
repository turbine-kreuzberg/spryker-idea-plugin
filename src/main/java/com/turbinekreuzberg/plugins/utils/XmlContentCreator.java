package com.turbinekreuzberg.plugins.utils;

import com.intellij.psi.PsiFile;

public class XmlContentCreator {
    public String create(PsiFile file) {
        return file.getText();
    }
}
