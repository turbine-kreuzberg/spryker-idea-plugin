package com.turbinekreuzberg.plugins.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * UI component for project-level settings of the PYZ Plugin.
 */
public class ProjectSettingsComponent {
    private final JPanel myMainPanel;
    private final JBCheckBox useProjectSettingsCheckbox = new JBCheckBox("Use project-specific settings");
    private final JBTextField pyzDirectoryText = new JBTextField();
    private final JBTextField pyzNamespaceText = new JBTextField();
    private final JBTextField pyzTestDirectoryText = new JBTextField();
    private final JBTextField pyzTestNamespaceText = new JBTextField();
    private final JBCheckBox extendInPyzFeatureActiveCheckbox = new JBCheckBox("Extend in PYZ feature active");
    private final JBCheckBox viewOnGithubFeatureActiveCheckbox = new JBCheckBox("View on Github feature active");
    private final JBCheckBox goToParentFeatureActiveCheckbox = new JBCheckBox("Go to parent feature active");
    private final JBCheckBox zedStubGatewayControllerFeatureActiveCheckbox = new JBCheckBox("Zed stub gateway controller feature active");
    private final JBCheckBox omsNavigationFeatureActiveCheckbox = new JBCheckBox("OMS navigation feature active");
    private final JBCheckBox twigGotoHandlingFeatureActiveCheckbox = new JBCheckBox("Twig goto handling feature active");
    private final JBCheckBox transferObjectGotoHandlingFeatureActiveCheckbox = new JBCheckBox("Transfer object goto handling feature active");
    private final JBCheckBox codeceptionHelperNavigationFeatureActiveCheckbox = new JBCheckBox("Codeception helper navigation feature active");
    private final JBCheckBox twigGlossaryKeyGotoHandlingFeatureActiveCheckbox = new JBCheckBox("Twig to glossary key goto handling feature active");

