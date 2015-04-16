package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.nickname.NicknameManager;
import java.util.ArrayList;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Nickname
        extends LonelyCommand
        implements CommandExecutor
{
    private final LonelyRanksPlugin plugin;

    public Nickname(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args)
    {
        if (!hasCommandPermission(cs, "lonely.ranks.nickname", "change your nickname")) {
            return true;
        }
        if ((args.length > 1) && (!hasCommandPermission(cs, "lonely.ranks.nickname.others", "change the nickname of others"))) {
            return true;
        }
        if ((args.length == 0) || ((args.length == 1) && (!(cs instanceof Player))))
        {
            String availableColors = "";
            for (ChatColor cc : this.plugin.nicknameManager.getAllowedColors()) {
                availableColors = availableColors + cc + "$" + cc.toString().substring(1) + ChatColor.RESET + ", ";
            }
            ArrayList<String> usageLines = new ArrayList();

            usageLines.add("Usage");
            usageLines.add("/nickname <nickname>");
            usageLines.add("/nickname reset");
            if (cs.hasPermission("lonely.ranks.nickname.others")) {
                usageLines.add("/nickname <playerName> <nickname>");
            }
            usageLines.add("");
            usageLines.add("Available colors: " + availableColors);
            usageLines.add("");
            usageLines.add("Note: costs " + this.plugin.economy.format(this.plugin.nicknameManager.getNickNameChangeCost()));

            send(cs, (String[])usageLines.toArray(new String[usageLines.size()]));

            return true;
        }
        Player playerToChange = null;
        String wantedNickname = "";
        if (args.length == 1)
        {
            playerToChange = (Player)cs;
            wantedNickname = args[0];
        }
        else
        {
            playerToChange = Bukkit.getPlayer(args[0]);
            if (playerToChange == null)
            {
                cs.sendMessage(ChatColor.RED + args[0] + " is not a valid player!");

                return true;
            }
            wantedNickname = args[1];
        }
        if (wantedNickname.equalsIgnoreCase("reset"))
        {
            this.plugin.nicknameManager.resetNickName(playerToChange);

            send(playerToChange, new String[] { "Nickname", "Your nickname was reset for free!" });
            if (cs != playerToChange) {
                send(cs, new String[] { "Nickname", playerToChange.getName() + "'s nickname was reset!" });
            }
            return true;
        }
        if (!wantedNickname.matches("[a-zA-Z0-9-_$]+"))
        {
            cs.sendMessage(ChatColor.RED + wantedNickname + " is invalid! (a-z, A-Z, 0-9, -_)");

            return true;
        }
        if (cs == playerToChange)
        {
            if (!this.plugin.economy.has(cs.getName(), this.plugin.nicknameManager.getNickNameChangeCost()))
            {
                cs.sendMessage(ChatColor.RED + "You need at least " + this.plugin.economy.format(this.plugin.nicknameManager.getNickNameChangeCost()) + "!");

                return true;
            }
            EconomyResponse transactionResult = this.plugin.economy.withdrawPlayer(cs.getName(), this.plugin.nicknameManager.getNickNameChangeCost());
            if (!transactionResult.transactionSuccess())
            {
                cs.sendMessage(ChatColor.RED + "An error occurred: " + transactionResult.errorMessage);

                return true;
            }
        }
        wantedNickname = wantedNickname.replace("$", "§");

        wantedNickname = wantedNickname.replace(ChatColor.MAGIC.toString(), "");

        wantedNickname = wantedNickname.replace(ChatColor.BLACK.toString(), ChatColor.DARK_GRAY.toString());

        this.plugin.nicknameManager.setNickname(playerToChange, wantedNickname);

        this.plugin.getServer().broadcastMessage(cs.getName() + " has changed their nickname to " + wantedNickname + ChatColor.RESET + "!");
        if (cs == playerToChange)
        {
            send(playerToChange, new String[] { "Nickname", "Your name has been updated to " + wantedNickname + "! (at a cost of " + this.plugin.economy.format(this.plugin.nicknameManager.getNickNameChangeCost()) + ")" });
        }
        else
        {
            send(cs, new String[] { "Nickname", playerToChange.getName() + "'s name has been updated to " + wantedNickname + "!" });

            send(playerToChange, new String[] { "Nickname", "Your name has been updated to " + wantedNickname + "!" });
        }
        return true;
    }
}
