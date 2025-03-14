package com.turbinekreuzberg.plugins.utils;

import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;

public class GenericContentCreatorTest extends PyzPluginTestCase {
    private GenericContentCreator creator;
    private Language phpLanguage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        creator = new GenericContentCreator();
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("PHP language support should be available", phpLanguage);
    }

    public void testCreateFromPhpFile() {
        // Test content with PHP code
        String phpContent = "<?php\n" +
            "namespace Pyz\\Zed\\Cart\\Business;\n" +
            "\n" +
            "use Spryker\\Zed\\Cart\\Business\\CartFacade as SprykerCartFacade;\n" +
            "\n" +
            "class CartFacade extends SprykerCartFacade\n" +
            "{\n" +
            "    public function addItem($sku)\n" +
            "    {\n" +
            "        return parent::addItem($sku);\n" +
            "    }\n" +
            "}\n";

        // Create test file
        PsiFile psiFile = createTestFile(
            "CartFacade.php",
            phpLanguage,
            phpContent
        );

        // Verify content creation
        String createdContent = creator.create(psiFile);
        assertEquals("Should return exact file content", phpContent, createdContent);
    }

    public void testCreateFromXmlFile() {
        // Test content with XML
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
            "<transfers xmlns=\"spryker:transfer-01\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xsi:schemaLocation=\"spryker:transfer-01 http://static.spryker.com/transfer-01.xsd\">\n" +
            "    <transfer name=\"Cart\">\n" +
            "        <property name=\"items\" type=\"Item[]\"/>\n" +
            "    </transfer>\n" +
            "</transfers>";

        // Create test file with a simpler path
        PsiFile psiFile = createTestFile(
            "cart.transfer.xml",
            Language.findLanguageByID("XML"),
            xmlContent
        );

        // Verify content creation
        String createdContent = creator.create(psiFile);
        assertEquals("Should return exact file content", xmlContent, createdContent);
    }
}
