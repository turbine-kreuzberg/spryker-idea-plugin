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
        assertTrue("Go to parent feature should be enabled by default", settings.goToParentFeatureActive);
        assertTrue("Zed stub gateway controller feature should be enabled by default", settings.zedStubGatewayControllerFeatureActive);
        assertTrue("OMS navigation feature should be enabled by default", settings.omsNavigationFeatureActive);
        assertTrue("Twig goto handling feature should be enabled by default", settings.twigGotoHandlingFeatureActive);
        assertTrue("Transfer object goto handling feature should be enabled by default", settings.transferObjectGotoHandlingFeatureActive);
        assertTrue("Codeception helper navigation feature should be enabled by default", settings.codeceptionHelperNavigationFeatureActive);
        assertTrue("Twig to glossary key goto handling feature should be enabled by default", settings.twigGlossaryKeyGotoHandlingFeatureActive);
    }

    public void testSettingsModification() throws ConfigurationException {
        // Modify settings
        settings.pyzDirectory = "/custom/Pyz/";
        settings.pyzNamespace = "CustomPyz";
        settings.extendInPyzFeatureActive = false;
        settings.viewOnGithubFeatureActive = false;
        settings.goToParentFeatureActive = false;
        settings.zedStubGatewayControllerFeatureActive = false;
        settings.omsNavigationFeatureActive = false;
        settings.twigGotoHandlingFeatureActive = false;
        settings.transferObjectGotoHandlingFeatureActive = false;
        settings.codeceptionHelperNavigationFeatureActive = false;
        settings.twigGlossaryKeyGotoHandlingFeatureActive = false;

        // Verify modifications
        assertEquals("/custom/Pyz/", settings.pyzDirectory);
        assertEquals("CustomPyz", settings.pyzNamespace);
        assertFalse("Extend in PYZ feature should be disabled", settings.extendInPyzFeatureActive);
        assertFalse("View on GitHub feature should be disabled", settings.viewOnGithubFeatureActive);
        assertFalse("Go to parent feature should be disabled", settings.goToParentFeatureActive);
        assertFalse("Zed stub gateway controller feature should be disabled", settings.zedStubGatewayControllerFeatureActive);
        assertFalse("OMS navigation feature should be disabled", settings.omsNavigationFeatureActive);
        assertFalse("Twig goto handling feature should be disabled", settings.twigGotoHandlingFeatureActive);
        assertFalse("Transfer object goto handling feature should be disabled", settings.transferObjectGotoHandlingFeatureActive);
        assertFalse("Codeception helper navigation feature should be disabled", settings.codeceptionHelperNavigationFeatureActive);
        assertFalse("Twig to glossary key goto handling feature should be disabled", settings.twigGlossaryKeyGotoHandlingFeatureActive);

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
        settings.viewOnGithubFeatureActive = false;
        settings.goToParentFeatureActive = false;
        settings.zedStubGatewayControllerFeatureActive = false;
        settings.omsNavigationFeatureActive = false;
        settings.twigGotoHandlingFeatureActive = false;
        settings.transferObjectGotoHandlingFeatureActive = false;
        settings.codeceptionHelperNavigationFeatureActive = false;
        settings.twigGlossaryKeyGotoHandlingFeatureActive = false;
        settings.viewOnGithubFeatureActive = false;
        settings.goToParentFeatureActive = false;

        // Reset the UI component to match current settings
        configurable.reset();
        
        // Verify that isModified returns false since UI matches settings
        assertFalse("Settings should not be modified after reset", configurable.isModified());
        
        // Apply the UI state to settings and verify they match
        configurable.apply();
        assertEquals("/custom/Pyz/", settings.pyzDirectory);
        assertEquals("CustomPyz", settings.pyzNamespace);
        assertFalse(settings.extendInPyzFeatureActive);
        assertFalse(settings.viewOnGithubFeatureActive);
        assertFalse(settings.goToParentFeatureActive);
        assertFalse(settings.zedStubGatewayControllerFeatureActive);
        assertFalse(settings.omsNavigationFeatureActive);
        assertFalse(settings.twigGotoHandlingFeatureActive);
        assertFalse(settings.transferObjectGotoHandlingFeatureActive);
        assertFalse(settings.codeceptionHelperNavigationFeatureActive);
        assertFalse(settings.twigGlossaryKeyGotoHandlingFeatureActive);
    }

    public void testFeatureToggling() {
        // Test extend in pyz feature
        settings.extendInPyzFeatureActive = false;
        assertFalse("Extend in pyz feature should be disabled", settings.extendInPyzFeatureActive);
        settings.extendInPyzFeatureActive = true;
        assertTrue("Extend in pyz feature should be enabled", settings.extendInPyzFeatureActive);

        // Test view on github feature
        settings.viewOnGithubFeatureActive = false;
        assertFalse("View on github feature should be disabled", settings.viewOnGithubFeatureActive);
        settings.viewOnGithubFeatureActive = true;
        assertTrue("View on github feature should be enabled", settings.viewOnGithubFeatureActive);

        // Test goToParent feature
        settings.goToParentFeatureActive = false;
        assertFalse("GoToParent feature should be disabled", settings.goToParentFeatureActive);
        settings.goToParentFeatureActive = true;
        assertTrue("GoToParent feature should be enabled", settings.goToParentFeatureActive);

        // Test Zed stub gateway controller feature
        settings.zedStubGatewayControllerFeatureActive = false;
        assertFalse("Zed stub gateway controller feature should be disabled", settings.zedStubGatewayControllerFeatureActive);
        settings.zedStubGatewayControllerFeatureActive = true;
        assertTrue("Zed stub gateway controller feature should be enabled", settings.zedStubGatewayControllerFeatureActive);

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

        // Test codeception helper navigation
        settings.codeceptionHelperNavigationFeatureActive = false;
        assertFalse("Codeception helper navigation should be disabled", settings.codeceptionHelperNavigationFeatureActive);
        settings.codeceptionHelperNavigationFeatureActive = true;
        assertTrue("Codeception helper navigation should be enabled", settings.codeceptionHelperNavigationFeatureActive);

        // Test Twig to glossary key goto handling feature
        settings.twigGlossaryKeyGotoHandlingFeatureActive = false;
        assertFalse("Twig to glossary key goto handling feature should be disabled", settings.twigGlossaryKeyGotoHandlingFeatureActive);
        settings.twigGlossaryKeyGotoHandlingFeatureActive = true;
        assertTrue("Twig to glossary key goto handling feature should be enabled", settings.twigGlossaryKeyGotoHandlingFeatureActive);
    }
    
    @Override
    protected void tearDown() throws Exception {
        configurable.disposeUIResources();
        super.tearDown();
    }
}
