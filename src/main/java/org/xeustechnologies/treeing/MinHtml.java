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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MinHtml {
    protected final List<Tag> links;
    protected final List<Tag> images;
    protected final List<Tag> scripts;
    protected final List<Tag> css;
    protected String title;
    protected String html;

    private MinHtml() {
        links = new ArrayList<Tag>();
        images = new ArrayList<Tag>();
        scripts = new ArrayList<Tag>();
        css = new ArrayList<Tag>();
    }

    public void addLink(Tag link) {
        links.add( link );
    }

    public void addImage(Tag image) {
        images.add( image );
    }

    public void addScript(Tag link) {
        scripts.add( link );
    }

    public List<Tag> getLinks() {
        return links;
    }

    public List<Tag> getImages() {
        return images;
    }

    public List<Tag> getScripts() {
        return scripts;
    }

    public List<Tag> getCss() {
        return css;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public static MinHtml parse(InputStream stream) throws Exception {
        StringBuffer html = new StringBuffer();

        byte buff[] = new byte[4096];
        int num = 0;

        while(( num = stream.read( buff ) ) != -1) {
            html.append( new String( buff, 0, num ) );
        }

        MinHtml minHtml = new MinHtml();
        minHtml.setHtml( html.toString() );

        XMLReader reader = XMLReaderFactory.createXMLReader( "org.xeustechnologies.treeing.HtmlSaxParser" );

        reader.setContentHandler( new MinHtmlParser( minHtml ) );

        reader.parse( new InputSource( new ByteArrayInputStream( html.toString().getBytes() ) ) );

        return minHtml;
    }

    static class MinHtmlParser extends DefaultHandler {

        protected MinHtml html;
        protected final String _A = "a";
        protected final String _IMG = "img";
        protected final String _SCRIPT = "script";
        protected final String _TITLE = "title";

        public MinHtmlParser(MinHtml html) {
            this.html = html;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // link
            if( localName.equalsIgnoreCase( _A ) ) {
                Tag link = new Tag();

                String href = attributes.getValue( "", "href" );

                if( href != null ) {
                    setLinkType( link, href );
                    setAttributes( link, attributes );

                    html.addLink( link );
                }
            }
            // image
            else if( localName.equalsIgnoreCase( _IMG ) ) {
                Tag tag = new Tag( localName );

                setAttributes( tag, attributes );

                html.addImage( tag );
            }
            // script
            else if( localName.equalsIgnoreCase( _SCRIPT ) ) {
                Tag scriptLink = new Tag();
                String src = attributes.getValue( "", "src" );

                if( src != null ) {
                    setLinkType( scriptLink, src );
                    setAttributes( scriptLink, attributes );

                    html.addScript( scriptLink );
                }
            }
            // title
            else if( localName.equalsIgnoreCase( _TITLE ) ) {

            }
        }

        private void setLinkType(Tag l, String src) {
            if( src.toLowerCase().startsWith( "http:" ) || src.toLowerCase().startsWith( "ftp:" )
                    || src.toLowerCase().startsWith( "https:" ) ) {
                l.setLinkType( Tag.LinkType.FOREIGN );
            } else if( src.toLowerCase().startsWith( "mailto:" ) ) {
                l.setLinkType( Tag.LinkType.MAIL );
            } else if( src.startsWith( "#" ) ) {
                l.setLinkType( Tag.LinkType.ANCHOR );
            } else {
                l.setLinkType( Tag.LinkType.LOCAL );
            }
        }

        private void setAttributes(Tag t, Attributes attributes) {
            for( int i = 0; i < attributes.getLength(); i++ ) {
                t.addAttribute( attributes.getLocalName( i ).toLowerCase(), attributes.getValue( i ) );
            }
        }
    }
}
