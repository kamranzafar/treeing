package org.xeustechnologies.treeing;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TreeingTest {
    @Test
    public void testTreeing() throws Exception {
        try {
            Thread t = new Thread( new Crawler( "http://www.python.org", 2, "c:/test/luc" ) );
            t.start();
            t.join();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testSearch() throws Exception {
        FSDirectory index = FSDirectory.open( new File( "C:/test/luc" ) );

        String querystr = "hello";
        Query q = new QueryParser( Version.LUCENE_CURRENT, "contents", new StandardAnalyzer( Version.LUCENE_CURRENT ) )
                .parse( querystr );

        int hitsPerPage = 10;
        IndexSearcher searcher = new IndexSearcher( index, true );
        TopScoreDocCollector collector = TopScoreDocCollector.create( hitsPerPage, true );
        searcher.search( q, collector );
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        System.out.println( collector.getTotalHits() );

        for( int i = 0; i < hits.length; ++i ) {
            int docId = hits[i].doc;
            Document d = searcher.doc( docId );
            System.out.println( ( i + 1 ) + ". " + d.get( "url" ) );
        }
    }
}
