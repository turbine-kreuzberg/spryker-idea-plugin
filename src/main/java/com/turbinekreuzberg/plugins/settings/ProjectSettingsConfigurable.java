package com.turbinekreuzberg.plugins.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Project-level settings configurable for the PYZ Plugin.
 */
public class ProjectSettingsConfigurable implements Configurable {
    private ProjectSettingsComponent mySettingsComponent;
    private final Project myProject;

    public ProjectSettingsConfigurable(Project project) {
        myProject = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PYZ Plugin Project Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new ProjectSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(myProject);
        
        boolean modified = mySettingsComponent.getUseProjectSettings() != settings.useProjectSettings;
        
        // Only check other settings if project settings are enabled
        if (mySettingsComponent.getUseProjectSettings()) {
            // For text fields, we need to handle potential null values
            if (settings.pyzDirectory != null) {
                modified |= !mySettingsComponent.getPyzDirectoryText().equals(settings.pyzDirectory);
            } else {
                modified |= !mySettingsComponent.getPyzDirectoryText().isEmpty();
            }
            
            if (settings.pyzNamespace != null) {
                modified |= !mySettingsComponent.getPyzNamespaceText().equals(settings.pyzNamespace);
            } else {
                modified |= !mySettingsComponent.getPyzNamespaceText().isEmpty();
            }

            if (settings.pyzTestDirectory != null) {
                modified |= !mySettingsComponent.getPyzTestDirectoryText().equals(settings.pyzTestDirectory);
            } else {
                modified |= !mySettingsComponent.getPyzTestDirectoryText().isEmpty();
            }

            if (settings.pyzTestNamespace != null) {
                modified |= !mySettingsComponent.getPyzTestNamespaceText().equals(settings.pyzTestNamespace);
            } else {
                modified |= !mySettingsComponent.getPyzTestNamespaceText().isEmpty();
            }
            
            // For boolean settings, we need to handle potential null values
            if (settings.extendInPyzFeatureActive != null) {
                modified |= mySettingsComponent.getExtendInPyzFeatureActive() != settings.extendInPyzFeatureActive;
            } else {
                modified |= mySettingsComponent.getExtendInPyzFeatureActive() != AppSettingsState.getInstance().extendInPyzFeatureActive;
            }
            
            if (settings.viewOnGithubFeatureActive != null) {
                modified |= mySettingsComponent.getViewOnGithubFeatureActive() != settings.viewOnGithubFeatureActive;
            } else {
                modified |= mySettingsComponent.getViewOnGithubFeatureActive() != AppSettingsState.getInstance().viewOnGithubFeatureActive;
            }
            
            if (settings.zedStubGatewayControllerFeatureActive != null) {
                modified |= mySettingsComponent.getZedStubGatewayControllerFeatureActive() != settings.zedStubGatewayControllerFeatureActive;
            } else {
                modified |= mySettingsComponent.getZedStubGatewayControllerFeatureActive() != AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive;
            }
            
            if (settings.omsNavigationFeatureActive != null) {
                modified |= mySettingsComponent.getOmsNavigationFeatureActive() != settings.omsNavigationFeatureActive;
            } else {
                modified |= mySettingsComponent.getOmsNavigationFeatureActive() != AppSettingsState.getInstance().omsNavigationFeatureActive;
            }
            
            if (settings.twigGotoHandlingFeatureActive != null) {
                modified |= mySettingsComponent.getTwigGotoHandlingFeatureActive() != settings.twigGotoHandlingFeatureActive;
            } else {
                modified |= mySettingsComponent.getTwigGotoHandlingFeatureActive() != AppSettingsState.getInstance().twigGotoHandlingFeatureActive;
            }
            
            if (settings.transferObjectGotoHandlingFeatureActive != null) {
                modified |= mySettingsComponent.getTransferObjectGotoHandlingFeatureActive() != settings.transferObjectGotoHandlingFeatureActive;
            } else {
                modified |= mySettingsComponent.getTransferObjectGotoHandlingFeatureActive() != AppSettingsState.getInstance().transferObjectGotoHandlingFeatureActive;
            }
            
            if (settings.codeceptionHelperNavigationFeatureActive != null) {
                modified |= mySettingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox() != settings.codeceptionHelperNavigationFeatureActive;
            } else {
                modified |= mySettingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox() != AppSettingsState.getInstance().codeceptionHelperNavigationFeatureActive;
            }
        }
        
        return modified;
    }

    @Override
    public void apply() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(myProject);
        
        // Update whether project settings are enabled
        settings.useProjectSettings = mySettingsComponent.getUseProjectSettings();
        
