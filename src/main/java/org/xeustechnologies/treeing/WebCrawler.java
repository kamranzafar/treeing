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
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.xeustechnologies.esl4j.LogManager;
import org.xeustechnologies.esl4j.Logger;
import org.xeustechnologies.treeing.Tag.LinkType;

/**
 * @author Kamran
 * 
 */
public class WebCrawler extends HttpConnector implements Runnable {

    private final BlockingQueue<String> urls = new LinkedBlockingQueue<String>();
    private final List<String> crawledUrls = Collections.synchronizedList( new ArrayList<String>() );
    private final int poolSize;
    private final HttpIndexer indexer;
    private Proxy proxy = Proxy.NO_PROXY;
    private Logger logger = LogManager.getLogger( WebCrawler.class );

    public WebCrawler(String url, int poolSize, String indexFolder) throws InterruptedException, IOException {
        urls.put( url );
        this.poolSize = poolSize;
        indexer = new HttpIndexer( indexFolder );
    }

    public WebCrawler(String url, int poolSize, String indexFolder, Proxy proxy) throws InterruptedException,
            IOException {
        this( url, poolSize, indexFolder );
        this.proxy = proxy;
    }

    public void run() {
        try {
            CrawlTask t[] = new CrawlTask[poolSize];

            for( int i = 0; i < poolSize; i++ ) {
                t[i] = new CrawlTask( urls, crawledUrls );
                logger.info( "Staring CrawlTask Thread: " + ( i + 1 ) );
                t[i].start();
            }

            for( int i = 0; i < poolSize; i++ ) {
                t[i].join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                indexer.closeIndex();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

                    logger.info( "Crawling: " + url );

                    crawl( url );

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (FileNotFoundException e) {
                    logger.error( "URL: " + e.getMessage() + " not found" );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void crawl(String urlstr) throws Exception {
            crawledUrls.add( urlstr );

            // Only http/https links are crawled
            if( !urlstr.toLowerCase().startsWith( "http" ) ) {
                return;
            }

            URLConnection conn = urlstr.toLowerCase().startsWith( "https:" ) ? getSecureConnection( urlstr, proxy )
                    : getConnection( urlstr, proxy );

            String mime = conn.getHeaderField( "Content-Type" );

            if( mime != null && mime.contains( "text/html" ) ) {
                InputStream in = conn.getInputStream();

                MinHtml html = MinHtml.parse( conn.getInputStream() );

                in.close();

                for( Tag l : html.getLinks() ) {
                    String nurlstr = l.getAttributes().get( "href" );
                    // Skip javascript links
                    if( !nurlstr.startsWith( "javascript:" ) ) {
                        URL nurl = new URL( conn.getURL(), nurlstr );

                        String nextUrl = nurl.toString();

                        if( l.getLinkType() == LinkType.LOCAL && !crawledUrls.contains( nextUrl )
                                && !urls.contains( nextUrl ) ) {
                            urls.put( nextUrl );
                        }
                    }
                }

                synchronized (indexer) {
                    indexer.indexUrl( urlstr, html.getHtml(), mime );
                }
            }
        }
    }
}
