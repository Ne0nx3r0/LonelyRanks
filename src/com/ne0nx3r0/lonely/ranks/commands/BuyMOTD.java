package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyMOTD
        extends LonelyCommand
        implements CommandExecutor {
    private final LonelyRanksPlugin plugin;
    private long cooldown;

    public BuyMOTD(LonelyRanksPlugin plugin) {
        this.plugin = plugin;

        this.cooldown = System.currentTimeMillis();
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (!hasCommandPermission(cs, "lonely.commands.buymotd", "View or pay to change the message of the day")) {
            return true;
        }
        long secondsLeft = (this.cooldown - System.currentTimeMillis()) / 1000L;
        String sSecondsLeft;
        if (secondsLeft < 1L) {
            sSecondsLeft = "Can be updated now";
        } else {
            sSecondsLeft = secondsLeft + " seconds left";
        }
        if (args.length == 0) {
            send(cs, new String[]{"Message of the day - Usage", "/buymotd - view the current MOTD and usage", "/buymotd <message> - change the MOTD for " + this.plugin.economy.format(5.0D), "", "Current message of the day (" + ChatColor.GOLD + sSecondsLeft + ChatColor.RESET + "):", ChatColor.YELLOW + this.plugin.getMOTDManager().getMOTD()});


            return true;
        }
        if (System.currentTimeMillis() < this.cooldown) {
            cs.sendMessage(ChatColor.RED + "You cannot update the MOTD since it was set recently, you need to wait " + (this.cooldown - System.currentTimeMillis()) / 1000L + " seconds!");

            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        String motd = sb.substring(1);
        if (((cs instanceof Player)) && (!this.plugin.getMOTDManager().isSafeMOTD(motd))) {
            cs.sendMessage(ChatColor.RED + "You cannot set the MOTD to: " + ChatColor.RESET + motd);

            return true;
        }
        double cost = 5.0D;
        if ((cs instanceof Player)) {
            if (!this.plugin.economy.has(cs.getName(), cost)) {
                cs.sendMessage(ChatColor.RED + "You don't have " + this.plugin.economy.format(cost) + ChatColor.RED + "!");

                return true;
            }
            EconomyResponse response = this.plugin.economy.withdrawPlayer(cs.getName(), cost);
            if (!response.transactionSuccess()) {
                cs.sendMessage(ChatColor.RED + "An error occurred: " + response.errorMessage);

                return true;
            }
        }
        this.plugin.getMOTDManager().setMOTD(motd);

        String displayName = "CONSOLE";
        if ((cs instanceof Player)) {
            displayName = ((Player) cs).getDisplayName();
        }
        this.plugin.getServer().broadcastMessage(ChatColor.GOLD + displayName + ChatColor.GOLD + " has paid " + this.plugin.economy.format(5.0D) + ChatColor.GOLD + " to set the message of the day to:");
        this.plugin.getServer().broadcastMessage(motd);


        this.cooldown = (System.currentTimeMillis() + 1800000L);

        return true;
    }
}
