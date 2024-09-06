package com.turbinekreuzberg.plugins.utils;

import com.intellij.psi.PsiFile;
import com.intellij.util.ResourceUtil;
import com.jetbrains.php.lang.psi.elements.impl.PhpNamespaceImpl;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class PhpContentCreator {
    public String create(PsiFile file, String relativePath, String method) {
        InputStream inputStream = ResourceUtil.getResourceAsStream(getClass().getClassLoader(), "templates", "phpClass.txt");
        String content = null;
        try {
            content = ResourceUtil.loadText(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contentWithClassName = content.replace("{{className}}", file.getVirtualFile().getNameWithoutExtension());
        String namespace = relativePath.replace("/", "\\");
        String sprykerNamespace = ((PhpNamespaceImpl) file.getFirstChild().getLastChild()).getPresentation().getPresentableText();

        contentWithClassName = contentWithClassName.replace("{{type}}", getType(file));
        contentWithClassName = contentWithClassName.replace("{{sprykerNamespace}}", sprykerNamespace);
        contentWithClassName = contentWithClassName.replace("{{method}}", method);

        return contentWithClassName.replace("{{namespace}}", AppSettingsState.getInstance().pyzNamespace + "\\" + namespace);
    }

    @NotNull
    private String getType(@NotNull PsiFile file) {
        PhpClassExtractor phpClassExtractor = new PhpClassExtractor();

        if (phpClassExtractor.extractClass(file).isInterface()) {
            return "interface";
        }

        if (phpClassExtractor.extractClass(file).isAbstract()) {
            return "abstract class";
        }

        if (phpClassExtractor.extractClass(file).isTrait()) {
            return "trait";
        }

        return "class";
    }
}
