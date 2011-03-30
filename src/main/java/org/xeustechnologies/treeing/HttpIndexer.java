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
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class HttpIndexer {

    private static final String MIME = "mime";
    private static final String URL = "url";
    private static final String CONTENTS = "contents";
    private final IndexWriter writer;

    public HttpIndexer(String indexDir) throws IOException {
        FSDirectory dir = FSDirectory.open( new File( indexDir ) );
        writer = new IndexWriter( dir, new StandardAnalyzer( Version.LUCENE_CURRENT ), true,
                IndexWriter.MaxFieldLength.LIMITED );
    }

    public void indexUrl(String url, String content, String mime) throws IOException {
        try {
            Document doc = new Document();

            doc.add( new Field( CONTENTS, new StringReader( content ) ) );
            doc.add( new Field( URL, url, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
            doc.add( new Field( MIME, mime, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

            writer.addDocument( doc );
            System.out.println( "Added: " + url );
            writer.commit();
        } catch (Exception e) {
            System.out.println( "Could not add: " + url );
        }
    }

    public void commitIndex() throws CorruptIndexException, IOException {
        writer.commit();
    }

    public void closeIndex() throws IOException {
        writer.optimize();
        writer.close();
    }
}