        // Only update other settings if project settings are enabled
        if (settings.useProjectSettings) {
            settings.pyzDirectory = mySettingsComponent.getPyzDirectoryText();
            settings.pyzNamespace = mySettingsComponent.getPyzNamespaceText();
            settings.pyzTestDirectory = mySettingsComponent.getPyzTestDirectoryText();
            settings.pyzTestNamespace = mySettingsComponent.getPyzTestNamespaceText();
            settings.extendInPyzFeatureActive = mySettingsComponent.getExtendInPyzFeatureActive();
            settings.viewOnGithubFeatureActive = mySettingsComponent.getViewOnGithubFeatureActive();
            settings.zedStubGatewayControllerFeatureActive = mySettingsComponent.getZedStubGatewayControllerFeatureActive();
            settings.omsNavigationFeatureActive = mySettingsComponent.getOmsNavigationFeatureActive();
            settings.twigGotoHandlingFeatureActive = mySettingsComponent.getTwigGotoHandlingFeatureActive();
            settings.transferObjectGotoHandlingFeatureActive = mySettingsComponent.getTransferObjectGotoHandlingFeatureActive();
            settings.codeceptionHelperNavigationFeatureActive = mySettingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox();
        } else {
            // If project settings are disabled, reset all project-specific settings to null
            // This ensures we fall back to application settings
            settings.pyzDirectory = null;
            settings.pyzNamespace = null;
            settings.pyzTestDirectory = null;
            settings.pyzTestNamespace = null;
            settings.extendInPyzFeatureActive = null;
            settings.viewOnGithubFeatureActive = null;
            settings.zedStubGatewayControllerFeatureActive = null;
            settings.omsNavigationFeatureActive = null;
            settings.twigGotoHandlingFeatureActive = null;
            settings.transferObjectGotoHandlingFeatureActive = null;
            settings.codeceptionHelperNavigationFeatureActive = null;
        }
    }

    @Override
    public void reset() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(myProject);
        
        // First set whether project settings are enabled
        mySettingsComponent.setUseProjectSettings(settings.useProjectSettings);
        
        // Set text fields
        if (settings.pyzDirectory != null) {
            mySettingsComponent.setPyzDirectoryText(settings.pyzDirectory);
        } else {
            // Use the application setting as default
            AppSettingsState appSettings = AppSettingsState.getInstance();
            mySettingsComponent.setPyzDirectoryText(appSettings.pyzDirectory);
        }
        
        if (settings.pyzNamespace != null) {
            mySettingsComponent.setPyzNamespaceText(settings.pyzNamespace);
        } else {
            // Use the application setting as default
            AppSettingsState appSettings = AppSettingsState.getInstance();
            mySettingsComponent.setPyzNamespaceText(appSettings.pyzNamespace);
        }

        if (settings.pyzTestDirectory != null) {
            mySettingsComponent.setPyzTestDirectoryText(settings.pyzTestDirectory);
        } else {
            // Use the application setting as default
            AppSettingsState appSettings = AppSettingsState.getInstance();
            mySettingsComponent.setPyzTestDirectoryText(appSettings.pyzTestDirectory);
        }

        if (settings.pyzTestNamespace != null) {
            mySettingsComponent.setPyzTestNamespaceText(settings.pyzTestNamespace);
        } else {
            // Use the application setting as default
            AppSettingsState appSettings = AppSettingsState.getInstance();
            mySettingsComponent.setPyzTestNamespaceText(appSettings.pyzTestNamespace);
        }
        
        // Set boolean features
        AppSettingsState appSettings = AppSettingsState.getInstance();
        
        mySettingsComponent.setExtendInPyzFeatureActive(
            settings.extendInPyzFeatureActive != null ? 
            settings.extendInPyzFeatureActive : 
            appSettings.extendInPyzFeatureActive
        );
        
        mySettingsComponent.setViewOnGithubFeatureActive(
            settings.viewOnGithubFeatureActive != null ? 
            settings.viewOnGithubFeatureActive : 
            appSettings.viewOnGithubFeatureActive
        );
        
        mySettingsComponent.setZedStubGatewayControllerFeatureActive(
            settings.zedStubGatewayControllerFeatureActive != null ? 
            settings.zedStubGatewayControllerFeatureActive : 
            appSettings.zedStubGatewayControllerFeatureActive
        );
        
        mySettingsComponent.setOmsNavigationFeatureActive(
            settings.omsNavigationFeatureActive != null ? 
            settings.omsNavigationFeatureActive : 
            appSettings.omsNavigationFeatureActive
        );
        
        mySettingsComponent.setTwigGotoHandlingFeatureActive(
            settings.twigGotoHandlingFeatureActive != null ? 
            settings.twigGotoHandlingFeatureActive : 
            appSettings.twigGotoHandlingFeatureActive
        );
        
        mySettingsComponent.setTransferObjectGotoHandlingFeatureActive(
            settings.transferObjectGotoHandlingFeatureActive != null ? 
            settings.transferObjectGotoHandlingFeatureActive : 
            appSettings.transferObjectGotoHandlingFeatureActive
        );
        
        mySettingsComponent.setCodeceptionHelperNavigationFeatureActiveCheckbox(
            settings.codeceptionHelperNavigationFeatureActive != null ? 
            settings.codeceptionHelperNavigationFeatureActive : 
            appSettings.codeceptionHelperNavigationFeatureActive
        );
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
