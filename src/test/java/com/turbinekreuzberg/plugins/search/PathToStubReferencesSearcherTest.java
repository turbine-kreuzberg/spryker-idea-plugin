package com.turbinekreuzberg.plugins.search;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.mockito.Mockito;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;

public class PathToStubReferencesSearcherTest extends PyzPluginTestCase {
    private Language phpLanguage;
    private PathToStubReferencesSearcher searcher;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        phpLanguage = Language.findLanguageByID("PHP");
        assertNotNull("PHP language support should be available", phpLanguage);
        searcher = new PathToStubReferencesSearcher();
        AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive = true;
    }

    public void testGatewayPathConversion() throws Exception {
        // Create test file content with proper namespace structure
        String fileContent = "<?php\n" +
                "namespace Pyz\\Zed\\Cart\\Communication\\Controller;\n\n" +
                "class GatewayController\n" +
                "{\n" +
                "    /**\n" +
                "     * @return void\n" +
                "     */\n" +
                "    public function addItemAction()\n" +
                "    {\n" +
                "        // Add item to cart\n" +
                "    }\n" +
                "}";

        // Configure the test file
        myFixture.configureByText("GatewayController.php", fileContent);
        
        // Find the method in the file
        PsiElement element = myFixture.findElementByText("addItemAction", MethodImpl.class);
        assertNotNull("Should find gateway action", element);
        assertTrue("Element should be a MethodImpl", element instanceof MethodImpl);
        MethodImpl gatewayMethod = (MethodImpl) element;

        // Mock search parameters
        ReferencesSearch.SearchParameters params = Mockito.mock(ReferencesSearch.SearchParameters.class);
        Mockito.when(params.getElementToSearch()).thenReturn(gatewayMethod);

        // Verify module name extraction
        String moduleName = invokePrivateMethod("getModuleName", gatewayMethod);
        assertEquals("Module name should be extracted correctly", "Cart", moduleName);

        // Verify method name extraction
        String methodName = invokePrivateMethod("getMethodName", gatewayMethod);
        assertEquals("Method name should be extracted correctly", "addItem", methodName);
    }

    private String invokePrivateMethod(String methodName, Object... args) throws Exception {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        Method method = PathToStubReferencesSearcher.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return (String) method.invoke(searcher, args);
    }

    public void testCamelCaseToKebabCase() throws Exception {
        // Create test file content with proper namespace structure
        String fileContent = "<?php\n" +
                "namespace Pyz\\Zed\\ProductCategory\\Communication\\Controller;\n\n" +
                "class GatewayController\n" +
                "{\n" +
                "    /**\n" +
                "     * @return void\n" +
                "     */\n" +
                "    public function assignProductToCategoryAction()\n" +
                "    {\n" +
                "        // Assign product to category\n" +
                "    }\n" +
                "}";

        // Configure the test file
        myFixture.configureByText("GatewayController.php", fileContent);
        
        // Find the method in the file
        PsiElement element = myFixture.findElementByText("assignProductToCategoryAction", MethodImpl.class);
        assertNotNull("Should find gateway action", element);
        assertTrue("Element should be a MethodImpl", element instanceof MethodImpl);
        MethodImpl gatewayMethod = (MethodImpl) element;

        // Verify complex camelCase to kebab-case conversion
        String moduleName = invokePrivateMethod("getModuleName", gatewayMethod);
        String methodName = invokePrivateMethod("getMethodName", gatewayMethod);
        
        assertEquals("Module name should be extracted correctly", "ProductCategory", moduleName);
        assertEquals("Method name should be extracted correctly", "assignProductToCategory", methodName);

        // Test the private kebab case conversion
        assertEquals("Module name should be converted to kebab-case", "product-category", invokePrivateMethod("convertCamelCaseToKebabCase", moduleName));
        assertEquals("Method name should be converted to kebab-case", "assign-product-to-category", invokePrivateMethod("convertCamelCaseToKebabCase", methodName));
    }

    public void testFeatureToggle() {
        // Disable the feature
        AppSettingsState.getInstance().zedStubGatewayControllerFeatureActive = false;

        // Create test file content with proper namespace structure
        String fileContent = "<?php\n" +
                "namespace Pyz\\Zed\\Cart\\Communication\\Controller;\n\n" +
                "class GatewayController\n" +
                "{\n" +
                "    /**\n" +
                "     * @return void\n" +
                "     */\n" +
                "    public function addItemAction()\n" +
                "    {\n" +
                "        // Add item to cart\n" +
                "    }\n" +
                "}";

        // Configure the test file
        myFixture.configureByText("GatewayController.php", fileContent);
        
        // Find the method in the file
        PsiElement element = myFixture.findElementByText("addItemAction", MethodImpl.class);
        assertNotNull("Should find gateway action", element);
        assertTrue("Element should be a MethodImpl", element instanceof MethodImpl);
        MethodImpl gatewayMethod = (MethodImpl) element;

        // Mock search parameters
        ReferencesSearch.SearchParameters params = Mockito.mock(ReferencesSearch.SearchParameters.class);
        Mockito.when(params.getElementToSearch()).thenReturn(gatewayMethod);

        // Execute search - should do nothing when feature is disabled
        searcher.processQuery(params, reference -> false);
    }

    public void testNonGatewayControllerMethod() {
        // Create test file content with proper namespace structure
        String fileContent = "<?php\n" +
                "namespace Pyz\\Zed\\Cart\\Business;\n\n" +
                "class CartFacade\n" +
                "{\n" +
                "    /**\n" +
                "     * @param string $sku\n" +
                "     * @return void\n" +
                "     */\n" +
                "    public function addItem($sku)\n" +
                "    {\n" +
                "        // Add item to cart\n" +
                "    }\n" +
                "}";

        // Configure the test file
        myFixture.configureByText("CartFacade.php", fileContent);
        
        // Find the method in the file
        PsiElement element = myFixture.findElementByText("addItem", MethodImpl.class);
        assertNotNull("Should find regular method", element);
        assertTrue("Element should be a MethodImpl", element instanceof MethodImpl);

        // Mock search parameters
        ReferencesSearch.SearchParameters params = Mockito.mock(ReferencesSearch.SearchParameters.class);
        Mockito.when(params.getElementToSearch()).thenReturn(element);

        // Execute search - should do nothing for non-gateway methods
        searcher.processQuery(params, reference -> false);
    }
}
