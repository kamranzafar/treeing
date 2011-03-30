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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Kamran
 * 
 */
public class MimeTypeHelper {
    protected static ResourceBundle mimeTypesBundle = ResourceBundle
            .getBundle( "org.xeustechnologies.treeing.mimetypes.properties" );

    public static String[] getMimeTypes(String ext) {
        return mimeTypesBundle.getString( ext ).split( "," );
    }

    public static String[] getExtensions(String mime) {
        List<String> exts = new ArrayList<String>();
        Enumeration<String> keys = mimeTypesBundle.getKeys();

        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            String ma[] = getMimeTypes( key );

            for( String m : ma ) {
                if( m.toLowerCase().equals( mime.toLowerCase() ) ) {
                    exts.add( key );
                    break;
                }
            }
        }

        return exts.toArray( new String[exts.size()] );
    }
}
