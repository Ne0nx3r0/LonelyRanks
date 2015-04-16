package com.ne0nx3r0.lonely.ranks.nickname;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.ranks.RankManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class NicknameManager
{
    private final LonelyRanksPlugin plugin;
    private final HashMap<String, String> customPlayerNames;
    private double NICKNAME_CHANGE_COST;
    private int NAMECOLOR_CHANGE_COST;
    private List<ChatColor> ALLOWED_COLORS;

    public NicknameManager(LonelyRanksPlugin plugin)
    {
        this.plugin = plugin;

        this.customPlayerNames = new HashMap();

        File nicksFile = new File(plugin.getDataFolder(), "nicks.yml");

        YamlConfiguration customColors = YamlConfiguration.loadConfiguration(nicksFile);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (customColors.isSet(player.getName()))
            {
                this.customPlayerNames.put(player.getName(), customColors.getString(player.getName()) + ChatColor.RESET);

                player.setDisplayName(customColors.getString(player.getName()) + ChatColor.RESET);
            }
            else
            {
                player.setDisplayName(plugin.rankManager.getRankColorFor(player.getName()) + player.getName() + ChatColor.RESET);
            }
        }
        this.NICKNAME_CHANGE_COST = plugin.getConfig().getInt("nickname_change_cost", 5);
        this.NAMECOLOR_CHANGE_COST = plugin.getConfig().getInt("namecolor_change_cost", 5);

        this.ALLOWED_COLORS = new ArrayList();
        ArrayList<ChatColor> bannedColors = new ArrayList();
        for (String color : plugin.getConfig().getStringList("banned_colors")) {
            try
            {
                bannedColors.add(ChatColor.valueOf(color.toUpperCase()));
            }
            catch (Exception e)
            {
                plugin.getLogger().log(Level.WARNING, "{0} is not a valid color!", color);
            }
        }
        for (ChatColor cc : ChatColor.values()) {
            if (!bannedColors.contains(cc)) {
                this.ALLOWED_COLORS.add(cc);
            }
        }
    }

    public void loadPlayerColor(Player player)
    {
        File nicksFile = new File(this.plugin.getDataFolder(), "nicks.yml");

        YamlConfiguration customColors = YamlConfiguration.loadConfiguration(nicksFile);
        if (customColors.isSet(player.getName()))
        {
            this.customPlayerNames.put(player.getName(), customColors.getString(player.getName()) + ChatColor.RESET);

            player.setDisplayName(customColors.getString(player.getName()) + ChatColor.RESET);
        }
        else
        {
            player.setDisplayName(this.plugin.rankManager.getRankColorFor(player.getName()) + player.getName() + ChatColor.RESET);
        }
    }

    public void unloadPlayerColor(Player player)
    {
        this.customPlayerNames.remove(player.getName());
    }

    public void setPlayerCustomName(Player p, String toString)
    {
        this.customPlayerNames.put(p.getName(), toString);

        p.setDisplayName(toString);

        File nicksFile = new File(this.plugin.getDataFolder(), "nicks.yml");

        YamlConfiguration customColors = YamlConfiguration.loadConfiguration(nicksFile);

        customColors.set(p.getName(), toString);
        try
        {
            customColors.save(nicksFile);
        }
        catch (IOException ex)
        {
            this.plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void removePlayerCustomColor(Player p)
    {
        p.setDisplayName(this.plugin.rankManager.getRankColorFor(p.getName()) + p.getName() + ChatColor.RESET);

        this.customPlayerNames.remove(p.getName());

        File nicksFile = new File(this.plugin.getDataFolder(), "nicks.yml");

        YamlConfiguration customColors = YamlConfiguration.loadConfiguration(nicksFile);

        customColors.set(p.getName(), null);
        try
        {
            customColors.save(nicksFile);
        }
        catch (IOException ex)
        {
            this.plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public String getPlayerDisplayName(Player player)
    {
        if (this.customPlayerNames.containsKey(player.getName())) {
            return (String)this.customPlayerNames.get(player.getName());
        }
        return this.plugin.rankManager.getRankColorFor(player.getName()) + player.getName();
    }

    public void setNickname(Player p, String sName)
    {
        setPlayerCustomName(p, this.plugin.rankManager.getRankColorFor(p.getName()) + "~" + sName + ChatColor.RESET);
    }

    public void resetNickName(Player player)
    {
        removePlayerCustomColor(player);
    }

    public boolean hasNickname(Player player)
    {
        String sNickname = (String)this.customPlayerNames.get(player.getName());
        if ((sNickname != null) && (sNickname.contains("~"))) {
            return true;
        }
        return false;
    }

    public double getNickNameChangeCost()
    {
        return this.NICKNAME_CHANGE_COST;
    }

    public double getNamecolorChangeCost()
    {
        return this.NAMECOLOR_CHANGE_COST;
    }

    public List<ChatColor> getAllowedColors()
    {
        return this.ALLOWED_COLORS;
    }
}
