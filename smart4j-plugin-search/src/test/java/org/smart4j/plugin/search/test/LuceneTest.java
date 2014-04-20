package org.smart4j.plugin.search.test;

import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest {

    private static final String indexDir = "D:/temp/index/test";
    private static final Version luceneVersion = Version.LUCENE_46;
    private static final Analyzer luceneAnalyzer = new StandardAnalyzer(luceneVersion);

    @Test
    public void indexTest() {
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            IndexWriterConfig config = new IndexWriterConfig(luceneVersion, luceneAnalyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(directory, config);

            Document document = new Document();
            document.add(new Field("title", "Lucene", TextField.TYPE_STORED));
            document.add(new Field("content", "Hello Lucene", TextField.TYPE_STORED));

            writer.addDocument(document);

            writer.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchTest1() {
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            IndexReader reader = DirectoryReader.open(directory);

            Term term = new Term("content", "lucene");
            Query query = new TermQuery(term);

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            System.out.println("找到 " + topDocs.totalHits + " 条记录");
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                System.out.println("title: " + document.get("title"));
                System.out.println("content: " + document.get("content"));
            }

            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchTest2() {
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            IndexReader reader = DirectoryReader.open(directory);

            QueryParser parser = new QueryParser(luceneVersion, "content", luceneAnalyzer);
            Query query = parser.parse("Lucene");

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            System.out.println("找到 " + topDocs.totalHits + " 条记录");
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                System.out.println("title: " + document.get("title"));
                System.out.println("content: " + document.get("content"));
            }

            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchTest3() {
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            IndexReader reader = DirectoryReader.open(directory);

            String[] fields = {"title", "content"};
            QueryParser parser = new MultiFieldQueryParser(luceneVersion, fields, luceneAnalyzer);
            Query query = parser.parse("Lucene");

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            System.out.println("找到 " + topDocs.totalHits + " 条记录");
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                System.out.println("title: " + document.get("title"));
                System.out.println("content: " + document.get("content"));
            }

            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
