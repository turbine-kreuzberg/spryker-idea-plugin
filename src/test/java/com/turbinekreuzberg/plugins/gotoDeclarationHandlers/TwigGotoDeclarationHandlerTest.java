package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;

public class TwigGotoDeclarationHandlerTest extends PyzPluginTestCase {
    private Language twigLanguage;
    private Language phpLanguage;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twigLanguage = Language.findLanguageByID("Twig");
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("Twig language support should be available", twigLanguage);
        assertNotNull("PHP language support should be available", phpLanguage);
    }
    
    public void testNavigationToIncludedTemplate() {
        // Create mock Twig templates
        PsiFile mainTemplate = createTestFile(
            "page-layout-main.twig",
            twigLanguage,
            "{% extends molecule('main-layout') %}\n" +
            "{% block content %}\n" +
            "    {% include molecule('header') %}\n" +
            "    {{ parent() }}\n" +
            "{% endblock %}"
        );

        createTestFile(
            "header.twig",
            twigLanguage,
            "{% extends model('component') %}\n" +
            "{% block body %}\n" +
            "    <header>...</header>\n" +
            "{% endblock %}"
        );

        TwigGotoHandler handler = new TwigGotoHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
            findElementAtText(mainTemplate, "header"),
            0,
            myFixture.getEditor()
        );

        assertNotNull("Should find navigation targets", targets);
        assertEquals("Should find exactly one target", 1, targets.length);
        assertTrue(
            "Target should be the header template",
            targets[0].getContainingFile().getName().equals("header.twig")
        );
    }

    public void testNavigationToWidget() {
        // Create mock Twig template with widget
        PsiFile template = createTestFile(
            "cart.twig",
            twigLanguage,
            "{% extends template('page-layout-main') %}\n" +
            "{% block content %}\n" +
            "    {% widget 'CartItemsWidget' %}\n" +
            "    {% endwidget %}\n" +
            "{% endblock %}"
        );

        // Create mock widget class
        createTestFile(
            "CartItemsWidget.php",
            phpLanguage,
            "<?php\n" +
            "namespace Pyz\\Yves\\CartPage\\Widget;\n" +
            "use SprykerShop\\Yves\\ShopApplication\\Widget\\AbstractWidget;\n" +
            "class CartItemsWidget extends AbstractWidget\n" +
            "{\n" +
            "    public function __construct()\n" +
            "    {\n" +
            "        $this->addParameter('items', []);\n" +
            "    }\n" +
            "}"
        );

        // Create mock widget template
        createTestFile(
            "cart-items.twig",
            twigLanguage,
            "{% extends template('widget') %}\n" +
            "{% block body %}\n" +
            "    <div class=\"cart-items\">...</div>\n" +
            "{% endblock %}"
        );

        TwigGotoHandler handler = new TwigGotoHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
            findElementAtText(template, "CartItemsWidget"),
            0,
            myFixture.getEditor()
        );

        assertNotNull("Should find navigation targets", targets);
        assertTrue("Should find both PHP class and template", targets.length == 2);
        
        boolean foundPhpClass = false;
        boolean foundTemplate = false;
        
        for (PsiElement target : targets) {
            String fileName = target.getContainingFile().getName();
            if (fileName.equals("CartItemsWidget.php")) {
                foundPhpClass = true;
            } else if (fileName.equals("cart-items.twig")) {
                foundTemplate = true;
            }
        }
        
        assertTrue("Should find widget PHP class", foundPhpClass);
        assertTrue("Should find widget template", foundTemplate);
    }
}
