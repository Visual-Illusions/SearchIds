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
package net.visualillusionsent.searchids.spout;

import net.visualillusionsent.searchids.DataParser;
import net.visualillusionsent.searchids.Result;
import net.visualillusionsent.searchids.SearchIds;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.searchids.UpdateThread;
import net.visualillusionsent.utils.ProgramStatus;
import net.visualillusionsent.utils.VersionChecker;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.entity.Player;
import org.spout.api.plugin.Plugin;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

public final class SpoutSearchIds extends VisualIllusionsSpoutPlugin implements SearchIds {

    public static DataParser parser;
    private UpdateThread updateThread;

    public SpoutSearchIds() {
        File viutilslib = new File("lib/viutils-1.1.1.jar");
        if (!viutilslib.exists()) {
            try {
                URL website = new URL("http://repo.visualillusionsent.net/net/visualillusionsent/viutils/1.1.1/viutils-1.1.1.jar");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("lib/viutils-1.1.1.jar");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            catch (Exception ex) {
                System.out.println("Failed to download VIUtils 1.1.1");
            }
        }
    }

    public final void onEnable() {
        initialize();
        checkStatus();
        checkVersion();
        if (!SearchIdsProperties.initProps()) {
            getLogger().severe("Could not initialize properties file");
            return;
        }
        if (parser == null) {
            try {
                parser = new DataParser(this);
            }
            catch (ParserConfigurationException ex) {
            }
            catch (SAXException ex) {
            }
        }

        if (updateThread == null) {
            updateThread = new UpdateThread(this);
        }

        if (!initData()) {
            getLogger().severe("Could not init the search data from: " + SearchIdsProperties.dataXml + ". Please check that the file exists and is not corrupt.");
            if (!SearchIdsProperties.autoUpdate) {
                getLogger().severe("Set auto-update-data=true in 'config/SearchIds/SearchIds.cfg' to automatically download the search data file " + SearchIdsProperties.dataXml);
            }
            return;
        }

        if (SearchIdsProperties.autoUpdate) {
            updateThread.start();
        }
        AnnotatedCommandExecutorFactory.create(new SpoutSearchCommandExecutor(this));
    }

    public final void onDisable() {
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
        if (!f.exists()) {
            if (!updateThread.updateData(SearchIdsProperties.updateSource)) {
                return false;
            }
        }

        return parser.search("test") != null;
    }

    final void printSearchResults(Player player, ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            player.sendMessage("§bSearch results for \"" + query + "\":");
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + SearchIdsProperties.delimiter + " " + SearchIdsProperties.rightPad(result.getName(), SearchIdsProperties.nameWidth));
                if (num % 2 == 0 || !itr.hasNext()) {
                    player.sendMessage("§6" + line.trim());
                    line = "";
                }
                if (num > 16) {
                    player.sendMessage("§6Not all results are displayed. Make your term more specific!");
                    break;
                }
            }
        }
        else {
            player.sendMessage("§cNo results found.");
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
        getLogger().info(msg);
    }

    @Override
    public void warning(String msg) {
        getLogger().warning(msg);
    }

    @Override
    public void severe(String msg) {
        getLogger().severe(msg);
    }

    @Override
    public void severe(String msg, Throwable thrown) {
        getLogger().log(Level.SEVERE, msg, thrown);
    }

    private final String getJarPath() {
        try {
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            return codeSource.getLocation().toURI().getPath();
        }
        catch (URISyntaxException ex) {
        }
        return "plugins/SearchIds.jar";
    }

    private final String getPluginName() {
        return "SearchIds";
    }
}
