package com.turbinekreuzberg.plugins;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.MapDataContext;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Base test case for PYZ plugin tests that provides common test functionality.
 */
public abstract class PyzPluginTestCase extends BasePlatformTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
    
    /**
     * Creates a test file with the given content and language.
     */
    protected PsiFile createTestFile(String relativePath, Language language, String content) {
        return myFixture.configureByText(relativePath, content);
    }
    
    /**
     * Creates an AnActionEvent for testing actions.
     */
    protected AnActionEvent createActionEvent(PsiFile file) {
        MapDataContext context = new MapDataContext();
        context.put(CommonDataKeys.PSI_FILE, file);
        context.put(CommonDataKeys.PROJECT, getProject());
        return AnActionEvent.createFromDataContext("", null, context);
    }
    
    /**
     * Finds an element at the given text in a file.
     */
    protected PsiElement findElementAtText(PsiFile file, String text) {
        Editor editor = myFixture.getEditor();
        String fileContent = file.getText();
        int offset = fileContent.indexOf(text);
        if (offset == -1) {
            return null;
        }
        return file.findElementAt(offset);
    }
}
