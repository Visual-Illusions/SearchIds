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
import java.util.Collections;
import java.util.List;
import net.visualillusionsent.utils.StringUtils;

public final class SearchIdsInformation {

    private final List<String> about;

    public SearchIdsInformation(SearchIds plugin) {
        List<String> pre = new ArrayList<String>();
        pre.add(center("§B--- §A" + plugin.getName() + "§6 v" + plugin.getRawVersion() + "§B ---"));
        pre.add("$VERSION_CHECK$");
        pre.add("§BBuild: §A" + plugin.getBuildNumber());
        pre.add("§BBuilt: §A" + plugin.getBuildTime());
        pre.add("§BDeveloper: §ADarkDiplomat");
        pre.add("§BWebsite: §Ahttp://wiki.visualillusionsent.net/" + plugin.getName());
        pre.add("§BIssues: §Ahttps://github.com/Visual-Illusions/" + plugin.getName() + "/issues");

        // Next line should always remain at the end of the About
        pre.add(center("§aCopyright © 2012-2013 §2Visual §6I§9l§bl§4u§as§2i§5o§en§7s §2Entertainment"));
        about = Collections.unmodifiableList(pre);
    }

    public final List<String> getAbout() {
        return about;
    }

    public final String center(String toCenter) {
        String strColorless = removeFormatting(toCenter);
        return StringUtils.padCharLeft(toCenter, (int) (Math.floor(63 - strColorless.length()) / 2), ' ');
    }

    private final String removeFormatting(String str) {
        return str.replace("\u00A7[A-FK-ORa-fk-or0-9]", "");
    }
}
