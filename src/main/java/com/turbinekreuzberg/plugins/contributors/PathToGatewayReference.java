package com.turbinekreuzberg.plugins.contributors;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
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
    public @Nullable PsiElement resolve() {
        String url = getCanonicalText();

        StringBuilder stringBuilder = new StringBuilder(url);

        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '-') {
                stringBuilder.deleteCharAt(i);
                stringBuilder.replace(i, i+1, String.valueOf(Character.toUpperCase(stringBuilder.charAt(i))));
            }
        }

        String[] urlParts = stringBuilder.toString().split("/");

        String sprykerModule = urlParts[1];
        String method = urlParts[3] + "Action";

        String[] paths = {
                "/src/Pyz/Zed/" + StringUtils.capitalize(sprykerModule) + "/Communication/Controller/GatewayController.php",
                "/vendor/spryker/" + sprykerModule + "/src/Spryker/Zed/" + StringUtils.capitalize(sprykerModule) + "/Communication/Controller/GatewayController.php",
                "/vendor/spryker-shop/" + sprykerModule + "/src/Spryker/Zed/" + StringUtils.capitalize(sprykerModule) + "/Communication/Controller/GatewayController.php",
                "/vendor/spryker-eco/" + sprykerModule + "/src/Spryker/Zed/" + StringUtils.capitalize(sprykerModule) + "/Communication/Controller/GatewayController.php",
        };

        for (String path:paths) {
            Path pyzPath = Paths.get(getElement().getProject().getBasePath() + path);
            VirtualFile virtualFile = VfsUtil.findFile(pyzPath, true);


            if (virtualFile != null) {
                PsiManager psiManager = PsiManager.getInstance(getElement().getProject());
                PsiFile selectedFile = psiManager.findFile(virtualFile);
                Collection <MethodImpl> dingens = PsiTreeUtil.findChildrenOfType(selectedFile, MethodImpl.class);

                for (MethodImpl ding: dingens) {
                    if (method.equals(ding.getName())) {
                        return ding;
                    }
                }

                return selectedFile;
            }

        }
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }
}
