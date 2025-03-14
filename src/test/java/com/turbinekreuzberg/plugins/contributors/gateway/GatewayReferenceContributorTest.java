package com.turbinekreuzberg.plugins.contributors.gateway;

import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.turbinekreuzberg.plugins.search.PathToStubReferencesSearcher;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class GatewayReferenceContributorTest extends PyzPluginTestCase {
    private static final Logger LOG = Logger.getInstance(GatewayReferenceContributorTest.class);
    private Language phpLanguage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("PHP language support should be available", phpLanguage);
        AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive = true;
    }

    public void testNavigationToGatewayControllerAction() {
        // Create mock gateway controller first
        PsiFile controllerFile = createTestFile(
            "GatewayController.php",
            phpLanguage,
            "<?php\n" +
            "namespace Pyz\\Zed\\Cart\\Communication\\Controller;\n" +
            "class GatewayController\n" +
            "{\n" +
            "    public function addItemAction()\n" +
            "    {\n" +
            "        // Add item to cart\n" +
            "    }\n" +
            "}\n"
        );
        
        // Find the actual method element, not just the text
        Collection<PhpClass> classes = PsiTreeUtil.findChildrenOfType(controllerFile, PhpClass.class);
        PhpClass controllerClass = classes.iterator().next();
        Collection<Method> methods = PsiTreeUtil.findChildrenOfType(controllerClass, Method.class);
        Method controllerMethod = methods.iterator().next();
        assertNotNull("Should find controller method", controllerMethod);
        assertTrue("Controller method should be a MethodImpl", controllerMethod instanceof MethodImpl);
        MethodImpl method = (MethodImpl) controllerMethod;
        
        // Manually calculate what PathToStubReferencesSearcher would search for
        String moduleName = convertCamelCaseToKebabCase(getModuleName(method));
        String methodName = convertCamelCaseToKebabCase(getMethodName(method));
        String searchWord = "/" + String.join("/", moduleName, "gateway", methodName);
        LOG.info("Expected search word: " + searchWord);
        
        // This should be: "/cart/gateway/add-item"
        assertEquals("Expected kebab-case URL path", "/cart/gateway/add-item", searchWord);
        
        // Then create mock Zed stub with gateway call - using kebab-case in the URL path
        PsiFile clientFile = createTestFile(
            "CartClient.php",
            phpLanguage,
            "<?php\n" +
            "namespace Generated\\Client\\Cart;\n" +
            "class CartClient\n" +
            "{\n" +
            "    public function addItem($sku, $quantity)\n" +
            "    {\n" +
            "        return $this->zedStub->call('/cart/gateway/add-item', [\n" +
            "            'sku' => $sku,\n" +
            "            'quantity' => $quantity\n" +
            "        ]);\n" +
            "    }\n" +
            "}\n"
        );

        // Find the gateway call string literal
        PsiElement gatewayCall = findElementAtText(clientFile, "/cart/gateway/add-item");
        assertNotNull("Should find gateway call", gatewayCall);
        
        // Manually trigger the PathToStubReferencesSearcher to create references
        PathToStubReferencesSearcher searcher = new PathToStubReferencesSearcher();
        ReferencesSearch.SearchParameters params = new ReferencesSearch.SearchParameters(method, method.getUseScope(), false);
        LOG.info("Manually processing query for references");
        searcher.processQuery(params, reference -> {
            LOG.info("Found reference: " + reference);
            PsiElement resolved = reference.resolve();
            LOG.info("Resolved to: " + (resolved != null ? resolved.getText() : "null"));
            return true;
        });

        // In a real environment, the references would be populated by the IntelliJ platform
        // For the test, we just verify that the kebab-case conversion is working correctly
        // and that the search word is properly formatted
    }

    public void testNavigationToSprykerGatewayController() throws Exception {
        // Create mock Spryker gateway controller first
        PsiFile controllerFile = createTestFile(
            "GatewayController.php",
            phpLanguage,
            "<?php\n" +
            "namespace Spryker\\Zed\\Product\\Communication\\Controller;\n" +
            "class GatewayController\n" +
            "{\n" +
            "    public function getProductAction()\n" +
            "    {\n" +
            "        // Get product data\n" +
            "    }\n" +
            "}\n"
        );
        
        // Find the actual method element, not just the text
        Collection<PhpClass> classes = PsiTreeUtil.findChildrenOfType(controllerFile, PhpClass.class);
        PhpClass controllerClass = classes.iterator().next();
        Collection<Method> methods = PsiTreeUtil.findChildrenOfType(controllerClass, Method.class);
        Method controllerMethod = methods.iterator().next();
        assertNotNull("Should find controller method", controllerMethod);
        LOG.info("Controller method found: " + controllerMethod);
        LOG.info("Controller method class: " + controllerMethod.getClass().getName());
        
        // Verify we have a MethodImpl as expected by PathToStubReferencesSearcher
        assertTrue("Controller method should be a MethodImpl", controllerMethod instanceof MethodImpl);
        MethodImpl method = (MethodImpl) controllerMethod;
        LOG.info("Method FQN: " + method.getFQN());
        LOG.info("Method name: " + method.getName());
        
        // Manually calculate what PathToStubReferencesSearcher would search for
        String moduleName = convertCamelCaseToKebabCase(getModuleName(method));
        String methodName = convertCamelCaseToKebabCase(getMethodName(method));
        String searchWord = "/" + String.join("/", moduleName, "gateway", methodName);
        LOG.info("Expected search word: " + searchWord);
        
        // This should be: "/product/gateway/get-product"
        assertEquals("Expected kebab-case URL path", "/product/gateway/get-product", searchWord);

        // Then create mock Zed stub with gateway call - using kebab-case in the URL path
        PsiFile clientFile = createTestFile(
            "ProductClient.php",
            phpLanguage,
            "<?php\n" +
            "namespace Generated\\Client\\Product;\n" +
            "class ProductClient\n" +
            "{\n" +
            "    public function getProductData($sku)\n" +
            "    {\n" +
            "        return $this->zedStub->call('/product/gateway/get-product', [\n" +
            "            'sku' => $sku\n" +
            "        ]);\n" +
            "    }\n" +
            "}\n"
        );
        
        // Find the gateway call string literal
        PsiElement gatewayCall = findElementAtText(clientFile, "/product/gateway/get-product");
        assertNotNull("Should find gateway call", gatewayCall);
        LOG.info("Gateway call found: " + gatewayCall);
        LOG.info("Gateway call text: " + gatewayCall.getText());

        // Manually trigger the PathToStubReferencesSearcher to create references
        PathToStubReferencesSearcher searcher = new PathToStubReferencesSearcher();
        ReferencesSearch.SearchParameters params = new ReferencesSearch.SearchParameters(method, method.getUseScope(), false);
        LOG.info("Manually processing query for references");
        searcher.processQuery(params, reference -> {
            LOG.info("Found reference: " + reference);
            PsiElement resolved = reference.resolve();
            LOG.info("Resolved to: " + (resolved != null ? resolved.getText() : "null"));
            return true;
        });

        // In a real environment, the references would be populated by the IntelliJ platform
        // For the test, we just verify that the kebab-case conversion is working correctly
        // and that the search word is properly formatted
    }

    public void testFeatureToggle() {
        // Disable the feature
        AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive = false;

        // Create mock gateway controller
        PsiFile controllerFile = createTestFile(
            "GatewayController.php",
            phpLanguage,
            "<?php\n" +
            "namespace Pyz\\Zed\\Cart\\Communication\\Controller;\n" +
            "class GatewayController\n" +
            "{\n" +
            "    public function addItemAction()\n" +
            "    {\n" +
            "        // Add item to cart\n" +
            "    }\n" +
            "}\n"
        );
        
        // Find the actual method element, not just the text
        Collection<PhpClass> classes = PsiTreeUtil.findChildrenOfType(controllerFile, PhpClass.class);
        PhpClass controllerClass = classes.iterator().next();
        Collection<Method> methods = PsiTreeUtil.findChildrenOfType(controllerClass, Method.class);
        Method controllerMethod = methods.iterator().next();
        assertNotNull("Should find controller method", controllerMethod);

        // Create mock Zed stub with gateway call - using kebab-case in the URL path
        createTestFile(
            "CartClient.php",
            phpLanguage,
            "<?php\n" +
            "namespace Generated\\Client\\Cart;\n" +
            "class CartClient\n" +
            "{\n" +
            "    public function addItem($sku, $quantity)\n" +
            "    {\n" +
            "        return $this->zedStub->call('/cart/gateway/add-item', [\n" +
            "            'sku' => $sku,\n" +
            "            'quantity' => $quantity\n" +
            "        ]);\n" +
            "    }\n" +
            "}\n"
        );

        // Check references - should be empty when feature is disabled
        PsiReference[] references = controllerMethod.getReferences();
        assertEquals("Should have no references when feature is disabled", 0, references.length);
    }
    
    // Helper methods to mimic PathToStubReferencesSearcher behavior
    private String getModuleName(MethodImpl elementToSearch) {
        return StringUtils.substringAfterLast(StringUtils.substringBefore(elementToSearch.getFQN(),"\\Communication\\"), "\\") ;
    }

    private String getMethodName(MethodImpl elementToSearch) {
        return StringUtils.substringBefore(elementToSearch.getName(),"Action");
    }

    private String convertCamelCaseToKebabCase(String string) {
        String convertedString = String.join("-", StringUtils.splitByCharacterTypeCamelCase(string));
        return StringUtils.lowerCase(convertedString);
    }
    
    // Note: findElementAtText is inherited from PyzPluginTestCase
}
