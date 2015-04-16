package com.ne0nx3r0.lonely.ranks.ranks;

import com.ne0nx3r0.badges.LonelyBadgesAPI;
import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RankManager
{
  private final LonelyRanksPlugin plugin;
  private final ArrayList<LonelyRank> availableRanks;

  public RankManager(LonelyRanksPlugin plugin)
  {
    this.plugin = plugin;

    this.availableRanks = new ArrayList();

    ConfigurationSection playerRanks = plugin.getConfig().getConfigurationSection("player_ranks");
    for (String rankName : playerRanks.getKeys(false))
    {
      ChatColor color = ChatColor.WHITE;
      try
      {
        color = ChatColor.valueOf(playerRanks.getString(rankName + ".color", "WHITE"));
      }
      catch (Exception e)
      {
        plugin.getLogger().log(Level.INFO, "{0} has an invalid color! ({1})", new Object[] { rankName, playerRanks.getString(rankName + ".color", "WHITE") });
      }
      boolean isStaff = playerRanks.getBoolean(rankName + ".staffRank", false);

      String prefix = playerRanks.getString(rankName + ".prefix", null);

      this.availableRanks.add(new LonelyRank(rankName, color, isStaff, prefix));
    }
  }

  public LonelyRank getRankOf(String playerName)
  {
    String sRank = this.plugin.permissions.getPrimaryGroup((World)Bukkit.getWorlds().get(0), playerName);
    if (sRank.equals("default")) {
      sRank = "newbie";
    }
    return getRank(sRank);
  }

  public LonelyRank getRank(String rankName)
  {
    for (LonelyRank rank : this.availableRanks) {
      if (rank.getName().equalsIgnoreCase(rankName)) {
        return rank;
      }
    }
    return null;
  }

  public boolean setRank(String playerName, LonelyRank rank)
  {
    if (!this.availableRanks.contains(rank)) {
      return false;
    }
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "exec u:" + playerName + " a:setgroup v:" + rank.getName());

    LonelyBadgesAPI lbapi = this.plugin.getLonelyBadgesAPI();
    if (lbapi != null)
    {
      Player player = this.plugin.getServer().getPlayer(playerName);
      UUID uuid = null;
      if (player != null)
      {
        uuid = player.getUniqueId();
      }
      else
      {
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(playerName);
        if (offlinePlayer != null) {
          uuid = offlinePlayer.getUniqueId();
        }
      }
      if (uuid != null) {
        try
        {
          if (rank.getName().equalsIgnoreCase("newbie"))
          {
            this.plugin.getClass();lbapi.adjustGlobalBadgeProperty(uuid, "lr_above_newbie_rank", 0);
          }
          else
          {
            this.plugin.getClass();lbapi.adjustGlobalBadgeProperty(uuid, "lr_above_newbie_rank", 1);
          }
        }
        catch (Exception ex) {}
      }
    }
    return true;
  }

  public ChatColor getRankColorFor(String playerName)
  {
    return getRankOf(playerName).getColor();
  }
}