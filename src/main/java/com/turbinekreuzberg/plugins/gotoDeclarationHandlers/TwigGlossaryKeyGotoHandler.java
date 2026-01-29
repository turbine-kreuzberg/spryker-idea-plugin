package com.turbinekreuzberg.plugins.gotoDeclarationHandlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

public class TwigGlossaryKeyGotoHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) {
            return null;
        }

        Project project = sourceElement.getProject();
        if (!SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.TWIG_GLOSSARY_KEY_GOTO_HANDLING)) {
            return null;
        }

        if (!sourceElement.getContainingFile().getName().endsWith(".twig")) {
            return null;
        }

        String glossaryKey = resolveGlossaryKey(sourceElement);
        if (glossaryKey == null) {
            return null;
        }
        PsiElement[] targetElements = {};

        PsiFile[] files = findGlossaryCsvFiles(project);
        
        for (PsiFile file : files) {
            String fileText = file.getViewProvider().getDocument().getText();
            if (!fileText.contains(glossaryKey)) {
                continue;
            }

            PsiElement lineElement = findLineElementContaining(file, glossaryKey);

            if (lineElement != null) {
                targetElements = ArrayUtil.append(targetElements, lineElement);
            }
        }

        return targetElements.length > 0 ? targetElements : null;
    }

    private @Nullable String resolveGlossaryKey(@NotNull PsiElement sourceElement) {
        if (sourceElement.getParent() == null) {
            return null;
        }

        if (sourceElement.getParent().getText().trim().contains("trans")) {
            return sourceElement.getText();
        }

        return null;
    }

    private PsiFile[] findGlossaryCsvFiles(Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        java.util.List<PsiFile> filesList = new java.util.ArrayList<>();
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile vFile : FilenameIndex.getAllFilesByExt(project, "csv", scope)) {
            String lowerName = vFile.getName().toLowerCase();
            if (lowerName.contains("glossary")) {
                PsiFile psiFile = psiManager.findFile(vFile);
                if (psiFile != null) {
                    filesList.add(psiFile);
                }
            }
        }
        
        return filesList.toArray(PsiFile.EMPTY_ARRAY);
    }

    public static PsiElement findLineElementContaining(PsiFile file, String glossaryKey) {
        Document doc = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (doc == null) return null;
        
        String fileText = doc.getText();
        String[] lines = fileText.split("\n");
        
        int currentOffset = 0;
        for (String line : lines) {
            if (line.startsWith(glossaryKey + ",")) {
                // Found the line, create a navigable element at this offset
                final int lineOffset = currentOffset;
                final String lineText = line;

                return new FakePsiElement() {
                    @Override
                    public PsiElement getParent() {
                        return file;
                    }

                    @Override
                    public TextRange getTextRange() {
                        return new TextRange(lineOffset, lineOffset + lineText.length());
                    }

                    @Override
                    public int getTextOffset() {
                        return lineOffset;
                    }

                    @Override
                    public String getText() {
                        return lineText;
                    }

                    @Override
                    public PsiFile getContainingFile() {
                        return file;
                    }

                    @Override
                    public @Nullable String getName() {
                        VirtualFile vf = file.getVirtualFile();
                        return vf != null ? vf.getName() : file.getName();
                    }

                    @Override
                    public ItemPresentation getPresentation() {
                        VirtualFile vf = file.getVirtualFile();
                        String fileName = vf != null ? vf.getName() : file.getName();
                        String location = vf != null ? vf.getPath() : null;

                        return new ItemPresentation() {
                            @Override
                            public @NotNull String getPresentableText() {
                                return fileName;
                            }

                            @Override
                            public @Nullable String getLocationString() {
                                return location;
                            }

                            @Override
                            public @Nullable Icon getIcon(boolean unused) {
                                return file.getIcon(0);
                            }
                        };
                    }
                };
            }
            currentOffset += line.length() + 1; // +1 for the newline character
        }
        
        return null;
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
