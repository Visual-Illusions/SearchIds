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

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.searchids.SearchIdsProperties;

public class SearchCommandListener implements CommandListener {

    private CanarySearchIds searchids;

    public SearchCommandListener(CanarySearchIds searchids) throws CommandDependencyException {
        this.searchids = searchids;
        Canary.commands().registerCommands(this, searchids, false);
    }

    @Command(aliases = { "search" },
        description = "Searches for the Id of a Item or Block",
        permissions = { "searchids.search" },
        toolTip = "/search <Item|Block name>")
    public void searchCommand(MessageReceiver receiver, String[] cmd) {
        if (cmd.length > 1) {
            String query = "";
            for (int i = 1; i < cmd.length; i++) {
                query = query + cmd[i] + " ";
            }
            query = query.trim();
            if (receiver instanceof Player) {
                searchids.printSearchResults((Player) receiver, CanarySearchIds.parser.search(query, SearchIdsProperties.base), query);
            }
            else {
                searchids.printConsoleSearchResults(CanarySearchIds.parser.search(query, SearchIdsProperties.base), query);
            }
        }
        else {
            if (receiver instanceof Player) {
                receiver.message("§cCorrect usage is: /search <Item|Block name>");
            }
            else {
                System.out.println("Correct usage is: /search [item to search for]");
            }
        }
    }
}
