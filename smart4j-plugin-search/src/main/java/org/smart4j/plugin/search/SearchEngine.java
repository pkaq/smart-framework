package org.smart4j.plugin.search;

import org.apache.lucene.document.Document;
import org.smart4j.plugin.search.bean.IndexData;
import org.smart4j.plugin.search.bean.SearchResult;

public interface SearchEngine {

    IndexData createIndexData();

    SearchResult createSearchResult(Document document);
}
