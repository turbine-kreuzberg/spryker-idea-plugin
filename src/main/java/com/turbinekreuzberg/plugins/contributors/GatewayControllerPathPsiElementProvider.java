package com.turbinekreuzberg.plugins.contributors;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;

public class GatewayControllerPathPsiElementProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

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
