/*
 * This file is part of SearchIds.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * SearchIds is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SearchIds is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SearchIds.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.searchids;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataParser {

    private final SearchIds searchids;

    public DataParser(SearchIds searchids) {
        this.searchids = searchids;
    }

    public ArrayList<Result> search(String query) {
        return search(query, "decimal");
    }

    public ArrayList<Result> search(String query, String base) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DataHandler handler = new DataHandler();
            handler.setPattern(Pattern.compile(".*?" + Pattern.quote(query) + ".*", Pattern.CASE_INSENSITIVE));
            saxParser.parse(SearchIdsProperties.dataXml, handler);
            return handler.getResults();
        }
        catch (Exception e) {
            searchids.warning("An error occurred while getting Results");
        }
        return null;
    }

    class DataHandler extends DefaultHandler {

        boolean data = false;
        boolean blocks = false;
        boolean items = false;
        boolean item = false;
        private Pattern pattern;
        private ArrayList<Result> results = new ArrayList<Result>();

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public ArrayList<Result> getResults() {
            return results;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("DATA")) {
                data = true;
            }

            if (qName.equalsIgnoreCase("BLOCKS")) {
                blocks = true;
            }

            if (qName.equalsIgnoreCase("ITEMS")) {
                items = true;
            }

            if (qName.equalsIgnoreCase("ITEM")) {
                item = true;

                if (SearchIdsProperties.searchType.equalsIgnoreCase("all") ||
                    (SearchIdsProperties.searchType.equalsIgnoreCase("blocks") && blocks == true) ||
                    (SearchIdsProperties.searchType.equalsIgnoreCase("items") && items == true)) {

                    String name = attributes.getValue("name");
                    String value = attributes.getValue("dec");
                    String id = attributes.getValue("id");

                    if (name != null && value != null) {
                        if (pattern.matcher(name).matches()) {
                            if (id != null) {
                                results.add(new Result(Integer.valueOf(value), Integer.valueOf(id), name));
                            }
                            else {
                                results.add(new Result(Integer.valueOf(value), name));
                            }
                        }
                    }
                    else {
                        searchids.warning("Name or value is null on an item");
                    }
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("DATA")) {
                data = false;
            }

            if (qName.equalsIgnoreCase("BLOCKS")) {
                blocks = false;
            }

            if (qName.equalsIgnoreCase("ITEMS")) {
                items = false;
            }

            if (qName.equalsIgnoreCase("ITEM")) {
                item = false;
            }
        }
    }
}
