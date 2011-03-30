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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Tag {
    protected String name;
    protected String value;
    protected Map<String, String> attributes;
    protected LinkType linkType;

    public Tag() {
        attributes = Collections.synchronizedMap( new HashMap<String, String>() );
        linkType = LinkType.NA;
    }

    public Tag(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, String value) {
        this.attributes.put( name, value );
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    /**
     * Used for tags that point to a resource default is NA (Not Applicable)
     */
    public enum LinkType {
        LOCAL, FOREIGN, MAIL, ANCHOR, NA
    }
}
