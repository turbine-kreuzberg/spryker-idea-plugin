package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.turbinekreuzberg.plugins.contributors.gateway.GatewayControllerPathPsiElementProvider;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

public class PsiPathToGatewayControllerContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        if (AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive) {
            registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiElement.class), new GatewayControllerPathPsiElementProvider());
        }
    }
}
