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
package net.visualillusionsent.spout.server.plugin.searchids;

import net.visualillusionsent.searchids.SearchIdsProperties;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;

/**
 * Command Executor for Spout
 * 
 * @author Jason (darkdiplomat)
 */
public class SpoutSearchCommandExecutor {

    private final SpoutSearchIds searchids;

    SpoutSearchCommandExecutor(SpoutSearchIds searchids) {
        this.searchids = searchids;
    }

    @Command(aliases = { "search" }, usage = "<query>", desc = "Searches.")
    @Permissible("searchids.search")
    public void search(CommandSource source, CommandArguments args) throws CommandException {
        if (args.length() > 1) {
            String query = args.getJoinedString(1).trim();
            if (source instanceof Player) {
                searchids.printSearchResults((Player) source, SpoutSearchIds.parser.search(query, SearchIdsProperties.base), query);
            }
            else {
                searchids.printConsoleSearchResults(SpoutSearchIds.parser.search(query, SearchIdsProperties.base), query);
            }
        }
        else {
            source.sendMessage("§cCorrect usage is: /search [item to search for]");
        }
    }
}
