package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

public class OmsXmlToDependencyControllerContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // Register the provider unconditionally - it will check the project settings when used
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlAttributeValue.class), 
            new OmsXmlPsiReferenceProvider()
        );
    }
}
