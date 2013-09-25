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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Update Task
 *
 * @author Jason (darkdiplomat)
 */
public final class UpdateTask implements Runnable {
    private SearchIds ids;

    public UpdateTask(SearchIds ids) {
        this.ids = ids;
    }

    public final void run() {
        updateData(SearchIdsProperties.updateSource);
    }

    public final boolean updateData(String source_url) {
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        boolean success = false, altUp = false;
        try {
            URL website = new URL(source_url);
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(SearchIdsProperties.dataXml);
            ids.info("Updating data from " + source_url + "...");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            ids.info("Update Successful!");
            success = true;
        }
        catch (MalformedURLException e) {
            if (source_url.equals(SearchIdsProperties.updateSource)) {
                ids.warning("Update from " + source_url + " Failed. Attempting Alternate Source...");
                altUp = true;
            }
            else {
                ids.warning("Update from " + source_url + " Failed.");
            }
        }
        catch (IOException ioex) {
            if (source_url.equals(SearchIdsProperties.updateSource)) {
                ids.warning("Update from " + source_url + " Failed. Attempting Alternate Source...");
                altUp = true;
            }
            else {
                ids.severe("Could not update search data. (Bad URL(s)?)", ioex);
                ioex.printStackTrace();
            }
        }
        finally {
            if (rbc != null && rbc.isOpen()) {
                try {
                    rbc.close();
                }
                catch (IOException ioex) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException ioex) {
                }
            }
        }
        return altUp ? updateData(SearchIdsProperties.updateSourceALT) : success;
    }
}
