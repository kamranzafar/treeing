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

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;

public class HtmlSaxParser extends AbstractSAXParser {
    public HtmlSaxParser() {
        super( new HTMLConfiguration() );
    }
}
