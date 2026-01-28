package com.turbinekreuzberg.plugins.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project-level settings state for the PYZ Plugin.
 * Null values indicate that the application-level setting should be used instead.
 */
@State(
        name = "com.turbinekreuzberg.plugins.settings.ProjectSettingsState",
        storages = @Storage("PyzPluginProjectSettings.xml")
)
public class ProjectSettingsState implements PersistentStateComponent<ProjectSettingsState> {
    
    // Flag to enable project-specific settings
    public boolean useProjectSettings = false;
    
    // Settings (null means "use application setting")
    public String pyzDirectory = null;
    public String pyzNamespace = null;
    public String pyzTestDirectory = null;
    public String pyzTestNamespace = null;
    public String[] sprykerNamespaces = null;
    public Boolean extendInPyzFeatureActive = null;
    public Boolean viewOnGithubFeatureActive = null;
    public Boolean goToParentFeatureActive = null;
    public Boolean zedStubGatewayControllerFeatureActive = null;
    public Boolean omsNavigationFeatureActive = null;
    public Boolean twigGotoHandlingFeatureActive = null;
    public Boolean transferObjectGotoHandlingFeatureActive = null;
    public Boolean codeceptionHelperNavigationFeatureActive = null;

    // We cannot have a non-serializable field that is not marked as transient
    // The Project reference is obtained through the service locator instead
    
    public static ProjectSettingsState getInstance(@NotNull Project project) {
        return project.getService(ProjectSettingsState.class);
    }

    @Nullable
    @Override
    public ProjectSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
    
    // Add explicit getter and setter for useProjectSettings to ensure proper serialization
    public boolean isUseProjectSettings() {
        return useProjectSettings;
    }
    
    public void setUseProjectSettings(boolean useProjectSettings) {
        this.useProjectSettings = useProjectSettings;
    }
}
