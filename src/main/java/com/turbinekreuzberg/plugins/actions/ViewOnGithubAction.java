package com.turbinekreuzberg.plugins.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.turbinekreuzberg.plugins.settings.AppSettingsState;
import com.turbinekreuzberg.plugins.utils.SprykerRelativeClassPathCreator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;

public class ViewOnGithubAction extends AnAction {

    SprykerRelativeClassPathCreator sprykerRelativeClassPathCreator;
    public ViewOnGithubAction() {
        sprykerRelativeClassPathCreator = new SprykerRelativeClassPathCreator();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        VirtualFile selectedVirtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        assert selectedVirtualFile != null;
        viewFileOnGithub(project, selectedVirtualFile);
    }

    private void viewFileOnGithub(Project project, @NotNull VirtualFile selectedVirtualFile) {
        String[] vendorSplitArray = selectedVirtualFile.getPath().split("vendor");
        if (vendorSplitArray.length != 2) {
            return;
        }

        String[] srcSplitArray = vendorSplitArray[1].split("src");
        if (srcSplitArray.length != 2) {
            return;
        }

        String selectedFilePackageName = srcSplitArray[0].substring(1, srcSplitArray[0].length() - 1);
        String tag = this.getInstalledPackageVersion(project, selectedFilePackageName);
        String fullUrl = "https://github.com" + srcSplitArray[0] + "blob/" + tag + "/src" + srcSplitArray[1];

        BrowserUtil.browse(URI.create(fullUrl));
    }

    private String getInstalledPackageVersion(@NotNull Project project, String selectedFilePackageName) {
        File file = new File(project.getBasePath() + "/composer.lock");
        VirtualFile composerLockVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (composerLockVirtualFile == null) {
            return "master";
        }

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile composerLockPsiFile = psiManager.findFile(composerLockVirtualFile);
        if (composerLockPsiFile == null) {
            return "master";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arrayNode = mapper.readTree(composerLockPsiFile.getText());
            for (JsonNode jsonNode : arrayNode.get("packages")) {
                if (jsonNode.get("name").asText().equals(selectedFilePackageName)){
                    return jsonNode.get("version").asText();
                }
            }
        } catch (JsonProcessingException ignored) {
        }

        return "master";
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        if (!AppSettingsState.getInstance().viewOnGithubFeatureActive) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        VirtualFile vFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        if (isNotFileInSprykerVendor(vFile)) {
            actionEvent.getPresentation().setVisible(false);
            return;
        }

        actionEvent.getPresentation().setVisible(true);
    }

    private boolean isNotFileInSprykerVendor(@NotNull VirtualFile vFile) {
        return vFile.getFileType() == UnknownFileType.INSTANCE || !sprykerRelativeClassPathCreator.isLocatedInSprykerVendor(vFile);
    }
}
