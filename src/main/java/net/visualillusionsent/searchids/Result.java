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

/**
 * Search Result class
 * 
 * @author croemmich
 * @author Jason (darkdiplomat)
 */
public final class Result {

    private final String name;
    private final int value;
    private final int id;

    public Result(int value, String name) {
        this.value = value;
        this.name = name;
        this.id = 0;
    }

    public Result(int value, int id, String name) {
        this.value = value;
        this.name = name;
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final String getValue() {
        if (SearchIdsProperties.base.equalsIgnoreCase("hex") || SearchIdsProperties.base.equalsIgnoreCase("hexadecimal")) {
            return Integer.toHexString(value).toUpperCase();
        }
        else {
            return String.valueOf(value);
        }
    }

    public final String getId() {
        if (SearchIdsProperties.baseId.equalsIgnoreCase("hex") || SearchIdsProperties.baseId.equalsIgnoreCase("hexadecimal")) {
            return Integer.toHexString(id).toUpperCase();
        }
        else {
            return String.valueOf(id);
        }
    }

    public final String getFullValue() {
        if (id == 0) {
            return getValue();
        }
        else {
            if (id >= 10) {
                return getValue() + ":" + getId();
            }
            else {
                return getValue() + ":" + getId() + " ";
            }
        }
    }

    public final int getValuePad() {
        if (id <= 0) {
            return SearchIdsProperties.numWidth;
        }
        else {
            return SearchIdsProperties.numWidth + 3;
        }
    }
}
