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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Kamran
 * 
 */
public class HttpConnector {

    protected int BUFFER_SIZE = 2048;
    protected int DEFAULT_STREAM_BUFFER_SIZE = 3072;
    protected int DEFAULT_CONNECT_TIMEOUT = 13000;
    protected int DEFAULT_READ_TIMEOUT = 13000;

    /**
     * @param url
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected URLConnection getConnection(String url) throws IOException {
        return getConnection( url, Proxy.NO_PROXY );
    }

    /**
     * @param url
     * @param proxy
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected URLConnection getConnection(String url, Proxy proxy) throws IOException {
        URL u = new URL( url );
        URLConnection conn = null;

        conn = u.openConnection( proxy == null ? Proxy.NO_PROXY : proxy );
        conn.setConnectTimeout( DEFAULT_CONNECT_TIMEOUT );
        conn.setReadTimeout( DEFAULT_READ_TIMEOUT );

        return conn;
    }

    /**
     * @param url
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    protected HttpsURLConnection getSecureConnection(String url) throws IOException, KeyManagementException,
            NoSuchAlgorithmException {
        return getSecureConnection( url, Proxy.NO_PROXY );
    }

    /**
     * @param url
     * @param proxy
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected HttpsURLConnection getSecureConnection(String url, Proxy proxy) throws IOException,
            NoSuchAlgorithmException, KeyManagementException {

        SSLContext context = SSLContext.getInstance( "TLS" );
        context.init( new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom() );

        URL u = new URL( url );
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection( proxy == null ? Proxy.NO_PROXY : proxy );

        conn.setSSLSocketFactory( context.getSocketFactory() );
        conn.setHostnameVerifier( new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        } );

        conn.setConnectTimeout( DEFAULT_CONNECT_TIMEOUT );
        conn.setReadTimeout( DEFAULT_READ_TIMEOUT );

        return conn;
    }

    /**
     * @param conn
     * @param data
     * @throws IOException
     */
    protected void doOutput(URLConnection conn, String data) throws IOException {
        BufferedWriter wr = new BufferedWriter( new OutputStreamWriter( conn.getOutputStream() ),
                DEFAULT_STREAM_BUFFER_SIZE );

        wr.write( data );
        wr.flush();
        wr.close();
    }

    /**
     * @param conn
     * @return
     * @throws IOException
     */
    protected StringBuffer doInput(URLConnection conn) throws IOException {
        BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ),
                DEFAULT_STREAM_BUFFER_SIZE );

        StringBuffer buff = new StringBuffer();

        char[] bb = new char[BUFFER_SIZE];
        int nob;

        while(( nob = rd.read( bb ) ) != -1) {
            buff.append( new String( bb, 0, nob ) );
        }

        // Log.d( getClass().getName(), buff.toString() );

        rd.close();

        return buff;
    }

    protected final static class DefaultTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
