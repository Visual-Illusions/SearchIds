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

import net.visualillusionsent.utils.PropertiesFile;

/**
 * SearchIds Properties enum
 *
 * @author Jason (darkdiplomat)
 */
public final class SearchIdsProperties {
    private PropertiesFile props;

    public final boolean initProps(SearchIds searchids) {
        props = new PropertiesFile("config/SearchIds/SearchIds.cfg");
        props.getString("search-type", "all");
        props.getString("base", "decimal");
        props.getString("data-xml", "config/SearchIds/search-ids-data.xml");
        props.getString("update-source", "http://dl.visualillusionsent.net/minecraft/plugins/SearchIds/search-ids-data.xml");
        props.getString("update-source-Alternate", "https://raw.github.com/Visual-Illusions/SearchIds/v3/search-ids-data.xml");
        props.getBoolean("auto-update-data", true);
        props.getInt("auto-update-interval", 600000);
        props.getInt("width-blockname", 25);
        props.getInt("width-number", 4);
        props.getCharacter("delimiter", '-');

        if (props.getInt("auto-update-interval") < 60000) {
            props.setInt("auto-update-interval", 60000);
            searchids.warning("auto-update-interval cannot be less than 60000! auto-update-interval set to 60000");
        }

        props.save();
        return true;
    }

    public final String searchType() {
        return props.getString("search-type");
    }

    public final String base() {
        return props.getString("base");
    }

    public final String dataXml() {
        return props.getString("data-xml");
    }

    public final String updateSource() {
        return props.getString("update-source");
    }

    public final String updateSourceALT() {
        return props.getString("update-source-Alternate");
    }

    public final boolean autoUpdate() {
        return props.getBoolean("auto-update-data");
    }

    public final long autoUpdateInterval() {
        return props.getLong("auto-update-interval");
    }

    public final int nameWidth() {
        return props.getInt("width-blockname");
    }

    public final int numWidth() {
        return props.getInt("width-number");
    }

    public final char delimiter() {
        return props.getCharacter("delimiter");
    }

    public static String leftPad(String s, int width) {
        return String.format("%" + width + "s", s);
    }

    public static String rightPad(String s, int width) {
        return String.format("%-" + width + "s", s);
    }
}
