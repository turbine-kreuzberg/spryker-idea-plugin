package com.turbinekreuzberg.plugins.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AppSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField pyzDirectoryText = new JBTextField();
    private final JBTextField pyzNamespaceText = new JBTextField();

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("PYZ directory (default is '/src/Pyz/')"), pyzDirectoryText, 1, false)
                .addLabeledComponent(new JBLabel("PYZ namespace (default is 'Pyz')"), pyzNamespaceText, 2, false)
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
}
