package com.turbinekreuzberg.plugins.contributors;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

public class PsiPathToGatewayControllerContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiElement.class), new GatewayControllerPathPsiElementProvider());
    }
}
