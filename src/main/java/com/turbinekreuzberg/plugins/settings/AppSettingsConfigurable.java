package com.turbinekreuzberg.plugins.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PYZ Plugin";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getPyzDirectoryText().equals(settings.pyzDirectory);
        modified |= !mySettingsComponent.getPyzNamespaceText().equals(settings.pyzNamespace);
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.pyzDirectory = mySettingsComponent.getPyzDirectoryText();
        settings.pyzNamespace = mySettingsComponent.getPyzNamespaceText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setPyzDirectoryText(settings.pyzDirectory);
        mySettingsComponent.setPyzNamespaceText(settings.pyzNamespace);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
