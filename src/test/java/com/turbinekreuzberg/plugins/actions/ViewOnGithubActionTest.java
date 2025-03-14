package com.turbinekreuzberg.plugins.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.ide.BrowserUtil;
import org.mockito.MockedStatic;
import java.net.URI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import org.mockito.Mockito;

import java.io.File;

public class ViewOnGithubActionTest extends PyzPluginTestCase {
    private ViewOnGithubAction action;
    private AnActionEvent event;
    private Project project;
    private VirtualFile virtualFile;
    private VirtualFile composerLockFile;
    private PsiFile composerLockPsiFile;
    private PsiManager psiManager;
    private String expectedPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        action = new ViewOnGithubAction();
        AppSettingsState.getInstance().viewOnGithubFeatureActive = true;

        // Create test project structure
        myFixture.getTempDirFixture().findOrCreateDir("vendor/spryker");
        
        // Mock components
        event = Mockito.mock(AnActionEvent.class);
        project = Mockito.mock(Project.class);
        virtualFile = Mockito.mock(VirtualFile.class);
        composerLockFile = Mockito.mock(VirtualFile.class);
        composerLockPsiFile = Mockito.mock(PsiFile.class);

        // Setup basic mocks
        Mockito.when(event.getProject()).thenReturn(project);
        Mockito.when(event.getData(PlatformDataKeys.VIRTUAL_FILE)).thenReturn(virtualFile);
        Mockito.when(project.getBasePath()).thenReturn(myFixture.getTempDirPath());
        
        // Mock presentation
        com.intellij.openapi.actionSystem.Presentation presentation = new com.intellij.openapi.actionSystem.Presentation();
        Mockito.when(event.getPresentation()).thenReturn(presentation);

        // Mock PsiManager
        psiManager = Mockito.mock(PsiManager.class);
        try (MockedStatic<PsiManager> psiManagerMock = Mockito.mockStatic(PsiManager.class)) {
            psiManagerMock.when(() -> PsiManager.getInstance(project)).thenReturn(psiManager);
        }
    }

    public void testActionVisibilityWithFeatureDisabled() {
        // Disable the feature
        AppSettingsState.getInstance().viewOnGithubFeatureActive = false;

        // Action should not be visible when feature is disabled
        action.update(event);
        assertFalse("Action should not be visible when feature is disabled", event.getPresentation().isVisible());
    }

    public void testActionVisibilityWithSprykerFile() {
        // Setup Spryker vendor path
        String filePath = "/project/root/vendor/spryker/catalog/src/Spryker/Zed/Catalog/Business/CatalogFacade.php";
        Mockito.when(virtualFile.getPath()).thenReturn(filePath);
        Mockito.when(virtualFile.getCanonicalPath()).thenReturn(filePath);
        
        // Mock parent directory
        VirtualFile parentDir = Mockito.mock(VirtualFile.class);
        Mockito.when(parentDir.getCanonicalPath()).thenReturn("/project/root/vendor/spryker/catalog/src/Spryker/Zed/Catalog/Business");
        Mockito.when(virtualFile.getParent()).thenReturn(parentDir);
        
        Mockito.when(virtualFile.getFileType()).thenReturn(Mockito.mock(com.intellij.openapi.fileTypes.FileType.class));
        
        // Action should be visible for Spryker vendor files
        action.update(event);
        assertTrue("Action should be visible for Spryker vendor files", event.getPresentation().isVisible());
    }

    public void testActionVisibilityWithNonSprykerFile() {
        // Setup non-Spryker path
        String filePath = "/project/root/src/Pyz/Zed/Catalog/Business/CatalogFacade.php";
        Mockito.when(virtualFile.getPath()).thenReturn(filePath);
        Mockito.when(virtualFile.getCanonicalPath()).thenReturn(filePath);
        
        // Mock parent directory
        VirtualFile parentDir = Mockito.mock(VirtualFile.class);
        Mockito.when(parentDir.getCanonicalPath()).thenReturn("/project/root/src/Pyz/Zed/Catalog/Business");
        Mockito.when(virtualFile.getParent()).thenReturn(parentDir);
        
        // Action should not be visible for non-Spryker files
        action.update(event);
        assertFalse("Action should not be visible for non-Spryker files", event.getPresentation().isVisible());
    }

    public void testActionVisibilityWithUnknownFileType() {
        // Setup unknown file type
        Mockito.when(virtualFile.getFileType()).thenReturn(UnknownFileType.INSTANCE);
        
        // Action should not be visible for unknown file types
        action.update(event);
        assertFalse("Action should not be visible for unknown file types", event.getPresentation().isVisible());
    }

    public void testPackageVersionFromComposerLock() throws Exception {
        // Create composer.lock file with package version
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode composerLock = mapper.createObjectNode();
        ObjectNode packageInfo = mapper.createObjectNode();
        packageInfo.put("name", "spryker/catalog");
        packageInfo.put("version", "1.2.3");
        composerLock.putArray("packages").add(packageInfo);

        // Create composer.lock file in the test project
        VirtualFile composerLockFile = myFixture.getTempDirFixture().createFile("composer.lock", composerLock.toString());

        // Create Spryker vendor file structure
        myFixture.getTempDirFixture().findOrCreateDir("vendor/spryker/catalog/src/Spryker/Zed/Catalog/Business");
        VirtualFile sprykerFile = myFixture.getTempDirFixture().createFile(
            "vendor/spryker/catalog/src/Spryker/Zed/Catalog/Business/CatalogFacade.php",
            "<?php\nnamespace Spryker\\Zed\\Catalog\\Business;\nclass CatalogFacade {}"
        );

        // Mock file system access
        Mockito.when(virtualFile.getPath()).thenReturn(sprykerFile.getPath());
        Mockito.when(virtualFile.getCanonicalPath()).thenReturn(sprykerFile.getPath());
        Mockito.when(virtualFile.getParent()).thenReturn(sprykerFile.getParent());

        // Mock LocalFileSystem and PsiManager
        try (MockedStatic<LocalFileSystem> localFileSystemMock = Mockito.mockStatic(LocalFileSystem.class);
             MockedStatic<PsiManager> psiManagerMock = Mockito.mockStatic(PsiManager.class);
             MockedStatic<BrowserUtil> browserUtilMock = Mockito.mockStatic(BrowserUtil.class)) {
            LocalFileSystem localFileSystem = Mockito.mock(LocalFileSystem.class);
            localFileSystemMock.when(LocalFileSystem::getInstance).thenReturn(localFileSystem);
            Mockito.when(localFileSystem.findFileByIoFile(Mockito.any())).thenReturn(composerLockFile);

            psiManagerMock.when(() -> PsiManager.getInstance(project)).thenReturn(psiManager);
            Mockito.when(psiManager.findFile(composerLockFile)).thenReturn(composerLockPsiFile);
            Mockito.when(composerLockPsiFile.getText()).thenReturn(composerLock.toString());

            // Action should be visible
            action.update(event);
            assertTrue("Action should be visible for Spryker vendor files", event.getPresentation().isVisible());

            // Execute action and verify URL
            action.actionPerformed(event);
            browserUtilMock.verify(() -> BrowserUtil.browse(
                URI.create("https://github.com/spryker/catalog/blob/1.2.3/src/Spryker/Zed/Catalog/Business/CatalogFacade.php")
            ));
        }
    }
}
