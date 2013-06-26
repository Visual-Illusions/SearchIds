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
package net.visualillusionsent.minecraft.server.mod.canary.plugin.searchids;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.searchids.DataParser;
import net.visualillusionsent.searchids.Result;
import net.visualillusionsent.searchids.SearchIds;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.searchids.UpdateThread;
import org.xml.sax.SAXException;

public final class CanarySearchIds extends Plugin implements SearchIds {

    public static DataParser parser;
    private UpdateThread updateThread;

    public final boolean enable() {
        if (!SearchIdsProperties.initProps()) {
            getLogman().severe("Could not initialize properties file");
            return false;
        }
        if (parser == null) {
            try {
                parser = new DataParser(this);
            }
            catch (ParserConfigurationException ex) {}
            catch (SAXException ex) {}
        }
        if (!initData()) {
            getLogman().severe("Could not init the search data from: " + SearchIdsProperties.dataXml + ". Please check that the file exists and is not corrupt.");
            if (!SearchIdsProperties.autoUpdate) {
                getLogman().severe("Set auto-update-data=true in 'config/SearchIds/SearchIds.cfg' to automatically download the search data file " + SearchIdsProperties.dataXml);
            }
            return false;
        }

        if (SearchIdsProperties.autoUpdate) {
            if (updateThread == null) {
                updateThread = new UpdateThread(this);
            }
            updateThread.start();
        }

        try {
            new SearchCommandListener(this);
        }
        catch (CommandDependencyException ex) {
            return false;
        }
        return true;
    }

    public final void disable() {
        if (updateThread != null) {
            updateThread.stop();
            updateThread = null;
        }
        parser = null;
    }

    private final boolean initData() {
        if ((SearchIdsProperties.dataXml == null) || (SearchIdsProperties.dataXml.equals(""))) {
            return false;
        }

        File f = new File(SearchIdsProperties.dataXml);
        if ((!updateData(SearchIdsProperties.updateSource)) && (!f.exists())) {
            return false;
        }

        return parser.search("test") != null;
    }

    public final boolean updateData(String Source) {
        if (SearchIdsProperties.autoUpdate) {
            try {
                URL url = new URL(Source);
                getLogman().info("Updating data from " + Source + "...");
                InputStream is = url.openStream();
                FileOutputStream fos = null;
                fos = new FileOutputStream(SearchIdsProperties.dataXml);
                int oneChar;
                while ((oneChar = is.read()) != -1) {
                    fos.write(oneChar);
                }
                is.close();
                fos.close();
                getLogman().info("Update Successful!");
                return true;
            }
            catch (MalformedURLException e) {
                if (Source.equals(SearchIdsProperties.updateSource)) {
                    getLogman().warning("Update from " + Source + " Failed. Attempting Alternate Source...");
                    return updateData(SearchIdsProperties.updateSourceALT);
                }
                else {
                    getLogman().warning("Update from " + Source + " Failed.");
                    return false;
                }
            }
            catch (IOException e) {
                getLogman().warning("Could not update search data.");
                return false;
            }
        }
        return true;
    }

    final void printSearchResults(Player player, ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            player.message("§bSearch results for \"" + query + "\":");
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + SearchIdsProperties.delimiter + " " + SearchIdsProperties.rightPad(result.getName(), SearchIdsProperties.nameWidth));
                if (num % 2 == 0 || !itr.hasNext()) {
                    player.message("§6" + line.trim());
                    line = "";
                }
                if (num > 16) {
                    player.message("§6Not all results are displayed. Make your term more specific!");
                    break;
                }
            }
        }
        else {
            player.message("§cNo results found.");
        }
    }

    final void printConsoleSearchResults(ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            System.out.println("Search results for \"" + query + "\":");
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + SearchIdsProperties.delimiter + " " + SearchIdsProperties.rightPad(result.getName(), SearchIdsProperties.nameWidth));
                if (num % 2 == 0 || !itr.hasNext()) {
                    System.out.println(line.trim());
                    line = "";
                }
                if (num > 16) {
                    System.out.println("Not all results are displayed. Make your term more specific!");
                    break;
                }
            }
        }
        else {
            System.out.println("No results found.");
        }
    }

    @Override
    public void info(String msg) {
        getLogman().info(msg);
    }

    @Override
    public void warning(String msg) {
        getLogman().warning(msg);
    }

    @Override
    public void severe(String msg) {
        getLogman().severe(msg);
    }
}
