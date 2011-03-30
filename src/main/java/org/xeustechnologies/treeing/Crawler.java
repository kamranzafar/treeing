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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.xeustechnologies.treeing.Tag.LinkType;

public class Crawler extends Thread {

    private final BlockingQueue<String> urls = new LinkedBlockingQueue<String>();
    private final List<String> crawledUrls = Collections.synchronizedList( new ArrayList<String>() );
    private final int poolSize;
    private final HttpIndexer indexer;

    public Crawler(String url, int poolSize, String indexFolder) throws InterruptedException, IOException {
        urls.put( url );
        this.poolSize = poolSize;
        indexer = new HttpIndexer( indexFolder );
    }

    @Override
    public void run() {
        try {
            CrawlTask t[] = new CrawlTask[poolSize];

            for( int i = 0; i < poolSize; i++ ) {
                t[i] = new CrawlTask( urls, crawledUrls );
                System.out.println( "Staring CrawlTask Thread: " + ( i + 1 ) );
                t[i].start();
            }

            for( int i = 0; i < poolSize; i++ ) {
                t[i].join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    class CrawlTask extends Thread {

        BlockingQueue<String> urls;
        List<String> crawledUrls;

        CrawlTask(BlockingQueue<String> urls, List<String> crawledUrls) {
            this.urls = urls;
            this.crawledUrls = crawledUrls;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    String url = urls.take();

                    System.out.println( "Crawling: " + url );

                    crawl( url );

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (FileNotFoundException e) {
                    System.err.println( "URL: " + e.getMessage() + " not found" );
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void crawl(String urlstr) throws Exception {

            crawledUrls.add( urlstr );

            URL url = new URL( urlstr );
            URLConnection conn = url
                    .openConnection( new Proxy( Type.HTTP, new InetSocketAddress( "10.105.24.70", 8080 ) ) );

            String mime = conn.getHeaderField( "Content-Type" );

            // System.out.println( mime );

            if( mime != null && mime.contains( "text/html" ) ) {
                InputStream in = conn.getInputStream();

                MinHtml html = MinHtml.parse( conn.getInputStream() );

                in.close();

                for( Tag l : html.getLinks() ) {
                    String nurlstr = l.getAttributes().get( "href" );
                    // Skip javascript links
                    if( !nurlstr.startsWith( "javascript:" ) ) {
                        URL nurl = new URL( conn.getURL(), nurlstr );

                        // System.out.println( "nurl: " + nurl.getHost() );

                        String nextUrl = nurl.toString();

                        if( l.getLinkType() == LinkType.LOCAL && !crawledUrls.contains( nextUrl )
                                && !urls.contains( nextUrl ) ) {
                            urls.put( nextUrl );
                            // System.out.println( "Next URL to crawl: " +
                            // nextUrl );
                        }
                    }
                }

                synchronized (indexer) {
                    indexer.indexUrl( urlstr, html.getHtml(), mime );
                }
                // for( Tag t : html.getImages() ) {
                // System.out.println( t.getAttributes().get( "src" ) );
                // }
                //
                // for( Tag s : html.getScripts() ) {
                // System.out.println( s.getLinkType().name() + " : " +
                // s.getAttributes().get( "src" ) );
                // }
            }
        }
    }

    public void close() throws IOException {
        indexer.closeIndex();
    }

    public static void main(String[] args) {
        Crawler c = null;
        try {
            c = new Crawler( "http://www.meteor.ie", 2, "c:/test/luc" );
            c.start();
        } catch (Exception e) {
            try {
                c.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
