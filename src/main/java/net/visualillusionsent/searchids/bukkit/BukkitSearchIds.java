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
package net.visualillusionsent.searchids.bukkit;

import net.visualillusionsent.searchids.DataParser;
import net.visualillusionsent.searchids.SearchIds;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.searchids.UpdateTask;
import net.visualillusionsent.utils.TaskManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

public final class BukkitSearchIds extends VisualIllusionsBukkitPlugin implements SearchIds {

    private DataParser parser;
    private UpdateTask updateTask;
    private ScheduledFuture<?> updateScheduledTask;

    public BukkitSearchIds() {
        // Check for VIUtils, download as nessary
        File viutilslib = new File("lib/viutils-" + viutils_version + ".jar");
        if (!viutilslib.exists()) {
            try {
                URL website = new URL("http://repo.visualillusionsent.net/net/visualillusionsent/viutils/" + viutils_version + "/viutils-" + viutils_version + ".jar");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("lib/viutils-" + viutils_version + ".jar");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            catch (Exception ex) {
                System.out.println("Failed to download VIUtils " + viutils_version);
            }
        }
        //
    }

    public final void onEnable() {
        initialize();
        checkStatus();
        checkVersion();
        if (!properties.initProps(this)) {
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
        if (updateTask == null) {
            updateTask = new UpdateTask(this);
        }
        if (!initData()) {
            getLogger().severe("Could not init the search data from: " + properties.dataXml() + ". Please check that the file exists and is not corrupt.");
            if (!properties.autoUpdate()) {
                getLogger().severe("Set auto-update-data=true in 'config/SearchIds/SearchIds.cfg' to automatically download the search data file " + properties.dataXml());
            }
            return;
        }
        if (properties.autoUpdate()) {
            long interval = properties.autoUpdateInterval();
            updateScheduledTask = TaskManager.scheduleContinuedTaskInMillis(updateTask, interval, interval);
        }
        new BukkitSearchCommandExecutor(this);
    }

    public final void onDisable() {
        if (updateScheduledTask != null) {
            updateScheduledTask.cancel(true);
            updateScheduledTask = null;
            updateTask = null;
        }
        parser = null;
    }

    private boolean initData() {
        File f = new File(properties.dataXml());
        if (!f.exists()) {
            if (!updateTask.updateData(properties.updateSource())) {
                return false;
            }
        }

        return parser.search("test") != null;
    }

    final DataParser getParser() {
        return parser;
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
}
