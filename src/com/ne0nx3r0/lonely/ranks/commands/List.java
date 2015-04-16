package com.ne0nx3r0.lonely.ranks.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.ranks.LonelyRank;
import com.ne0nx3r0.lonely.ranks.ranks.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List
        extends LonelyCommand
        implements CommandExecutor
{
    private final LonelyRanksPlugin plugin;

    public List(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
    {
        if (!cs.hasPermission("lonely.commands.list"))
        {
            cs.sendMessage(ChatColor.RED + "You don't have permission!");
            cs.sendMessage(ChatColor.RED + "Node: " + ChatColor.RESET + "lonely.commands.list");

            return true;
        }
        StringBuilder staffOnline = new StringBuilder();
        StringBuilder playersOnline = new StringBuilder();

        int staffTotal = 0;
        int playersTotal = 0;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            Essentials e = this.plugin.getEssentials();

            boolean isAFK = false;
            if (e != null)
            {
                User user = e.getUser(player.getName());
                if ((user != null) &&
                        (user.isAfk())) {
                    isAFK = true;
                }
            }
            if (this.plugin.rankManager.getRankOf(player.getName()).isStaff())
            {
                staffOnline.append(", ").append(player.getDisplayName());
                if (isAFK) {
                    staffOnline.append(ChatColor.YELLOW).append(" [AFK]").append(ChatColor.RESET);
                }
                staffTotal++;
            }
            else
            {
                playersOnline.append(", ").append(player.getDisplayName());
                if (isAFK) {
                    playersOnline.append(ChatColor.YELLOW).append(" [AFK]").append(ChatColor.RESET);
                }
                playersTotal++;
            }
        }
        send(cs, new String[] { "Staff Online (" + staffTotal + ")", staffOnline.length() > 2 ? staffOnline.substring(2) : "", "", ChatColor.DARK_PURPLE + "---" + ChatColor.RESET + " Players Online (" + playersTotal + ") " + ChatColor.DARK_PURPLE + "---", playersOnline.length() > 2 ? playersOnline.substring(2) : "" });






        return true;
    }
}
