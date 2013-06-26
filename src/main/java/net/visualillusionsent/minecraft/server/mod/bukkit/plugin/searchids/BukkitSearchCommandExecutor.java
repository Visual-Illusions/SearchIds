package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.searchids;

import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.spout.server.plugin.searchids.SpoutSearchIds;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Executor for Bukkit
 * 
 * @author Jason (darkdiplomat)
 */
public class BukkitSearchCommandExecutor implements CommandExecutor {

    private final BukkitSearchIds searchids;

    BukkitSearchCommandExecutor(BukkitSearchIds searchids) {
        this.searchids = searchids;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("search")) {
            if (args.length > 1) {
                String query = StringUtils.join(args, ' ', 1, args.length - 1);
                if (sender instanceof Player) {
                    searchids.printSearchResults((Player) sender, SpoutSearchIds.parser.search(query, SearchIdsProperties.base), query);
                }
                else {
                    searchids.printConsoleSearchResults(SpoutSearchIds.parser.search(query, SearchIdsProperties.base), query);
                }
            }
            else {
                sender.sendMessage("§cCorrect usage is: /search [item to search for]");
            }
            return true;
        }
        return false;
    }

}
