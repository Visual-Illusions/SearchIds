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
 * Update Thread
 * 
 * @author croemmich
 * @author Jason (darkdiplomat)
 */
public final class UpdateThread implements Runnable {

    private boolean running = false;
    private Thread thread;
    private SearchIds ids;

    public UpdateThread(SearchIds ids) {
        this.ids = ids;
    }

    public final void run() {
        while (running) {
            try {
                Thread.sleep(SearchIdsProperties.autoUpdateInterval);
            }
            catch (InterruptedException IE) {
                if (running) { //Only send message if running
                    ids.warning("An Error occured in UpdateThread.");
                }
            }
            if (running) { //Make sure we don't update if not running
                ids.updateData(SearchIdsProperties.updateSource);
            }
        }
    }

    public final void start() {
        running = true;
        thread = new Thread(this);
    }

    public final void stop() {
        running = false;
        thread.interrupt();
        thread = null;
    }
}
