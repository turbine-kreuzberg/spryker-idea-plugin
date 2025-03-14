package com.turbinekreuzberg.plugins.settings;

import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import javax.swing.JPanel;

public class AppSettingsComponentTest extends PyzPluginTestCase {
    private AppSettingsComponent settingsComponent;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settingsComponent = new AppSettingsComponent();
    }

    public void testComponentInitialization() {
        JPanel panel = settingsComponent.getPanel();
        assertNotNull("Settings panel should be created", panel);
        assertNotNull("Should have preferred focused component", settingsComponent.getPreferredFocusedComponent());
    }

    public void testDirectoryField() {
        // Test setting and getting directory
        String testDir = "/custom/Pyz/";
        settingsComponent.setPyzDirectoryText(testDir);
        assertEquals("Directory text should match", testDir, settingsComponent.getPyzDirectoryText());
    }

    public void testNamespaceField() {
        // Test setting and getting namespace
        String testNamespace = "CustomPyz";
        settingsComponent.setPyzNamespaceText(testNamespace);
        assertEquals("Namespace text should match", testNamespace, settingsComponent.getPyzNamespaceText());
    }

    public void testFeatureToggles() {
        // Test extend in PYZ feature toggle
        settingsComponent.setExtendInPyzFeatureActive(true);
        assertTrue("Extend in PYZ feature should be enabled", settingsComponent.getExtendInPyzFeatureActive());
        settingsComponent.setExtendInPyzFeatureActive(false);
        assertFalse("Extend in PYZ feature should be disabled", settingsComponent.getExtendInPyzFeatureActive());

        // Test GitHub feature toggle
        settingsComponent.setViewOnGithubFeatureActive(true);
        assertTrue("GitHub feature should be enabled", settingsComponent.getViewOnGithubFeatureActive());
        settingsComponent.setViewOnGithubFeatureActive(false);
        assertFalse("GitHub feature should be disabled", settingsComponent.getViewOnGithubFeatureActive());

        // Test Zed stub gateway controller feature toggle
        settingsComponent.setZedStubGatewayControllerFeatureActive(true);
        assertTrue("Zed stub feature should be enabled", settingsComponent.getZedStubGatewayControllerFeatureActive());
        settingsComponent.setZedStubGatewayControllerFeatureActive(false);
        assertFalse("Zed stub feature should be disabled", settingsComponent.getZedStubGatewayControllerFeatureActive());

        // Test OMS navigation feature toggle
        settingsComponent.setOmsNavigationFeatureActive(true);
        assertTrue("OMS navigation should be enabled", settingsComponent.getOmsNavigationFeatureActive());
        settingsComponent.setOmsNavigationFeatureActive(false);
        assertFalse("OMS navigation should be disabled", settingsComponent.getOmsNavigationFeatureActive());

        // Test Twig navigation feature toggle
        settingsComponent.setTwigGotoHandlingFeatureActive(true);
        assertTrue("Twig navigation should be enabled", settingsComponent.getTwigGotoHandlingFeatureActive());
        settingsComponent.setTwigGotoHandlingFeatureActive(false);
        assertFalse("Twig navigation should be disabled", settingsComponent.getTwigGotoHandlingFeatureActive());

        // Test transfer object navigation feature toggle
        settingsComponent.setTransferObjectGotoHandlingFeatureActive(true);
        assertTrue("Transfer object navigation should be enabled", settingsComponent.getTransferObjectGotoHandlingFeatureActive());
        settingsComponent.setTransferObjectGotoHandlingFeatureActive(false);
        assertFalse("Transfer object navigation should be disabled", settingsComponent.getTransferObjectGotoHandlingFeatureActive());

        // Test codeception helper navigation feature toggle
        settingsComponent.setCodeceptionHelperNavigationFeatureActiveCheckbox(true);
        assertTrue("Codeception helper navigation should be enabled", settingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox());
        settingsComponent.setCodeceptionHelperNavigationFeatureActiveCheckbox(false);
        assertFalse("Codeception helper navigation should be disabled", settingsComponent.getCodeceptionHelperNavigationFeatureActiveCheckbox());
    }

    @Override
    protected void tearDown() throws Exception {
        settingsComponent = null;
        super.tearDown();
    }
}
