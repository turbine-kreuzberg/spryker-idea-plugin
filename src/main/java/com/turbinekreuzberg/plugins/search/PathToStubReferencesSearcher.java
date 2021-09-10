package com.turbinekreuzberg.plugins.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.jetbrains.annotations.NotNull;

public class PathToStubReferencesSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    public PathToStubReferencesSearcher() {}

    @Override
    public void processQuery(ReferencesSearch.@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();
        if (elementToSearch instanceof MethodImpl && ((MethodImpl) queryParameters.getElementToSearch()).getName().endsWith("Action")) {
            SearchRequestCollector collector = queryParameters.getOptimizer();
//            TODO: replace static string
            collector.searchWord("/realtime-price/gateway/get-price", elementToSearch.getUseScope(),  UsageSearchContext.IN_STRINGS, false, elementToSearch);
        }
    }
}
