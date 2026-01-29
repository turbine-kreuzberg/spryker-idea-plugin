package com.turbinekreuzberg.plugins.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for accessing PYZ Plugin settings.
 * Checks project-level settings first, then falls back to application-level settings.
 */
public class SettingsManager {

    /**
     * Get the PYZ directory setting, checking project settings first.
     * 
     * @param project the current project
     * @return the configured PYZ directory
     */
    public static String getPyzDirectory(@NotNull Project project) {
        ProjectSettingsState projectSettings = ProjectSettingsState.getInstance(project);
        AppSettingsState appSettings = AppSettingsState.getInstance();
        
        if (projectSettings.useProjectSettings && projectSettings.pyzDirectory != null) {
            return projectSettings.pyzDirectory;
        }
        return appSettings.pyzDirectory;
    }
    
    /**
     * Get the PYZ namespace setting, checking project settings first.
     * 
     * @param project the current project
     * @return the configured PYZ namespace
     */
    public static String getPyzNamespace(@NotNull Project project) {
        ProjectSettingsState projectSettings = ProjectSettingsState.getInstance(project);
        AppSettingsState appSettings = AppSettingsState.getInstance();
        
        if (projectSettings.useProjectSettings && projectSettings.pyzNamespace != null) {
            return projectSettings.pyzNamespace;
        }
        return appSettings.pyzNamespace;
    }

    /**
     * Get the PYZ test directory setting, checking project settings first.
     *
     * @param project the current project
     * @return the configured PYZ test directory
     */
    public static String getPyzTestDirectory(@NotNull Project project) {
        ProjectSettingsState projectSettings = ProjectSettingsState.getInstance(project);
        AppSettingsState appSettings = AppSettingsState.getInstance();

        if (projectSettings.useProjectSettings && projectSettings.pyzTestDirectory != null) {
            return projectSettings.pyzTestDirectory;
        }
        return appSettings.pyzTestDirectory;
    }

    /**
     * Get the PYZ test namespace setting, checking project settings first.
     *
     * @param project the current project
     * @return the configured PYZ test namespace
     */
    public static String getPyzTestNamespace(@NotNull Project project) {
        ProjectSettingsState projectSettings = ProjectSettingsState.getInstance(project);
        AppSettingsState appSettings = AppSettingsState.getInstance();

        if (projectSettings.useProjectSettings && projectSettings.pyzTestNamespace != null) {
            return projectSettings.pyzTestNamespace;
        }
        return appSettings.pyzTestNamespace;
    }
    
    /**
     * Check if a feature is enabled, checking project settings first.
     * 
     * @param project the current project
     * @param feature the feature to check
     * @return true if the feature is enabled
     */
    public static boolean isFeatureEnabled(@NotNull Project project, @NotNull Feature feature) {
        ProjectSettingsState projectSettings = ProjectSettingsState.getInstance(project);
        AppSettingsState appSettings = AppSettingsState.getInstance();
        
        if (!projectSettings.useProjectSettings) {
            return getAppFeatureValue(appSettings, feature);
        }
        
        Boolean projectValue = getProjectFeatureValue(projectSettings, feature);
        return projectValue != null ? projectValue : getAppFeatureValue(appSettings, feature);
    }
    
    /**
     * Get the value of a feature from application settings.
     */
    private static boolean getAppFeatureValue(AppSettingsState appSettings, Feature feature) {
        switch (feature) {
            case EXTEND_IN_PYZ:
                return appSettings.extendInPyzFeatureActive;
            case VIEW_ON_GITHUB:
                return appSettings.viewOnGithubFeatureActive;
            case GO_TO_PARENT:
                return appSettings.goToParentFeatureActive;
            case ZED_STUB_GATEWAY_CONTROLLER:
                return appSettings.zedStubGatewayControllerFeatureActive;
            case OMS_NAVIGATION:
                return appSettings.omsNavigationFeatureActive;
            case TWIG_GOTO_HANDLING:
                return appSettings.twigGotoHandlingFeatureActive;
            case TRANSFER_OBJECT_GOTO_HANDLING:
                return appSettings.transferObjectGotoHandlingFeatureActive;
            case CODECEPTION_HELPER_NAVIGATION:
                return appSettings.codeceptionHelperNavigationFeatureActive;
            default:
                return false;
        }
    }
    
    /**
     * Get the value of a feature from project settings.
     */
    @Nullable
    private static Boolean getProjectFeatureValue(ProjectSettingsState projectSettings, Feature feature) {
        switch (feature) {
            case EXTEND_IN_PYZ:
                return projectSettings.extendInPyzFeatureActive;
            case VIEW_ON_GITHUB:
                return projectSettings.viewOnGithubFeatureActive;
            case GO_TO_PARENT:
                return projectSettings.goToParentFeatureActive;
            case ZED_STUB_GATEWAY_CONTROLLER:
                return projectSettings.zedStubGatewayControllerFeatureActive;
            case OMS_NAVIGATION:
                return projectSettings.omsNavigationFeatureActive;
            case TWIG_GOTO_HANDLING:
                return projectSettings.twigGotoHandlingFeatureActive;
            case TRANSFER_OBJECT_GOTO_HANDLING:
                return projectSettings.transferObjectGotoHandlingFeatureActive;
            case CODECEPTION_HELPER_NAVIGATION:
                return projectSettings.codeceptionHelperNavigationFeatureActive;
            default:
                return null;
        }
    }
    
    /**
     * Enum of available features in the plugin.
     */
    public enum Feature {
        EXTEND_IN_PYZ,
        VIEW_ON_GITHUB,
        GO_TO_PARENT,
        ZED_STUB_GATEWAY_CONTROLLER,
        OMS_NAVIGATION,
        TWIG_GOTO_HANDLING,
        TRANSFER_OBJECT_GOTO_HANDLING,
        CODECEPTION_HELPER_NAVIGATION
    }
}
