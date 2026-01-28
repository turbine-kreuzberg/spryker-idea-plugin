package com.turbinekreuzberg.plugins.utils;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class SprykerPathUtils {

    private static final String PATTERN_TO_FIND_SPRYKER_FILES = "(vendor\\/spryker[a-z-]*\\/[-a-z0-9]+\\/(src|tests)\\/Spryker[A-z]*\\/)";

    private static final String[] SPRYKER_NAMESPACES = {
            "SprykerShop",
            "SprykerEco",
            "Spryker",
            "SprykerSdk",
            "SprykerFeature",
            "SprykerCommunity",
    };

    public String getRelativeClassPath(@NotNull VirtualFile file) {
        String[] regexArray = file.getParent().getCanonicalPath().split(PATTERN_TO_FIND_SPRYKER_FILES);
        if (regexArray.length == 2) {
            return regexArray[1];
        }

        return "";
    }

    public boolean isLocatedInSprykerVendor(VirtualFile file) {
        return file.getParent().getCanonicalPath().matches(".*" + PATTERN_TO_FIND_SPRYKER_FILES + ".*");
    }

    public String[] getSprykerNamespaces() {
        return SPRYKER_NAMESPACES;
    }
}
