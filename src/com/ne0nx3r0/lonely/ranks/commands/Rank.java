package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.nickname.NicknameManager;
import com.ne0nx3r0.lonely.ranks.ranks.LonelyRank;
import com.ne0nx3r0.lonely.ranks.ranks.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Rank
        extends LonelyCommand
        implements CommandExecutor
{
    private final LonelyRanksPlugin plugin;

    public Rank(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args)
    {
        if (args.length == 0)
        {
            send(cs, new String[] { "Usage", "/rank <player>        " + ChatColor.GRAY + "- get a player's rank", "/rank <player> <rank> " + ChatColor.GRAY + "- set a player's rank" });




            return true;
        }
        if (args.length == 1)
        {
            LonelyRank rank = this.plugin.rankManager.getRankOf(args[0]);
            if (rank == null)
            {
                send(cs, new String[] { "Rank", args[0] + " doesn't seem to have a rank" });

                return true;
            }
            send(cs, new String[] { "Rank", args[0] + " is ranked " + rank.getDisplayName() });

            return true;
        }
        if (!hasCommandPermission(cs, "lonely.ranks.player", "rank other players")) {
            return true;
        }
        String sPlayer = args[0];
        String sRank = args[1];
        if (this.plugin.rankManager.getRankOf(sPlayer) == null)
        {
            cs.sendMessage(ChatColor.RED + sPlayer + " is not a valid player!");

            return true;
        }
        LonelyRank rank = this.plugin.rankManager.getRank(sRank);
        if (rank == null)
        {
            cs.sendMessage(ChatColor.RED + sRank + " is not a valid rank!");

            return true;
        }
        if ((cs instanceof Player)) {
            if (((rank.isStaff()) || (this.plugin.rankManager.getRankOf(sPlayer).isStaff())) &&
                    (!hasCommandPermission(cs, "lonely.ranks.admin", "rank to or from staff ranks"))) {
                return true;
            }
        }
        this.plugin.rankManager.setRank(sPlayer, rank);

        Player player = Bukkit.getPlayer(sPlayer);
        if (player != null) {
            this.plugin.nicknameManager.loadPlayerColor(player);
        }
        this.plugin.getServer().broadcastMessage(sPlayer + ChatColor.LIGHT_PURPLE + " has been given the " + rank.getDisplayName() + ChatColor.LIGHT_PURPLE + " rank");

        return true;
    }
}
