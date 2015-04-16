package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MC
        extends LonelyCommand
        implements CommandExecutor
{
    private final LonelyRanksPlugin plugin;

    public MC(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
    {
        if (!hasCommandPermission(cs, "lonely.ranks.mc", "Talk in ModChat")) {
            return false;
        }
        if (args.length == 0)
        {
            send(cs, new String[] { "Usage", "/mc <message>" });


            return true;
        }
        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + " " + args[i];
        }
        ConsoleCommandSender console = this.plugin.getServer().getConsoleSender();
        if ((cs instanceof Player)) {
            console.sendMessage(String.format("%s[MC] %s%s: %s", new Object[] { ChatColor.DARK_GREEN, ChatColor.WHITE, ((Player)cs).getDisplayName(), message }));
        } else {
            console.sendMessage(String.format("%s[MC] %sCONSOLE:%s", new Object[] { ChatColor.DARK_GREEN, ChatColor.WHITE, message }));
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lonely.ranks.mc")) {
                if ((cs instanceof Player)) {
                    p.sendMessage(ChatColor.DARK_GREEN + "[MC] " + ChatColor.WHITE + ((Player)cs).getDisplayName() + ":" + message);
                } else {
                    p.sendMessage(ChatColor.DARK_GREEN + "[MC] " + ChatColor.WHITE + "CONSOLE:" + message);
                }
            }
        }
        return true;
    }
}
