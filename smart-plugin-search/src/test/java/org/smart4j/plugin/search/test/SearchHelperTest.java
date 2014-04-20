package org.smart4j.plugin.search.test;

import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class SearchHelperTest {

    private static final String indexDir = "D:/temp/index/smart4j";
    private static final Version luceneVersion = Version.LUCENE_46;
    private static final Analyzer luceneAnalyzer = new StandardAnalyzer(luceneVersion);

    @Test
    public void searchTest() {
        int number = 1;
        int size = 5;
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            IndexReader reader = DirectoryReader.open(directory);

            String[] fields = {"title", "content"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(luceneVersion, fields, luceneAnalyzer);
            Query query = parser.parse("iPhone");

            int start = (number - 1) * size;
            int count = start + size;

            TopScoreDocCollector collector = TopScoreDocCollector.create(count, false);
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.search(query, collector);

            TopDocs topDocs = collector.topDocs(start, size);
            int total = topDocs.totalHits;
            System.out.println("找到 " + total + " 条记录");
            if (total > 0) {
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document document = searcher.doc(scoreDoc.doc);
                    System.out.println("--------------------------------------------------");
                    System.out.println("title: " + document.get("title"));
                    System.out.println("content: " + document.get("content"));
                }
            }

            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
