package com.turbinekreuzberg.plugins.utils;

import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;

public class PhpContentCreatorTest extends PyzPluginTestCase {
    private PhpContentCreator contentCreator;
    private Language phpLanguage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentCreator = new PhpContentCreator();
        AppSettingsState.getInstance().pyzNamespace = "Pyz";
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("PHP language support should be available", phpLanguage);
    }

    public void testCreateAbstractClass() {
        // Create test file with abstract class
        PsiFile file = createTestFile(
            "AbstractExample.php",
            phpLanguage,
            "<?php\n\nnamespace Spryker\\Test;\n\nabstract class AbstractExample\n{\n}\n"
        );

        String actualContent = contentCreator.create(file, "Test/AbstractExample", "extends");
        assertTrue("Should create abstract class", actualContent.contains("abstract class AbstractExample"));
        assertTrue("Should have correct namespace", actualContent.contains("namespace Pyz\\Test"));
        assertTrue("Should use Spryker class", actualContent.contains("use Spryker\\Test\\AbstractExample as SprykerAbstractExample"));
        assertTrue("Should extend Spryker class with alias", actualContent.contains("extends SprykerAbstractExample"));
    }

    public void testCreateFacade() {
        // Create test file with facade class
        PsiFile file = createTestFile(
            "ExampleFacade.php",
            phpLanguage,
            "<?php\n\nnamespace Spryker\\Test;\n\nclass ExampleFacade\n{\n}\n"
        );

        String actualContent = contentCreator.create(file, "Test/ExampleFacade", "extends");
        assertTrue("Should create class", actualContent.contains("class ExampleFacade"));
        assertTrue("Should have correct namespace", actualContent.contains("namespace Pyz\\Test"));
        assertTrue("Should use Spryker class", actualContent.contains("use Spryker\\Test\\ExampleFacade as SprykerExampleFacade"));
        assertTrue("Should extend Spryker class with alias", actualContent.contains("extends SprykerExampleFacade"));
    }

    public void testCreateInterface() {
        // Create test file with interface
        PsiFile file = createTestFile(
            "ExampleFacadeInterface.php",
            phpLanguage,
            "<?php\n\nnamespace Spryker\\Test;\n\ninterface ExampleFacadeInterface\n{\n}\n"
        );

        String actualContent = contentCreator.create(file, "Test/ExampleFacadeInterface", "extends");
        assertTrue("Should create interface", actualContent.contains("interface ExampleFacadeInterface"));
        assertTrue("Should have correct namespace", actualContent.contains("namespace Pyz\\Test"));
        assertTrue("Should use Spryker interface", actualContent.contains("use Spryker\\Test\\ExampleFacadeInterface as SprykerExampleFacadeInterface"));
        assertTrue("Should extend Spryker interface with alias", actualContent.contains("extends SprykerExampleFacadeInterface"));
    }

}
