package com.turbinekreuzberg.plugins.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import com.turbinekreuzberg.plugins.utils.SprykerPathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoToParentAction extends AnAction {

    SprykerPathUtils sprykerPathUtils;
    public GoToParentAction() {
        sprykerPathUtils = new SprykerPathUtils();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }

        VirtualFile selectedVirtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedVirtualFile == null) {
            return;
        }

        VirtualFile parentFile = findParentVendorFile(project, selectedVirtualFile);
        if (parentFile != null) {
            new OpenFileDescriptor(project, parentFile).navigate(true);
        }
    }

    @Nullable
    private VirtualFile findParentVendorFile(Project project, VirtualFile pyzFile) {
        String pyzPath = pyzFile.getPath();
        String basePath = project.getBasePath();
        if (basePath == null) {
            return null;
        }

        // Determine if this is a test file or regular file
        boolean isTestFile = pyzPath.contains("/tests/");
        String relativePath = getRelativePath(project, isTestFile, basePath, pyzPath);
        if (relativePath == null) {
            return null;
        }

        // Find which Spryker namespace has the parent file
        String sprykerNamespace = findMatchingSprykerNamespace(pyzFile, project);
        if (sprykerNamespace == null) {
            return null;
        }

        // Construct the vendor path
        return buildParentVendorPath(project, pyzFile, sprykerNamespace, isTestFile, basePath, relativePath);
    }

    private @Nullable VirtualFile buildParentVendorPath(
            Project project,
            VirtualFile pyzFile,
            String sprykerNamespace,
            boolean isTestFile,
            String basePath,
            String relativePath
    ) {
        String sprykerPackage = convertCamelCaseToKebabCase(sprykerNamespace);
        String module = extractModuleFromPath(pyzFile, project);
        if (module == null) {
            return null;
        }

        String modulePackage = convertCamelCaseToKebabCase(module);
        String fullPackagePath = sprykerPackage + "/" + modulePackage;
        sprykerNamespace = isTestFile ? sprykerNamespace + "Test" : sprykerNamespace;
        String vendorPath = basePath + "/vendor/" + fullPackagePath + "/" +
            (isTestFile ? "tests" : "src") + "/" + sprykerNamespace + "/" + relativePath;

        File vendorFile = new File(vendorPath);
        if (vendorFile.exists()) {
            return pyzFile.getFileSystem().findFileByPath(vendorPath);
        }

        return null;
    }

    private static @Nullable String getRelativePath(Project project, boolean isTestFile, String basePath, String pyzPath) {
        String pyzDirectory = isTestFile ?
            SettingsManager.getPyzTestDirectory(project) :
            SettingsManager.getPyzDirectory(project);

        // Extract the relative path after the PYZ directory
        String pyzDirPattern = basePath + pyzDirectory;
        if (!pyzPath.startsWith(pyzDirPattern)) {
            return null;
        }

        return pyzPath.substring(pyzDirPattern.length());
    }

    @Nullable
    private String extractModuleFromPath(VirtualFile file, Project project) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            return null;
        }

        // Extract module name from file path structure
        // Path structure: /src/Pyz/{ApplicationLayer}/{Module}/... or /tests/PyzTest/{ApplicationLayer}/{Module}/...
        String pathAfterPyz = getPathAfterPyz(file, project, basePath);
        if (pathAfterPyz == null) {
            return null;
        }

        // Split path to extract ApplicationLayer/Module (e.g., "Zed/Cart/Business/...")
        String[] pathParts = pathAfterPyz.split("/");
        if (pathParts.length >= 2) {
            return pathParts[1]; // Second part is the module name
        }

        return null;
    }

    private static @Nullable String getPathAfterPyz(VirtualFile file, Project project, String basePath) {
        String filePath = file.getPath();
        String pyzDirectory = SettingsManager.getPyzDirectory(project);
        String pyzTestDirectory = SettingsManager.getPyzTestDirectory(project);

        String pathAfterPyz = null;
        if (filePath.contains(basePath + pyzDirectory)) {
            pathAfterPyz = filePath.substring((basePath + pyzDirectory).length());
        } else if (filePath.contains(basePath + pyzTestDirectory)) {
            pathAfterPyz = filePath.substring((basePath + pyzTestDirectory).length());
        }

        return pathAfterPyz;
    }

    @Nullable
    private String findMatchingSprykerNamespace(VirtualFile file, Project project) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            return null;
        }

        String module = extractModuleFromPath(file, project);
        if (module == null) {
            return null;
        }
        
        // Get configured Spryker namespaces and check which one has the parent file
        String[] sprykerNamespaces = sprykerPathUtils.getSprykerNamespaces();
        for (String sprykerNamespace : sprykerNamespaces) {
            if (existsSprykerNamespace(sprykerNamespace, module, basePath)) {
                return sprykerNamespace;
            }
        }

        return null;
    }

    private @Nullable boolean existsSprykerNamespace(String sprykerNamespace, String module, String basePath) {
        // Convert namespace to package format (e.g., SprykerShop -> spryker-shop)
        String sprykerPackage = convertCamelCaseToKebabCase(sprykerNamespace);
        String modulePackage = convertCamelCaseToKebabCase(module);
        String fullPackagePath = sprykerPackage + "/" + modulePackage;
        String vendorPackagePath = basePath + "/vendor/" + fullPackagePath;

        // Check if this vendor package directory exists
        @Nullable VirtualFile vendorPackageDir = LocalFileSystem.getInstance().findFileByPath(vendorPackagePath);
        return vendorPackageDir != null && vendorPackageDir.isDirectory();
    }

    private String convertCamelCaseToKebabCase(String namespace) {
        return namespace.replaceAll("([A-Z])(?=[A-Z])", "$1_").replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null || !SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.GO_TO_PARENT)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        VirtualFile vFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        if (vFile.getFileType().getName().equals("PHP")) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        // Show action only for files in PYZ namespace (not in vendor)
        if (!isFileInPyzNamespace(project, vFile)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        // Only show action if a parent file exists
        VirtualFile parentFile = findParentVendorFile(project, vFile);
        actionEvent.getPresentation().setVisible(parentFile != null);
    }

    private boolean isFileInPyzNamespace(@NotNull Project project, @NotNull VirtualFile vFile) {
        if (vFile.getFileType() == UnknownFileType.INSTANCE) {
            return false;
        }

        String filePath = vFile.getPath();
        String basePath = project.getBasePath();
        if (basePath == null) {
            return false;
        }

        // Check if file is in PYZ directory or PYZ test directory
        String pyzDirectory = SettingsManager.getPyzDirectory(project);
        String pyzTestDirectory = SettingsManager.getPyzTestDirectory(project);

        return filePath.startsWith(basePath + pyzDirectory) || 
               filePath.startsWith(basePath + pyzTestDirectory);
    }

}
