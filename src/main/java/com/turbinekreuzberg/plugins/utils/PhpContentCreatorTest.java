package com.turbinekreuzberg.plugins.utils;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;


public class PhpContentCreatorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testCreatePhpClass(){
        // Arrange
        PsiFile baseClass = getPsiFile("ExampleFacade.php");
        String expectedFileContent = getFileContent("ExpectedExtendedExampleFacade.php");

        // Act
        String createdFileContent = new PhpContentCreator().create(baseClass, "Zed/Example");

        // Assert
        assertEquals(expectedFileContent, createdFileContent);
    }

    public void testCreatePhpInterface(){
        // Arrange
        PsiFile baseClass = getPsiFile("ExampleFacadeInterface.php");
        String expectedFileContent = getFileContent("ExpectedExtendedExampleFacadeInterface.php");

        // Act
        String createdFileContent = new PhpContentCreator().create(baseClass, "Zed/Example");

        // Assert
        assertEquals(expectedFileContent, createdFileContent);
    }

    public void testCreatePhpTrait(){
        // Arrange
        PsiFile baseClass = getPsiFile("ExampleTrait.php");
        String expectedFileContent = getFileContent("ExpectedExtendedExampleTrait.php");

        // Act
        String createdFileContent = new PhpContentCreator().create(baseClass, "Zed/Example");

        // Assert
        assertEquals(expectedFileContent, createdFileContent);
    }

    public void testCreatePhpAbstractClass(){
        // Arrange
        PsiFile baseClass = getPsiFile("AbstractExample.php");
        String expectedFileContent = getFileContent("ExpectedExtendedAbstractExample.php");

        // Act
        String createdFileContent = new PhpContentCreator().create(baseClass, "Zed/Example");

        // Assert
        assertEquals(expectedFileContent, createdFileContent);
    }

    private PsiFile getPsiFile(String fileName) {
        VirtualFile vfile = VirtualFileManager.getInstance().findFileByUrl("file://src/tests/fixtures/PhpContentCreator/" + fileName);
        return getPsiManager().findFile(vfile);
    }

    private String getFileContent(String fileName) {
        VirtualFile expectedVirtualFile = VirtualFileManager.getInstance().findFileByUrl("file://src/tests/fixtures/PhpContentCreator/" + fileName);
        return LoadTextUtil.loadText(expectedVirtualFile).toString();
    }
}
