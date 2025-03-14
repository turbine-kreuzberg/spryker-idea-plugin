package com.turbinekreuzberg.plugins.settings;

import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.intellij.openapi.options.ConfigurationException;

public class PyzSettingsTest extends PyzPluginTestCase {
    private AppSettingsState settings;
    private AppSettingsConfigurable configurable;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settings = AppSettingsState.getInstance();
        configurable = new AppSettingsConfigurable();
    }
    
    public void testDefaultSettings() {
        // Verify default settings
        assertEquals("/src/Pyz/", settings.pyzDirectory);
        assertEquals("Pyz", settings.pyzNamespace);
        assertTrue("Extend in PYZ feature should be enabled by default", settings.extendInPyzFeatureActive);
        assertTrue("View on GitHub feature should be enabled by default", settings.viewOnGithubFeatureActive);
    }

    public void testSettingsModification() throws ConfigurationException {
        // Modify settings
        settings.pyzDirectory = "/custom/Pyz/";
        settings.pyzNamespace = "CustomPyz";
        settings.extendInPyzFeatureActive = false;
        settings.viewOnGithubFeatureActive = false;
        
        // Verify modifications
        assertEquals("/custom/Pyz/", settings.pyzDirectory);
        assertEquals("CustomPyz", settings.pyzNamespace);
        assertFalse("Extend in PYZ feature should be disabled", settings.extendInPyzFeatureActive);
        assertFalse("View on GitHub feature should be disabled", settings.viewOnGithubFeatureActive);
        
        // Test settings persistence
        configurable.createComponent(); // Initialize component
        assertTrue("Settings should be modified", configurable.isModified());
        configurable.apply();
        assertFalse("Settings should be applied", configurable.isModified());
    }

    public void testSettingsReset() throws ConfigurationException {
        // Create UI component
        configurable.createComponent();
        
        // Apply some changes to settings
        settings.pyzDirectory = "/custom/Pyz/";
        settings.pyzNamespace = "CustomPyz";
        settings.extendInPyzFeatureActive = false;
        
        // Reset the UI component to match current settings
        configurable.reset();
        
        // Verify that isModified returns false since UI matches settings
        assertFalse("Settings should not be modified after reset", configurable.isModified());
        
        // Apply the UI state to settings and verify they match
        configurable.apply();
        assertEquals("/custom/Pyz/", settings.pyzDirectory);
        assertEquals("CustomPyz", settings.pyzNamespace);
        assertFalse(settings.extendInPyzFeatureActive);
    }

    public void testFeatureToggling() {
        // Test OMS navigation
        settings.omsNavigationFeatureActive = false;
        assertFalse("OMS navigation should be disabled", settings.omsNavigationFeatureActive);
        settings.omsNavigationFeatureActive = true;
        assertTrue("OMS navigation should be enabled", settings.omsNavigationFeatureActive);
        
        // Test Twig navigation
        settings.twigGotoHandlingFeatureActive = false;
        assertFalse("Twig navigation should be disabled", settings.twigGotoHandlingFeatureActive);
        settings.twigGotoHandlingFeatureActive = true;
        assertTrue("Twig navigation should be enabled", settings.twigGotoHandlingFeatureActive);
        
        // Test transfer object navigation
        settings.transferObjectGotoHandlingFeatureActive = false;
        assertFalse("Transfer object navigation should be disabled", settings.transferObjectGotoHandlingFeatureActive);
        settings.transferObjectGotoHandlingFeatureActive = true;
        assertTrue("Transfer object navigation should be enabled", settings.transferObjectGotoHandlingFeatureActive);
        
        // Test Zed stub gateway controller feature
        settings.zedStubGatewayControllerFeatureActive = false;
        assertFalse("Zed stub gateway controller feature should be disabled", settings.zedStubGatewayControllerFeatureActive);
        settings.zedStubGatewayControllerFeatureActive = true;
        assertTrue("Zed stub gateway controller feature should be enabled", settings.zedStubGatewayControllerFeatureActive);
        
        // Test codeception helper navigation
        settings.codeceptionHelperNavigationFeatureActive = false;
        assertFalse("Codeception helper navigation should be disabled", settings.codeceptionHelperNavigationFeatureActive);
        settings.codeceptionHelperNavigationFeatureActive = true;
        assertTrue("Codeception helper navigation should be enabled", settings.codeceptionHelperNavigationFeatureActive);
    }
    
    @Override
    protected void tearDown() throws Exception {
        configurable.disposeUIResources();
        super.tearDown();
    }
}
