package com.turbinekreuzberg.plugins.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PathToStubReferencesSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    public PathToStubReferencesSearcher() {}

    @Override
    public void processQuery(ReferencesSearch.@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();
        if (elementToSearch instanceof MethodImpl && ((MethodImpl) elementToSearch).getName().endsWith("Action")) {
            String moduleName = convertCamelCaseToKebabCase(getModuleName((MethodImpl) elementToSearch));
            String methodName = convertCamelCaseToKebabCase(getMethodName((MethodImpl) elementToSearch));
            String word = "/" + String.join("/", moduleName, "gateway", methodName);

            SearchRequestCollector collector = queryParameters.getOptimizer();
            collector.searchWord(word, elementToSearch.getUseScope(),  UsageSearchContext.IN_STRINGS, false, elementToSearch);
        }
    }

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
}
