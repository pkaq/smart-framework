package smart.plugin.search;

import org.apache.lucene.document.Document;
import smart.plugin.search.bean.IndexData;
import smart.plugin.search.bean.SearchResult;

public interface SearchEngine {

    IndexData createIndexData();

    SearchResult createSearchResult(Document document);
}
