package com.turbinekreuzberg.plugins.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AppSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField pyzDirectoryText = new JBTextField();
    private final JBTextField pyzNamespaceText = new JBTextField();
    private final JBTextField pyzTestDirectoryText = new JBTextField();
    private final JBTextField pyzTestNamespaceText = new JBTextField();
    private final JBCheckBox extendInPyzFeatureActiveCheckbox = new JBCheckBox("Extend-in-PYZ");
    private final JBCheckBox viewOnGithubFeatureActiveCheckbox = new JBCheckBox("View-on-GitHub");
    private final JBCheckBox zedStubGatewayControllerFeatureActiveCheckbox = new JBCheckBox("Zed stub <> gateway controller navigation");
    private final JBCheckBox omsNavigationFeatureActiveCheckbox = new JBCheckBox("State machine navigation");
    private final JBCheckBox twigGotoHandlingFeatureActiveCheckbox = new JBCheckBox("Twig goto-handling");
    private final JBCheckBox transferObjectGotoHandlingFeatureActiveCheckbox = new JBCheckBox("Transfer object goto-handling");
    private final JBCheckBox codeceptionHelperNavigationFeatureActiveCheckbox = new JBCheckBox("Codeception helper navigation");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JLabel("Active features"))
                .addVerticalGap(10)
                .addComponent(extendInPyzFeatureActiveCheckbox, 1)
                .addComponent(viewOnGithubFeatureActiveCheckbox, 1)
                .addComponent(zedStubGatewayControllerFeatureActiveCheckbox, 1)
                .addComponent(omsNavigationFeatureActiveCheckbox, 1)
                .addComponent(twigGotoHandlingFeatureActiveCheckbox, 1)
                .addComponent(transferObjectGotoHandlingFeatureActiveCheckbox, 1)
                .addComponent(codeceptionHelperNavigationFeatureActiveCheckbox, 1)
                .addVerticalGap(10)
                .addSeparator()
                .addVerticalGap(10)
                .addLabeledComponent(new JBLabel("PYZ directory (default is '/src/Pyz/')"), pyzDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ namespace (default is 'Pyz')"), pyzNamespaceText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ test directory (default is '/tests/PyzTest/')"), pyzTestDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ test namespace (default is 'PyzTest')"), pyzTestNamespaceText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return pyzDirectoryText;
    }

    @NotNull
    public String getPyzDirectoryText() {
        return pyzDirectoryText.getText();
    }

    public void setPyzDirectoryText(@NotNull String newText) {
        pyzDirectoryText.setText(newText);
    }

    @NotNull
    public String getPyzNamespaceText() {
        return pyzNamespaceText.getText();
    }

    public void setPyzNamespaceText(@NotNull String newText) {
        pyzNamespaceText.setText(newText);
    }
    
    @NotNull
    public String getPyzTestDirectoryText() {
        return pyzTestDirectoryText.getText();
    }

    public void setPyzTestDirectoryText(@NotNull String newText) {
        pyzTestDirectoryText.setText(newText);
    }

    @NotNull
    public String getPyzTestNamespaceText() {
        return pyzTestNamespaceText.getText();
    }

    public void setPyzTestNamespaceText(@NotNull String newText) {
        pyzTestNamespaceText.setText(newText);
    }

    public boolean getExtendInPyzFeatureActive() {
        return extendInPyzFeatureActiveCheckbox.isSelected();
    }

    public void setExtendInPyzFeatureActive(boolean newStatus) {
        extendInPyzFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getViewOnGithubFeatureActive() {
        return viewOnGithubFeatureActiveCheckbox.isSelected();
    }

    public void setViewOnGithubFeatureActive(boolean newStatus) {
        viewOnGithubFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getZedStubGatewayControllerFeatureActive() {
        return zedStubGatewayControllerFeatureActiveCheckbox.isSelected();
    }

    public void setZedStubGatewayControllerFeatureActive(boolean newStatus) {
        zedStubGatewayControllerFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getOmsNavigationFeatureActive() {
        return omsNavigationFeatureActiveCheckbox.isSelected();
    }

    public void setOmsNavigationFeatureActive(boolean newStatus) {
        omsNavigationFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getTwigGotoHandlingFeatureActive() {
        return twigGotoHandlingFeatureActiveCheckbox.isSelected();
    }

    public void setTwigGotoHandlingFeatureActive(boolean newStatus) {
        twigGotoHandlingFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getTransferObjectGotoHandlingFeatureActive() {
        return transferObjectGotoHandlingFeatureActiveCheckbox.isSelected();
    }

    public void setTransferObjectGotoHandlingFeatureActive(boolean newStatus) {
        transferObjectGotoHandlingFeatureActiveCheckbox.setSelected(newStatus);
    }

    public boolean getCodeceptionHelperNavigationFeatureActiveCheckbox() {
        return codeceptionHelperNavigationFeatureActiveCheckbox.isSelected();
    }

    public void setCodeceptionHelperNavigationFeatureActiveCheckbox(boolean newStatus) {
        codeceptionHelperNavigationFeatureActiveCheckbox.setSelected(newStatus);
    }
}
