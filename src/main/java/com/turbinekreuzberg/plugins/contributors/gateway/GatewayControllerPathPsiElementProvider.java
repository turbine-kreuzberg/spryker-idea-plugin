package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.jetbrains.annotations.NotNull;

public class GatewayControllerPathPsiElementProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        Project project = element.getProject();
        if (!SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.ZED_STUB_GATEWAY_CONTROLLER)) {
            return PsiReference.EMPTY_ARRAY;
        }

        if (element instanceof StringLiteralExpressionImpl) {
            if (element.getParent().getParent() != null && element.getParent().getParent() instanceof MethodReferenceImpl) {
                if (((MethodReferenceImpl) element.getParent().getParent()).getNameNode().getText().equals("call")) {
                    return new PsiReference[]{new PathToGatewayReference(element)};
                }
            }
        }

        return PsiReference.EMPTY_ARRAY;
    }
}