    public ProjectSettingsComponent() {
        JPanel settingsPanel = FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addComponent(new JLabel("Active features"))
                .addVerticalGap(10)
                .addComponent(extendInPyzFeatureActiveCheckbox, 1)
                .addComponent(viewOnGithubFeatureActiveCheckbox, 1)
                .addComponent(goToParentFeatureActiveCheckbox, 1)
                .addComponent(zedStubGatewayControllerFeatureActiveCheckbox, 1)
                .addComponent(omsNavigationFeatureActiveCheckbox, 1)
                .addComponent(twigGotoHandlingFeatureActiveCheckbox, 1)
                .addComponent(transferObjectGotoHandlingFeatureActiveCheckbox, 1)
                .addComponent(codeceptionHelperNavigationFeatureActiveCheckbox, 1)
                .addComponent(twigGlossaryKeyGotoHandlingFeatureActiveCheckbox, 1)
                .addVerticalGap(10)
                .addSeparator()
                .addVerticalGap(10)
                .addLabeledComponent(new JBLabel("PYZ directory (default is '/src/Pyz/')"), pyzDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ namespace (default is 'Pyz')"), pyzNamespaceText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ test directory (default is '/tests/PyzTest/')"), pyzTestDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ test namespace (default is 'PyzTest')"), pyzTestNamespaceText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(useProjectSettingsCheckbox, 1)
                .addComponent(settingsPanel, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        // Enable/disable other controls based on the "use project settings" checkbox
        useProjectSettingsCheckbox.addActionListener(e -> setControlsEnabled(useProjectSettingsCheckbox.isSelected()));
        
        // Initialize controls as disabled until "use project settings" is checked
        setControlsEnabled(false);
    }

    private void setControlsEnabled(boolean enabled) {
        pyzDirectoryText.setEnabled(enabled);
        pyzNamespaceText.setEnabled(enabled);
        pyzTestDirectoryText.setEnabled(enabled);
        pyzTestNamespaceText.setEnabled(enabled);
        extendInPyzFeatureActiveCheckbox.setEnabled(enabled);
        viewOnGithubFeatureActiveCheckbox.setEnabled(enabled);
        goToParentFeatureActiveCheckbox.setEnabled(enabled);
        zedStubGatewayControllerFeatureActiveCheckbox.setEnabled(enabled);
        omsNavigationFeatureActiveCheckbox.setEnabled(enabled);
        twigGotoHandlingFeatureActiveCheckbox.setEnabled(enabled);
        transferObjectGotoHandlingFeatureActiveCheckbox.setEnabled(enabled);
        codeceptionHelperNavigationFeatureActiveCheckbox.setEnabled(enabled);
        twigGlossaryKeyGotoHandlingFeatureActiveCheckbox.setEnabled(enabled);
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return useProjectSettingsCheckbox;
    }

    // Getters and setters
    public boolean getUseProjectSettings() {
        return useProjectSettingsCheckbox.isSelected();
    }

    public void setUseProjectSettings(boolean useProjectSettings) {
        useProjectSettingsCheckbox.setSelected(useProjectSettings);
        setControlsEnabled(useProjectSettings);
    }

    public String getPyzDirectoryText() {
        return pyzDirectoryText.getText();
    }

    public void setPyzDirectoryText(String pyzDirectory) {
        pyzDirectoryText.setText(pyzDirectory);
    }

    public String getPyzNamespaceText() {
        return pyzNamespaceText.getText();
    }

    public void setPyzNamespaceText(String pyzNamespace) {
        pyzNamespaceText.setText(pyzNamespace);
    }

    public String getPyzTestDirectoryText() {
        return pyzTestDirectoryText.getText();
    }

    public void setPyzTestDirectoryText(String pyzTestDirectory) {
        pyzTestDirectoryText.setText(pyzTestDirectory);
    }

    public String getPyzTestNamespaceText() {
        return pyzTestNamespaceText.getText();
    }

    public void setPyzTestNamespaceText(String pyzTestNamespace) {
        pyzTestNamespaceText.setText(pyzTestNamespace);
    }

    public boolean getExtendInPyzFeatureActive() {
        return extendInPyzFeatureActiveCheckbox.isSelected();
    }

    public void setExtendInPyzFeatureActive(boolean extendInPyzFeatureActive) {
        extendInPyzFeatureActiveCheckbox.setSelected(extendInPyzFeatureActive);
    }

    public boolean getViewOnGithubFeatureActive() {
        return viewOnGithubFeatureActiveCheckbox.isSelected();
    }

    public void setViewOnGithubFeatureActive(boolean viewOnGithubFeatureActive) {
        viewOnGithubFeatureActiveCheckbox.setSelected(viewOnGithubFeatureActive);
    }

    public boolean getGoToParentFeatureActive() { return goToParentFeatureActiveCheckbox.isSelected(); }

    public void setGoToParentFeatureActive(boolean goToParentFeatureActive) {
        goToParentFeatureActiveCheckbox.setSelected(goToParentFeatureActive);
    }

    public boolean getZedStubGatewayControllerFeatureActive() {
        return zedStubGatewayControllerFeatureActiveCheckbox.isSelected();
    }

    public void setZedStubGatewayControllerFeatureActive(boolean zedStubGatewayControllerFeatureActive) {
        zedStubGatewayControllerFeatureActiveCheckbox.setSelected(zedStubGatewayControllerFeatureActive);
    }

    public boolean getOmsNavigationFeatureActive() {
        return omsNavigationFeatureActiveCheckbox.isSelected();
    }

    public void setOmsNavigationFeatureActive(boolean omsNavigationFeatureActive) {
        omsNavigationFeatureActiveCheckbox.setSelected(omsNavigationFeatureActive);
    }

    public boolean getTwigGotoHandlingFeatureActive() {
        return twigGotoHandlingFeatureActiveCheckbox.isSelected();
    }

    public void setTwigGotoHandlingFeatureActive(boolean twigGotoHandlingFeatureActive) {
        twigGotoHandlingFeatureActiveCheckbox.setSelected(twigGotoHandlingFeatureActive);
    }

    public boolean getTransferObjectGotoHandlingFeatureActive() {
        return transferObjectGotoHandlingFeatureActiveCheckbox.isSelected();
    }

    public void setTransferObjectGotoHandlingFeatureActive(boolean transferObjectGotoHandlingFeatureActive) {
        transferObjectGotoHandlingFeatureActiveCheckbox.setSelected(transferObjectGotoHandlingFeatureActive);
    }

    public boolean getCodeceptionHelperNavigationFeatureActiveCheckbox() {
        return codeceptionHelperNavigationFeatureActiveCheckbox.isSelected();
    }

    public void setCodeceptionHelperNavigationFeatureActiveCheckbox(boolean codeceptionHelperNavigationFeatureActive) {
        codeceptionHelperNavigationFeatureActiveCheckbox.setSelected(codeceptionHelperNavigationFeatureActive);
    }

    public boolean getTwigGlossaryKeyGotoHandlingFeatureActive() {
        return twigGlossaryKeyGotoHandlingFeatureActiveCheckbox.isSelected();
    }

    public void setTwigGlossaryKeyGotoHandlingFeatureActive(boolean twigGlossaryKeyGotoHandlingFeatureActive) {
        twigGlossaryKeyGotoHandlingFeatureActiveCheckbox.setSelected(twigGlossaryKeyGotoHandlingFeatureActive);
    }
}
