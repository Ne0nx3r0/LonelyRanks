package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.nickname.NicknameManager;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Namecolor
        extends LonelyCommand
        implements CommandExecutor
{
    private final LonelyRanksPlugin plugin;

    public Namecolor(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
    {
        if (!hasCommandPermission(cs, "lonely.ranks.namecolor", "change your namecolor")) {
            return true;
        }
        if ((args.length == 2) && (!hasCommandPermission(cs, "lonely.ranks.namecolor.others", "change the namecolor of others"))) {
            return true;
        }
        if ((args.length == 0) || ((args.length == 1) && (!(cs instanceof Player))))
        {
            String sColors = "";
            for (ChatColor cc : this.plugin.nicknameManager.getAllowedColors()) {
                if (cc.equals(ChatColor.MAGIC)) {
                    sColors = sColors + ChatColor.RESET + cc.name().toLowerCase() + ", ";
                } else {
                    sColors = sColors + cc + cc.name().toLowerCase() + ChatColor.RESET + ", ";
                }
            }
            send(cs, new String[] { "Usage", "/namecolor <color>", "/namecolor <playername> <color>", "", "Available colors: ", sColors.substring(sColors.length() - 4) });







            return true;
        }
        Player playerToChange = null;
        String wantedColor = "";
        if (args.length == 1)
        {
            playerToChange = (Player)cs;
            wantedColor = args[0];
        }
        else
        {
            playerToChange = Bukkit.getPlayer(args[0]);
            if (playerToChange == null)
            {
                cs.sendMessage(ChatColor.RED + args[0] + " is not a valid player!");

                return true;
            }
            wantedColor = args[1];
        }
        ChatColor colorToChangeTo = null;
        for (ChatColor cc : ChatColor.values()) {
            if (cc.name().equalsIgnoreCase(wantedColor))
            {
                colorToChangeTo = cc;

                break;
            }
        }
        if (colorToChangeTo == null)
        {
            cs.sendMessage(ChatColor.RED + wantedColor + " is not a valid color!");

            return true;
        }
        if (!this.plugin.nicknameManager.getAllowedColors().contains(colorToChangeTo))
        {
            cs.sendMessage(ChatColor.RED + wantedColor + " is not allowed.");

            return true;
        }
        if (colorToChangeTo.equals(ChatColor.RESET))
        {
            this.plugin.nicknameManager.removePlayerCustomColor(playerToChange);
            if (cs.equals(playerToChange)) {
                send(cs, new String[] { "Namecolor", "Your namecolor has been reset for free!" });
            } else {
                send(cs, new String[] { "Namecolor", playerToChange.getName() + "'s namecolor has been reset!" });
            }
            return true;
        }
        if (args.length < 2)
        {
            if (!this.plugin.economy.has(cs.getName(), this.plugin.nicknameManager.getNamecolorChangeCost()))
            {
                cs.sendMessage(ChatColor.RED + "You do not have enough LonelyBucks to use this! (Cost = " + this.plugin.economy.format(this.plugin.nicknameManager.getNamecolorChangeCost()) + ChatColor.RED + ")");

                return true;
            }
            EconomyResponse transactionResult = this.plugin.economy.withdrawPlayer(cs.getName(), this.plugin.nicknameManager.getNamecolorChangeCost());
            if (!transactionResult.transactionSuccess())
            {
                cs.sendMessage(ChatColor.RED + "An error occurred: " + transactionResult.errorMessage);

                return true;
            }
        }
        this.plugin.nicknameManager.setPlayerCustomName(playerToChange, colorToChangeTo.toString() + playerToChange.getName() + ChatColor.RESET);
        if (cs.equals(playerToChange))
        {
            send(cs, new String[] { "Namecolor", "You have paid " + this.plugin.economy.format(this.plugin.nicknameManager.getNamecolorChangeCost()) + " to change your namecolor!" });
        }
        else
        {
            send(playerToChange, new String[] { "Namecolor", "Your name color has been changed to " + colorToChangeTo + colorToChangeTo.name() + ChatColor.RESET + "!" });



            send(cs, new String[] { "Namecolor", "You have changed " + playerToChange.getName() + "'s color to " + colorToChangeTo + ChatColor.RESET + "!" });
        }
        return true;
    }
}
