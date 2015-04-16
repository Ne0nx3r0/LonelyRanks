package com.ne0nx3r0.lonely.ranks.commands;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Rename extends LonelyCommand implements CommandExecutor {
    private final LonelyRanksPlugin plugin;

    public Rename(LonelyRanksPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (!hasCommandPermission(cs, "lonely.ranks.rename", "rename items")) {
            return true;
        }

        if(args.length == 0){
            cs.sendMessage(ChatColor.GRAY+"Usage: "+ChatColor.RESET+"/rename New Item Name"+ChatColor.RED+" (must be holding an item)");

            java.util.List<ChatColor> allowedColors = this.plugin.nicknameManager.getAllowedColors();

            StringBuilder sAllowedColors = new StringBuilder();

            for(ChatColor cc : allowedColors){
                sAllowedColors.append(", ").append(cc.toString()+"&"+cc.getChar());
            }

            cs.sendMessage(ChatColor.GRAY+"Available colors: "+ChatColor.RESET+sAllowedColors.substring(2));

            return true;
        }

        if(!(cs instanceof Player)){
            cs.sendMessage("Not from console.");

            return true;
        }

        Player player = (Player) cs;
        ItemStack isHand = player.getItemInHand();

        if(isHand == null || isHand.getType().equals(Material.AIR)){
            cs.sendMessage(ChatColor.RED+"You must be holding an item!");

            return true;
        }

        double cost = 5.0;

        if (!this.plugin.economy.has(cs.getName(), cost)) {
            cs.sendMessage(ChatColor.RED
                    + "You do not have enough "+this.plugin.economy.currencyNamePlural()
                    +" to use this! (Cost = "
                    + this.plugin.economy.format(cost) + ChatColor.RED + ")");

            return true;
        }

        EconomyResponse transactionResult = this.plugin.economy.withdrawPlayer(cs.getName(), cost);
        if (!transactionResult.transactionSuccess()) {
            cs.sendMessage(ChatColor.RED + "An error occurred: " + transactionResult.errorMessage);

            return true;
        }

        StringBuilder newItemName = new StringBuilder();

        for(String arg : args){
            newItemName.append(" ").append(arg);
        }

        String sNewItemName = newItemName.substring(1);

        for(ChatColor cc : ChatColor.values()){
            sNewItemName.replace("&"+cc.getChar(),cc.toString());
        }

        ItemMeta meta = isHand.getItemMeta();

        meta.setDisplayName(sNewItemName);

        isHand.setItemMeta(meta);

        cs.sendMessage("Paid "+plugin.economy.format(cost)+" to set "+isHand.getType()+"'s name to: "+sNewItemName);

        return true;
    }
}