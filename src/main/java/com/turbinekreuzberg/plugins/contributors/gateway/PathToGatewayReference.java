package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class PathToGatewayReference extends PsiReferenceBase {
    public PathToGatewayReference(@NotNull PsiElement element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public PathToGatewayReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public PathToGatewayReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    public PathToGatewayReference(@NotNull PsiElement element) {
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
        String[] urlParts = getUrlParts();

        String moduleName = urlParts[1];
        String targetMethod = urlParts[3] + "Action";

        String[] paths = {
            AppSettingsState.getInstance().pyzDirectory + "Zed/" + StringUtils.capitalize(moduleName) + "/Communication/Controller/GatewayController.php",
            "/src/Pyz/Zed/" + StringUtils.capitalize(moduleName) + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker/" + moduleName + "/src/Spryker/Zed/" + StringUtils.capitalize(moduleName) + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker-shop/" + moduleName + "/src/Spryker/Zed/" + StringUtils.capitalize(moduleName) + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker-eco/" + moduleName + "/src/Spryker/Zed/" + StringUtils.capitalize(moduleName) + "/Communication/Controller/GatewayController.php",
        };

        for (String path:paths) {
            Path pyzPath = Paths.get(getElement().getProject().getBasePath() + path);
            VirtualFile virtualFile = VfsUtil.findFile(pyzPath, true);

            if (virtualFile != null) {
                PsiManager psiManager = PsiManager.getInstance(getElement().getProject());
                PsiFile targetFile = psiManager.findFile(virtualFile);
                Collection <MethodImpl> methodCollection = PsiTreeUtil.findChildrenOfType(targetFile, MethodImpl.class);

                for (MethodImpl method: methodCollection) {
                    if (targetMethod.equals(method.getName())) {
                        return method;
                    }
                }
            }

        }
        return null;
    }

    @NotNull
    private String[] getUrlParts() {
        String url = getCanonicalText();

        StringBuilder stringBuilder = new StringBuilder(url);

        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '-') {
                stringBuilder.deleteCharAt(i);
                stringBuilder.replace(i, i+1, String.valueOf(Character.toUpperCase(stringBuilder.charAt(i))));
            }
        }

       return stringBuilder.toString().split("/");
    }
}
