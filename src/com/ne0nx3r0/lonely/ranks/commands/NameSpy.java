/*  1:   */ package com.ne0nx3r0.lonely.ranks.commands;
/*  2:   */ 
/*  3:   */ import org.bukkit.Bukkit;
/*  4:   */ import org.bukkit.ChatColor;
/*  5:   */ import org.bukkit.Server;
/*  6:   */ import org.bukkit.command.Command;
/*  7:   */ import org.bukkit.command.CommandExecutor;
/*  8:   */ import org.bukkit.command.CommandSender;
/*  9:   */ import org.bukkit.entity.Player;
/* 10:   */ 
/* 11:   */ public class NameSpy
/* 12:   */   extends LonelyCommand
/* 13:   */   implements CommandExecutor
/* 14:   */ {
/* 15:   */   public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
/* 16:   */   {
/* 17:13 */     if (!(cs instanceof Player))
/* 18:   */     {
/* 19:14 */       cs.sendMessage(ChatColor.RED + "Not from console!");
/* 20:   */       
/* 21:16 */       return true;
/* 22:   */     }
/* 23:19 */     if (!cs.hasPermission("lonely.commands.namespy"))
/* 24:   */     {
/* 25:20 */       cs.sendMessage(ChatColor.RED + "You don't have permission!");
/* 26:21 */       cs.sendMessage(ChatColor.RED + "Node: " + ChatColor.RESET + "lonely.commands.namespy");
/* 27:   */       
/* 28:23 */       return true;
/* 29:   */     }
/* 30:26 */     if (!cs.hasPermission("lonely.commands.namespy.spying"))
/* 31:   */     {
/* 32:27 */       Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "exec u:" + cs.getName() + " a:addperm v:lonely.commands.namespy.spying");
/* 33:   */       
/* 34:29 */       cs.sendMessage("Namespy enabled!");
/* 35:   */     }
/* 36:   */     else
/* 37:   */     {
/* 38:32 */       Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "exec u:" + cs.getName() + " a:rmperm v:lonely.commands.namespy.spying");
/* 39:   */       
/* 40:34 */       cs.sendMessage("Namespy disabled!");
/* 41:   */     }
/* 42:37 */     return true;
/* 43:   */   }
/* 44:   */ }


/* Location:           M:\plugins\LonelyRanks.jar
 * Qualified Name:     com.ne0nx3r0.lonely.ranks.commands.NameSpy
 * JD-Core Version:    0.7.0.1
 */