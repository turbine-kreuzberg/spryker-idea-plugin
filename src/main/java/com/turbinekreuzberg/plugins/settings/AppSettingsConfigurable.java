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
        return "PYZ Plugin Settings";
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
        modified |= !mySettingsComponent.getPyzTestDirectoryText().equals(settings.pyzTestDirectory);
        modified |= !mySettingsComponent.getPyzTestNamespaceText().equals(settings.pyzTestNamespace);
        modified |= mySettingsComponent.getExtendInPyzFeatureActive() != settings.extendInPyzFeatureActive;
        modified |= mySettingsComponent.getViewOnGithubFeatureActive() != settings.viewOnGithubFeatureActive;
        modified |= mySettingsComponent.getGoToParentFeatureActive() != settings.goToParentFeatureActive;
        modified |= mySettingsComponent.getZedStubGatewayControllerFeatureActive() != settings.zedStubGatewayControllerFeatureActive;
        modified |= mySettingsComponent.getOmsNavigationFeatureActive() != settings.omsNavigationFeatureActive;
        modified |= mySettingsComponent.getTwigGotoHandlingFeatureActive() != settings.twigGotoHandlingFeatureActive;
        modified |= mySettingsComponent.getTransferObjectGotoHandlingFeatureActive() != settings.transferObjectGotoHandlingFeatureActive;
        modified |= mySettingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox() != settings.codeceptionHelperNavigationFeatureActive;
        modified |= mySettingsComponent.getTwigGlossaryKeyGotoHandlingFeatureActiveCheckbox() != settings.twigGlossaryKeyGotoHandlingFeatureActive;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.pyzDirectory = mySettingsComponent.getPyzDirectoryText();
        settings.pyzNamespace = mySettingsComponent.getPyzNamespaceText();
        settings.pyzTestDirectory = mySettingsComponent.getPyzTestDirectoryText();
        settings.pyzTestNamespace = mySettingsComponent.getPyzTestNamespaceText();
        settings.extendInPyzFeatureActive = mySettingsComponent.getExtendInPyzFeatureActive();
        settings.viewOnGithubFeatureActive = mySettingsComponent.getViewOnGithubFeatureActive();
        settings.goToParentFeatureActive = mySettingsComponent.getGoToParentFeatureActive();
        settings.zedStubGatewayControllerFeatureActive = mySettingsComponent.getZedStubGatewayControllerFeatureActive();
        settings.omsNavigationFeatureActive = mySettingsComponent.getOmsNavigationFeatureActive();
        settings.twigGotoHandlingFeatureActive = mySettingsComponent.getTwigGotoHandlingFeatureActive();
        settings.transferObjectGotoHandlingFeatureActive = mySettingsComponent.getTransferObjectGotoHandlingFeatureActive();
        settings.codeceptionHelperNavigationFeatureActive = mySettingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox();
        settings.twigGlossaryKeyGotoHandlingFeatureActive = mySettingsComponent.getTwigGlossaryKeyGotoHandlingFeatureActiveCheckbox();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setPyzDirectoryText(settings.pyzDirectory);
        mySettingsComponent.setPyzNamespaceText(settings.pyzNamespace);
        mySettingsComponent.setPyzTestDirectoryText(settings.pyzTestDirectory);
        mySettingsComponent.setPyzTestNamespaceText(settings.pyzTestNamespace);
        mySettingsComponent.setExtendInPyzFeatureActive(settings.extendInPyzFeatureActive);
        mySettingsComponent.setViewOnGithubFeatureActive(settings.viewOnGithubFeatureActive);
        mySettingsComponent.setGoToParentFeatureActive(settings.goToParentFeatureActive);
        mySettingsComponent.setZedStubGatewayControllerFeatureActive(settings.zedStubGatewayControllerFeatureActive);
        mySettingsComponent.setOmsNavigationFeatureActive(settings.omsNavigationFeatureActive);
        mySettingsComponent.setTwigGotoHandlingFeatureActive(settings.twigGotoHandlingFeatureActive);
        mySettingsComponent.setTransferObjectGotoHandlingFeatureActive(settings.transferObjectGotoHandlingFeatureActive);
        mySettingsComponent.setCodeceptionHelperNavigationFeatureActiveCheckbox(settings.codeceptionHelperNavigationFeatureActive);
        mySettingsComponent.setTwigGlossaryKeyGotoHandlingFeatureActiveCheckbox(settings.twigGlossaryKeyGotoHandlingFeatureActive);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
