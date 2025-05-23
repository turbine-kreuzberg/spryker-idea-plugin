package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.regex.Pattern;

public class PathToGatewayReference extends PsiReferenceBase<PsiElement> {
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
        Project project = getElement().getProject();
        String packageName = getPackageName();
        String moduleName = getModuleName();
        String targetMethodName = getTargetMethodName();

        String[] paths = {
            SettingsManager.getPyzDirectory(project) + "Zed/" + moduleName + "/Communication/Controller/GatewayController.php",
            "/src/Pyz/Zed/" + moduleName + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker/" + packageName + "/src/Spryker/Zed/" + moduleName + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker-shop/" + packageName + "/src/Spryker/Zed/" + moduleName + "/Communication/Controller/GatewayController.php",
            "/vendor/spryker-eco/" + packageName + "/src/Spryker/Zed/" + moduleName + "/Communication/Controller/GatewayController.php",
        };

        for (String path:paths) {
            Path pyzPath = Paths.get(project.getBasePath() + path);
            VirtualFile virtualFile = VfsUtil.findFile(pyzPath, true);

            if (virtualFile != null) {
                PsiManager psiManager = PsiManager.getInstance(project);
                PsiFile targetFile = psiManager.findFile(virtualFile);
                Collection <MethodImpl> methodCollection = PsiTreeUtil.findChildrenOfType(targetFile, MethodImpl.class);

                for (MethodImpl method: methodCollection) {
                    if (targetMethodName.equals(method.getName())) {
                        return method;
                    }
                }
            }

        }
        return null;
    }

    @NotNull
    private String getPackageName() {
        String[] urlParts = getCanonicalText().split("/");

        return urlParts[1];
    }

    @NotNull
    private String getModuleName() {
        String camelCasedPackageName = convertKebabCaseToCamelCase(getPackageName());

        return StringUtils.capitalize(camelCasedPackageName);
    }

    @NotNull
    private String getTargetMethodName() {
        String[] urlParts = getCanonicalText().split("/");
        String camelCasedTargetMethodName = convertKebabCaseToCamelCase(urlParts[3]);

        return camelCasedTargetMethodName + "Action";
    }

    @NotNull
    private String convertKebabCaseToCamelCase(String kebabCasedString) {
        return Pattern.compile("-([a-z])")
                .matcher(kebabCasedString)
                .replaceAll(mr -> mr.group(1).toUpperCase());
    }
}
