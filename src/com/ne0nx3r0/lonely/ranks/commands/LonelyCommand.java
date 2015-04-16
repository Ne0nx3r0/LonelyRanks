package com.ne0nx3r0.lonely.ranks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LonelyCommand
{
    public boolean hasCommandPermission(CommandSender cs, String sPerm, String sAction)
    {
        if (!(cs instanceof Player)) {
            return true;
        }
        if (cs.hasPermission(sPerm)) {
            return true;
        }
        send(cs, new String[] { "Permissions", ChatColor.RED + "You do not have permission to " + ChatColor.WHITE + sAction + ChatColor.RED + ".", ChatColor.RED + "Permission node: " + ChatColor.WHITE + sPerm });



        return false;
    }

    public void send(CommandSender cs, String... args)
    {
        cs.sendMessage(ChatColor.DARK_PURPLE + "--- " + ChatColor.WHITE + args[0] + ChatColor.DARK_PURPLE + " ---");
        for (int i = 1; i < args.length; i++) {
            cs.sendMessage(args[i]);
        }
        cs.sendMessage("");
    }
}
