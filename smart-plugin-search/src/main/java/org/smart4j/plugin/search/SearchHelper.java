package org.smart4j.plugin.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.dao.bean.Pager;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.plugin.search.bean.IndexData;
import org.smart4j.plugin.search.bean.IndexDocument;
import org.smart4j.plugin.search.bean.IndexField;
import org.smart4j.plugin.search.bean.SearchResult;

public class SearchHelper {

    private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);

    private static final List<SearchEngine> searchEngineList = new ArrayList<SearchEngine>();
    private static final String indexDir = ConfigHelper.getString("smart.plugin.search.index_dir");
    private static final Version luceneVersion = Version.LUCENE_46;
    private static final Analyzer luceneAnalyzer = new StandardAnalyzer(luceneVersion);

    static {
        List<Class<?>> searchEngineClassList = ClassHelper.getClassListBySuper(SearchEngine.class);
        if (CollectionUtil.isNotEmpty(searchEngineClassList)) {
            for (Class<?> searchEngineClass : searchEngineClassList) {
                SearchEngine searchEngine = (SearchEngine) BeanHelper.getBean(searchEngineClass);
                searchEngineList.add(searchEngine);
            }
        }
    }

    public static void index() {
        Directory directory = null;
        IndexWriter writer = null;
        try {
            directory = FSDirectory.open(new File(indexDir));
            IndexWriterConfig config = new IndexWriterConfig(luceneVersion, luceneAnalyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(directory, config);

            for (SearchEngine searchEngine : searchEngineList) {
                IndexData indexData = searchEngine.createIndexData();
                if (indexData != null) {
                    List<IndexDocument> indexDocumentList = indexData.getIndexDocumentList();
                    if (CollectionUtil.isNotEmpty(indexDocumentList)) {
                        for (IndexDocument indexDocument : indexDocumentList) {
                            Document document = new Document();
                            List<IndexField> indexFieldList = indexDocument.getIndexFieldList();
                            if (CollectionUtil.isNotEmpty(indexFieldList)) {
                                for (IndexField indexField : indexFieldList) {
                                    document.add(new Field(indexField.getName(), indexField.getValue(), TextField.TYPE_STORED));
                                }
                            }
                            writer.addDocument(document);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (directory != null) {
                    directory.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public static Pager<SearchResult> search(String keywords, int number, int size) {
        Pager<SearchResult> searchResultPager = null;
        Directory directory = null;
        IndexReader reader = null;
        try {
            directory = FSDirectory.open(new File(indexDir));
            reader = DirectoryReader.open(directory);

            List<String> fieldList = new ArrayList<String>();
            for (IndexFieldName indexFieldName : IndexFieldName.values()) {
                fieldList.add(indexFieldName.name());
            }
            String[] fields = fieldList.toArray(new String[fieldList.size()]);
            QueryParser parser = new MultiFieldQueryParser(luceneVersion, fields, luceneAnalyzer);
            Query query = parser.parse(keywords);

            int start = (number - 1) * size;
            int count = start + size;

            TopScoreDocCollector collector = TopScoreDocCollector.create(count, false);
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.search(query, collector);

            List<SearchResult> searchResultList = new ArrayList<SearchResult>();
            TopDocs topDocs = collector.topDocs(start, size);
            int total = topDocs.totalHits;
            if (total > 0) {
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document document = searcher.doc(scoreDoc.doc);
                    for (SearchEngine searchEngine : searchEngineList) {
                        SearchResult searchResult = searchEngine.createSearchResult(document);
                        if (searchResult != null) {
                            searchResultList.add(searchResult);
                        }
                    }
                }
            }

            searchResultPager = new Pager<SearchResult>(number, size, total, searchResultList);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (directory != null) {
                    directory.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return searchResultPager;
    }
}
