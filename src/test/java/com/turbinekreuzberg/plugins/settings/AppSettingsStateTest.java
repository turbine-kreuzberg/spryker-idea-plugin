package com.turbinekreuzberg.plugins.settings;

import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.intellij.util.xmlb.XmlSerializerUtil;

public class AppSettingsStateTest extends PyzPluginTestCase {
    private AppSettingsState settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settings = AppSettingsState.getInstance();
    }

    public void testStateInstance() {
        assertNotNull("Settings instance should be available", settings);
        assertEquals("Should have default PYZ directory", "/src/Pyz/", settings.pyzDirectory);
        assertEquals("Should have default PYZ namespace", "Pyz", settings.pyzNamespace);
    }

    public void testGetState() {
        AppSettingsState state = settings.getState();
        assertNotNull("State should not be null", state);
        assertSame("State should be the same instance", settings, state);
    }

    public void testLoadState() {
        // Create a new state with different values
        AppSettingsState newState = new AppSettingsState();
        newState.pyzDirectory = "/custom/Pyz/";
        newState.pyzNamespace = "CustomPyz";
        newState.extendInPyzFeatureActive = false;
        newState.viewOnGithubFeatureActive = false;
        newState.omsNavigationFeatureActive = false;

        // Load the new state
        settings.loadState(newState);

        // Verify all properties are copied correctly
        assertEquals("PYZ directory should be updated", "/custom/Pyz/", settings.pyzDirectory);
        assertEquals("PYZ namespace should be updated", "CustomPyz", settings.pyzNamespace);
        assertFalse("Extend feature should be disabled", settings.extendInPyzFeatureActive);
        assertFalse("GitHub feature should be disabled", settings.viewOnGithubFeatureActive);
        assertFalse("OMS navigation should be disabled", settings.omsNavigationFeatureActive);
    }

    public void testStateCloning() {
        // Create a new state with custom values
        AppSettingsState source = new AppSettingsState();
        source.pyzDirectory = "/test/Pyz/";
        source.pyzNamespace = "TestPyz";
        source.extendInPyzFeatureActive = false;

        // Create target state
        AppSettingsState target = new AppSettingsState();

        // Clone state using XmlSerializerUtil
        XmlSerializerUtil.copyBean(source, target);

        // Verify all properties are copied correctly
        assertEquals("PYZ directory should be copied", source.pyzDirectory, target.pyzDirectory);
        assertEquals("PYZ namespace should be copied", source.pyzNamespace, target.pyzNamespace);
        assertEquals("Feature state should be copied", source.extendInPyzFeatureActive, target.extendInPyzFeatureActive);
    }

    public void testFeatureDefaults() {
        assertTrue("Extend in PYZ feature should be enabled by default", settings.extendInPyzFeatureActive);
        assertTrue("View on GitHub feature should be enabled by default", settings.viewOnGithubFeatureActive);
        assertTrue("Zed stub gateway controller feature should be enabled by default", settings.zedStubGatewayControllerFeatureActive);
        assertTrue("OMS navigation feature should be enabled by default", settings.omsNavigationFeatureActive);
        assertTrue("Twig goto handling feature should be enabled by default", settings.twigGotoHandlingFeatureActive);
        assertTrue("Transfer object goto handling feature should be enabled by default", settings.transferObjectGotoHandlingFeatureActive);
        assertTrue("Codeception helper navigation feature should be enabled by default", settings.codeceptionHelperNavigationFeatureActive);
    }

    @Override
    protected void tearDown() throws Exception {
        // Reset settings to defaults
        AppSettingsState defaultSettings = new AppSettingsState();
        settings.loadState(defaultSettings);
        super.tearDown();
    }
}
