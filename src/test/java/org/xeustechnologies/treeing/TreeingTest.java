/**
 *  Treeing. Crawling, indexing and searching web content
 *  Copyright (C) 2011 Kamran
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Contact Info:
 *  xeus.man@gmail.com
 */
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

/**
 * @author Kamran
 * 
 */
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
