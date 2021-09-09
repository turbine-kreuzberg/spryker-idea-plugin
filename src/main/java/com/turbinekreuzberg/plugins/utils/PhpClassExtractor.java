package com.turbinekreuzberg.plugins.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.SmartList;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PhpClassExtractor {

    @NotNull
    public PhpClassImpl extractClass(PsiFile pyzFile) {

        final Collection<PsiElement> selectedElementClass = new SmartList<>();

        pyzFile.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PhpClassImpl) {
                    selectedElementClass.add(element);
                }
                super.visitElement(element);
            }
        });
        return (PhpClassImpl) ((SmartList) selectedElementClass).get(0);
    }
}