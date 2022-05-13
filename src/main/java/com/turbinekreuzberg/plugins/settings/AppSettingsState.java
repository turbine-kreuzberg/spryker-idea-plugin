package com.turbinekreuzberg.plugins.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.turbinekreuzberg.plugins.settings.AppSettingsState",
        storages = @Storage("PyzPluginSettings.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String pyzDirectory = "/src/Pyz/";
    public String pyzNamespace = "Pyz";
    public boolean extendInPyzFeatureActive = true;
    public boolean viewOnGithubFeatureActive = true;
    public boolean zedStubGatewayControllerFeatureActive = true;
    public boolean omsNavigationFeatureActive = true;
    public boolean twigGotoHandlingFeatureActive = true;
    public boolean transferObjectGotoHandlingFeatureActive = true;

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
