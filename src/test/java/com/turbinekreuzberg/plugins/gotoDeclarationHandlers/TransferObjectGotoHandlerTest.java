package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;

public class TransferObjectGotoHandlerTest extends PyzPluginTestCase {
    private Language xmlLanguage;
    private Language phpLanguage;
    private TransferObjectGotoHandler handler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xmlLanguage = Language.findLanguageByID("XML");
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("XML language support should be available", xmlLanguage);
        assertNotNull("PHP language support should be available", phpLanguage);
        handler = new TransferObjectGotoHandler();
        AppSettingsState.getInstance().transferObjectGotoHandlingFeatureActive = true;
    }

    public void testNavigationFromPhpToTransferDefinition() {
        // Create mock transfer definition
        createTestFile(
            "CartTransfer.transfer.xml",
            xmlLanguage,
            "<?xml version=\"1.0\"?>\n" +
            "<transfers xmlns=\"spryker:transfer-01\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xsi:schemaLocation=\"spryker:transfer-01 http://static.spryker.com/transfer-01.xsd\">\n" +
            "    <transfer name=\"Cart\">\n" +
            "        <property name=\"items\" type=\"Item[]\"/>\n" +
            "        <property name=\"totals\" type=\"Totals\"/>\n" +
            "    </transfer>\n" +
            "</transfers>"
        );

        // Create mock PHP class using the transfer
        PsiElement element = findElementAtText(
            createTestFile(
                "CartFacade.php",
                phpLanguage,
                "<?php\n" +
                "namespace Pyz\\Zed\\Cart\\Business;\n" +
                "class CartFacade\n" +
                "{\n" +
                "    /**\n" +
                "     * @param \\Generated\\Shared\\Transfer\\CartTransfer $cartTransfer\n" +
                "     */\n" +
                "    public function addItem($cartTransfer)\n" +
                "    {\n" +
                "        // Add item to cart\n" +
                "    }\n" +
                "}\n"
            ),
            "CartTransfer"
        );
        assertNotNull("Should find transfer reference", element);

        // Test navigation
        PsiElement[] targets = handler.getGotoDeclarationTargets(element, 0, null);
        assertNotNull("Should find navigation targets", targets);
        assertTrue("Should find at least one target", targets.length > 0);
        
        boolean foundXmlDefinition = false;
        for (PsiElement target : targets) {
            if (target == null) {
                continue;
            }
            
            if (target.getContainingFile() != null && 
                target.getContainingFile().getName().endsWith(".transfer.xml")) {
                foundXmlDefinition = true;
                assertTrue(
                    "Target XML should contain transfer definition",
                    target.getContainingFile().getText().contains("transfer name=\"Cart\"")
                );
            } else {
                // Check if it's our custom TransferFileReference
                if (target.getClass().getName().contains("TransferFileReference") &&
                    target.toString().contains("CartTransfer")) {
                    foundXmlDefinition = true;
                }
            }
        }
        assertTrue("Should find XML transfer definition", foundXmlDefinition);
    }

    public void testNavigationFromTransferXmlToPhpClass() {
        // Create mock transfer class
        createTestFile(
            "ProductTransfer.php",
            phpLanguage,
            "<?php\n" +
            "namespace Generated\\Shared\\Transfer;\n" +
            "class ProductTransfer extends AbstractTransfer\n" +
            "{\n" +
            "    public function getSku(): string\n" +
            "    {\n" +
            "        return $this->sku;\n" +
            "    }\n" +
            "}\n"
        );

        // Create mock transfer definition with reference
        PsiElement element = findElementAtText(
            createTestFile(
                "ProductTransfer.transfer.xml",
                xmlLanguage,
                "<?xml version=\"1.0\"?>\n" +
                "<transfers xmlns=\"spryker:transfer-01\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"spryker:transfer-01 http://static.spryker.com/transfer-01.xsd\">\n" +
                "    <transfer name=\"Product\">\n" +
                "        <property name=\"sku\" type=\"string\"/>\n" +
                "        <property name=\"price\" type=\"int\"/>\n" +
                "    </transfer>\n" +
                "</transfers>"
            ),
            "Product"
        );
        assertNotNull("Should find transfer name", element);

        // Test navigation
        PsiElement[] targets = handler.getGotoDeclarationTargets(element, 0, null);
        assertNotNull("Should find navigation targets", targets);
        assertTrue("Should find at least one target", targets.length > 0);

        boolean foundPhpClass = false;
        for (PsiElement target : targets) {
            if (target != null && target.getContainingFile() != null && 
                target.getContainingFile().getName().equals("ProductTransfer.php")) {
                foundPhpClass = true;
                assertTrue(
                    "Target should be ProductTransfer class",
                    target.getContainingFile().getText().contains("class ProductTransfer")
                );
            }
        }
        assertTrue("Should find PHP transfer class", foundPhpClass);
    }

    public void testNavigationFromTransferXmlPropertyType() {
        // Create mock transfer definition with property type reference
        PsiElement element = findElementAtText(
            createTestFile(
                "OrderTransfer.transfer.xml",
                xmlLanguage,
                "<?xml version=\"1.0\"?>\n" +
                "<transfers xmlns=\"spryker:transfer-01\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"spryker:transfer-01 http://static.spryker.com/transfer-01.xsd\">\n" +
                "    <transfer name=\"Order\">\n" +
                "        <property name=\"items\" type=\"OrderItem[]\"/>\n" +
                "        <property name=\"customer\" type=\"Customer\"/>\n" +
                "    </transfer>\n" +
                "</transfers>"
            ),
            "Customer"
        );
        assertNotNull("Should find transfer type reference", element);

        // Test navigation
        PsiElement[] targets = handler.getGotoDeclarationTargets(element, 0, null);
        assertNotNull("Should find navigation targets", targets);
    }

    public void testFeatureToggle() {
        // Disable the feature
        AppSettingsState.getInstance().transferObjectGotoHandlingFeatureActive = false;

        // Create mock PHP class using the transfer
        PsiElement element = findElementAtText(
            createTestFile(
                "CartFacade.php",
                phpLanguage,
                "<?php\n" +
                "namespace Pyz\\Zed\\Cart\\Business;\n" +
                "class CartFacade\n" +
                "{\n" +
                "    /**\n" +
                "     * @param \\Generated\\Shared\\Transfer\\CartTransfer $cartTransfer\n" +
                "     */\n" +
                "    public function addItem($cartTransfer)\n" +
                "    {\n" +
                "        // Add item to cart\n" +
                "    }\n" +
                "}\n"
            ),
            "CartTransfer"
        );
        assertNotNull("Should find transfer reference", element);

        // Test navigation when feature is disabled
        PsiElement[] targets = handler.getGotoDeclarationTargets(element, 0, null);
        assertNull("Should not provide navigation when feature is disabled", targets);
    }

    public void testIgnoreAbstractTransfer() {
        // Create mock PHP class using AbstractTransfer
        PsiElement element = findElementAtText(
            createTestFile(
                "ProductTransfer.php",
                phpLanguage,
                "<?php\n" +
                "namespace Generated\\Shared\\Transfer;\n" +
                "class ProductTransfer extends AbstractTransfer\n" +
                "{\n" +
                "}\n"
            ),
            "AbstractTransfer"
        );
        assertNotNull("Should find AbstractTransfer reference", element);

        // Test navigation - should ignore AbstractTransfer
        PsiElement[] targets = handler.getGotoDeclarationTargets(element, 0, null);
        assertNull("Should not provide navigation for AbstractTransfer", targets);
    }
}
