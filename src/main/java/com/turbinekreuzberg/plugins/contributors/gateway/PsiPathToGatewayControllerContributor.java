package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.turbinekreuzberg.plugins.contributors.gateway.GatewayControllerPathPsiElementProvider;
import org.jetbrains.annotations.NotNull;

public class PsiPathToGatewayControllerContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // Register the provider unconditionally - it will check the project settings when used
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(PsiElement.class), 
            new GatewayControllerPathPsiElementProvider()
        );
    }
}
