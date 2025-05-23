package com.turbinekreuzberg.plugins.contributors.oms;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.util.ProcessingContext;
import com.turbinekreuzberg.plugins.settings.SettingsManager;
import org.jetbrains.annotations.NotNull;

public class OmsXmlPsiReferenceProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        Project project = element.getProject();
        if (!SettingsManager.isFeatureEnabled(project, SettingsManager.Feature.OMS_NAVIGATION)) {
            return PsiReference.EMPTY_ARRAY;
        }
        
        if (element instanceof XmlAttributeValueImpl) {
            if (element.getParent() instanceof XmlAttributeImpl) {
                if (this.isCommandOrCondition(element)) {
                    return new PsiReference[]{new OmsXmlToDependencyReference(element)};
                }
                if (this.isSubProcessInclude(element)) {
                    return new PsiReference[]{new OmsXmlToSubProcessReference(element)};
                }
            }
        }

        return PsiReference.EMPTY_ARRAY;
    }

    private Boolean isCommandOrCondition(PsiElement element) {
        return ((XmlAttributeImpl)element.getParent()).getName().equals("command") || ((XmlAttributeImpl)element.getParent()).getName().equals("condition");
    }

    private Boolean isSubProcessInclude(PsiElement element) {
        return ((XmlAttributeImpl)element.getParent()).getName().equals("file");
    }
}
