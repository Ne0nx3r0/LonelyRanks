package com.ne0nx3r0.lonely.ranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpy
        extends LonelyCommand
        implements CommandExecutor
{
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
    {
        if (!(cs instanceof Player))
        {
            cs.sendMessage(ChatColor.RED + "Not from console!");

            return true;
        }
        if (!cs.hasPermission("lonely.commands.commandspy"))
        {
            cs.sendMessage(ChatColor.RED + "You don't have permission!");
            cs.sendMessage(ChatColor.RED + "Node: " + ChatColor.RESET + "lonely.commands.commandspy");

            return true;
        }
        if (!cs.hasPermission("lonely.commands.commandspy.spying"))
        {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "exec u:" + cs.getName() + " a:addperm v:lonely.commands.commandspy.spying");

            cs.sendMessage("Commandspy enabled!");
        }
        else
        {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "exec u:" + cs.getName() + " a:rmperm v:lonely.commands.commandspy.spying");

            cs.sendMessage("Commandspy disabled!");
        }
        return true;
    }
}
