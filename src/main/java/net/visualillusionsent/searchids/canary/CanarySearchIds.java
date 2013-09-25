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
package net.visualillusionsent.searchids.canary;

import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.searchids.DataParser;
import net.visualillusionsent.searchids.SearchIds;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.searchids.UpdateTask;
import net.visualillusionsent.utils.TaskManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

public final class CanarySearchIds extends VisualIllusionsCanaryPlugin implements SearchIds {

    private DataParser parser;
    private UpdateTask updateTask;
    private ScheduledFuture<?> updateScheduledTask;

    public CanarySearchIds() {
        super();
    }

    public final boolean enable() {
        checkStatus();
        checkVersion();
        if (!SearchIdsProperties.initProps()) {
            getLogman().severe("Could not initialize properties file");
            return false;
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
            getLogman().severe("Could not init the search data from: " + SearchIdsProperties.dataXml + ". Please check that the file exists and is not corrupt.");
            if (!SearchIdsProperties.autoUpdate) {
                getLogman().severe("Set auto-update-data=true in 'config/SearchIds/SearchIds.cfg' to automatically download the search data file " + SearchIdsProperties.dataXml);
            }
            return false;
        }
        if (SearchIdsProperties.autoUpdate) {
            int interval = SearchIdsProperties.autoUpdateInterval;
            updateScheduledTask = TaskManager.scheduleContinuedTaskInMillis(updateTask, interval, interval);
        }
        try {
            new CanarySearchCommandListener(this);
        }
        catch (CommandDependencyException ex) {
            return false;
        }
        return true;
    }

    public final void disable() {
        if (updateScheduledTask != null) {
            updateScheduledTask.cancel(true);
            updateScheduledTask = null;
            updateTask = null;
        }
        parser = null;
    }

    private boolean initData() {
        if ((SearchIdsProperties.dataXml == null) || (SearchIdsProperties.dataXml.equals(""))) {
            return false;
        }

        File f = new File(SearchIdsProperties.dataXml);
        if (!f.exists()) {
            if (!updateTask.updateData(SearchIdsProperties.updateSource)) {
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

    @Override
    public void severe(String msg, Throwable thrown) {
        getLogman().log(Level.SEVERE, msg, thrown);
    }
}
