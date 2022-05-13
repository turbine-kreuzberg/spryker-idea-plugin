package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

public class OmsXmlToDependencyControllerContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        if (AppSettingsState.getInstance().omsNavigationFeatureActive) {
            registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class), new OmsXmlPsiReferenceProvider());
        }
    }
}
