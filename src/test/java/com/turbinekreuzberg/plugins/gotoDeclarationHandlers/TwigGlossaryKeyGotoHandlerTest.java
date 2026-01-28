package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;

public class TwigGlossaryKeyGotoHandlerTest extends PyzPluginTestCase {
    private Language twigLanguage;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twigLanguage = Language.findLanguageByID("Twig");
        assertNotNull("Twig language support should be available", twigLanguage);
    }
    
    public void testNavigationToGlossaryKey() {
        PsiFile twigTemplate = createTestFile(
            "product.twig",
            twigLanguage,
            "{% extends template('page-layout-main') %}\n" +
            "{% block content %}\n" +
            "    <h1>{{ 'product.name' | trans }}</h1>\n" +
            "    <p>{{ 'product.description' | trans }}</p>\n" +
            "{% endblock %}"
        );

        PsiFile glossaryFile = createTestFile(
            "glossary.csv",
            Language.ANY,
            "product.name,Product Name\n" +
            "product.description,Product Description\n" +
            "cart.total,Total Amount\n"
        );

        TwigGlossaryKeyGotoHandler handler = new TwigGlossaryKeyGotoHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
            findElementAtText(twigTemplate, "product.name"),
            0,
            myFixture.getEditor()
        );

        assertNotNull("Should find navigation targets", targets);
        assertEquals("Should find exactly one target", 1, targets.length);
        assertTrue(
            "Target should be in glossary.csv",
            targets[0].getContainingFile().getName().equals("glossary.csv")
        );
        assertTrue(
            "Target text should contain the glossary key",
            targets[0].getText().startsWith("product.name,")
        );
    }

    public void testNoNavigationForNonExistentKey() {
        PsiFile twigTemplate = createTestFile(
            "error.twig",
            twigLanguage,
            "{% block content %}\n" +
            "    <p>{{ 'non.existent.key' | trans }}</p>\n" +
            "{% endblock %}"
        );

        createTestFile(
            "glossary.csv",
            Language.ANY,
            "existing.key,Existing Value\n" +
            "another.key,Another Value\n"
        );

        TwigGlossaryKeyGotoHandler handler = new TwigGlossaryKeyGotoHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
            findElementAtText(twigTemplate, "non.existent.key"),
            0,
            myFixture.getEditor()
        );

        assertNull("Should not find navigation targets for non-existent key", targets);
    }

    public void testNoNavigationForNonTwigFiles() {
        PsiFile phpFile = createTestFile(
            "Controller.php",
            Language.findLanguageByID("PHP"),
            "<?php\n" +
            "class Controller {\n" +
            "    public function index() {\n" +
            "        return 'product.name';\n" +
            "    }\n" +
            "}"
        );

        createTestFile(
            "glossary.csv",
            Language.ANY,
            "product.name,Product Name\n"
        );

        TwigGlossaryKeyGotoHandler handler = new TwigGlossaryKeyGotoHandler();
        PsiElement[] targets = handler.getGotoDeclarationTargets(
            findElementAtText(phpFile, "product.name"),
            0,
            myFixture.getEditor()
        );

        assertNull("Should not find navigation targets in non-Twig files", targets);
    }
}
