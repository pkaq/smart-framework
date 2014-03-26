package com.smart.plugin.search;

import com.smart.plugin.search.bean.IndexData;
import com.smart.plugin.search.bean.SearchResult;
import org.apache.lucene.document.Document;

public interface SearchEngine {

    IndexData createIndexData();

    SearchResult createSearchResult(Document document);
}
