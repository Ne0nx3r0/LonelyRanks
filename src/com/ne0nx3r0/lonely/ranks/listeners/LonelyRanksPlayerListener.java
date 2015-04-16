package com.ne0nx3r0.lonely.ranks.listeners;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.api.CoolPointsAPI;
import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.ranks.LonelyRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class LonelyRanksPlayerListener
        implements Listener {
    private final LonelyRanksPlugin plugin;
    private final Map<String, String> lastMessages;
    private final Map<String, String> chatColorReplacements;
    private final CoolPointsAPI CPAPI;

    public LonelyRanksPlayerListener(LonelyRanksPlugin plugin) {
        this.plugin = plugin;

        this.CPAPI = ((CoolPointsPlugin) plugin.getServer().getPluginManager().getPlugin("CoolPoints")).getCoolPointsAPI();

        this.lastMessages = new HashMap();
        this.chatColorReplacements = new HashMap();
        ConfigurationSection chatColorSection = plugin.getConfig().getConfigurationSection("chat_colors");
        for (String key : chatColorSection.getKeys(false)) {
            this.chatColorReplacements.put(key, chatColorSection.getString(key));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("lonely.ranks.hideJoinQuit")) {
            e.setJoinMessage("");

            return;
        }
        this.plugin.nicknameManager.loadPlayerColor(e.getPlayer());

        String sAka = "";
        if (this.plugin.nicknameManager.hasNickname(e.getPlayer())) {
            sAka = " aka " + e.getPlayer().getName();
        }
        e.setJoinMessage(ChatColor.GRAY + e.getPlayer().getDisplayName() + ChatColor.GRAY + sAka + " (" + this.CPAPI.getCoolPoints(e.getPlayer().getName()) + "CP) " + ChatColor.GRAY + "has joined the game");

        final Player p = e.getPlayer();

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
            public void run() {
                p.sendMessage(ChatColor.GRAY + "The current message of the day is: ");
                p.sendMessage(LonelyRanksPlayerListener.this.plugin.getMOTDManager().getMOTD());
            }
        }, 40L);
        if (this.plugin.rankManager.getRankOf(p.getName()).getName().toLowerCase().equals("vip")) {
            this.plugin.getClass();
            this.plugin.getLonelyBadgesAPI().setGlobalBadgeProperty(e.getPlayer().getUniqueId(), "lr_rank_vip", 1);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().hasPermission("lonely.ranks.hideJoinQuit")) {
            e.setQuitMessage("");

            return;
        }
        String sAka = "";
        if (this.plugin.nicknameManager.hasNickname(e.getPlayer())) {
            sAka = " aka " + e.getPlayer().getName();
        }
        e.setQuitMessage(ChatColor.GRAY + e.getPlayer().getDisplayName() + ChatColor.GRAY + sAka + " has left the game");

        this.plugin.nicknameManager.unloadPlayerColor(e.getPlayer());

        this.lastMessages.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String lastMessage = (String) this.lastMessages.get(e.getPlayer().getName());
        if (e.getMessage().equals(lastMessage)) {
            e.setCancelled(true);

            e.getPlayer().sendMessage(ChatColor.RED + "You just said that.");

            return;
        }
        this.lastMessages.put(e.getPlayer().getName(), e.getMessage());
        if (e.getPlayer().hasPermission("lonely.ranks.chatColor")) {
            for (Map.Entry<String, String> entry : this.chatColorReplacements.entrySet()) {
                e.setMessage(e.getMessage().replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue()));
            }
        }
        LonelyRank rank = this.plugin.rankManager.getRankOf(e.getPlayer().getName());
        if (this.plugin.nicknameManager.hasNickname(e.getPlayer())) {
            ArrayList<Player> playersToMessageRealNamesTo = new ArrayList();

            Iterator<Player> iterator = e.getRecipients().iterator();
            while (iterator.hasNext()) {
                Player current = (Player) iterator.next();
                if (current.hasPermission("lonely.commands.namespy.spying")) {
                    playersToMessageRealNamesTo.add(current);

                    iterator.remove();
                }
            }
            for (Player p : playersToMessageRealNamesTo) {
                p.sendMessage(rank.getPrefix() + " (" + e.getPlayer().getName() + ") " + this.plugin.nicknameManager.getPlayerDisplayName(e.getPlayer()) + ": " + e.getMessage());
            }
        }
        e.setFormat(rank.getPrefix() + this.plugin.nicknameManager.getPlayerDisplayName(e.getPlayer()) + ": " + ChatColor.WHITE + "%2$s");
        if ((e.getMessage().toLowerCase().contains("waffle")) || (e.getPlayer().getDisplayName().toLowerCase().contains("waffle"))) {
            this.plugin.getClass();
            this.plugin.getLonelyBadgesAPI().getBadgeManager().AdjustGlobalBadgeProperty(e.getPlayer().getUniqueId(), "lr_waffle_said", 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/op")) {
            e.setCancelled(true);
        }
        if ((e.getMessage().startsWith("/")) && (!e.getMessage().startsWith("/lurk ")) && (!e.getMessage().startsWith("/mc ")) && (!e.getPlayer().hasPermission("lonely.commands.commandspy.ninja"))) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if ((p != e.getPlayer()) && (p.hasPermission("lonely.commands.commandspy.spying"))) {
                    p.sendMessage(ChatColor.DARK_GRAY + e.getPlayer().getName() + ": " + ChatColor.GRAY + e.getMessage());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player pDied = e.getEntity();

        Player pKiller = e.getEntity().getKiller();
        if (pKiller != null) {
            if (pKiller.equals(pDied)) {
                return;
            }
            if ((pKiller.getItemInHand() != null) && (pKiller.getItemInHand().getType().equals(Material.GOLD_SWORD))) {
                this.plugin.getClass();
                this.plugin.getLonelyBadgesAPI().adjustGlobalBadgeProperty(pKiller.getUniqueId(), "lr_gold_sword_kills", 1);
            }
            if ((pDied.getName().equals("Ne0nx3r0")) && (!pKiller.getName().equalsIgnoreCase("dark78660"))) {
                this.plugin.getClass();
                incrementRankKills(pKiller.getUniqueId(), "lr_killed_ne0nx3r0");
            }
            if (this.plugin.getMOTDManager() != null) {
                LonelyRank rank = this.plugin.rankManager.getRankOf(pDied.getName());
                switch (rank.getName()) {
                    case "admin":
                        this.plugin.getClass();
                        incrementRankKills(pKiller.getUniqueId(), "lr_admin_kills");
                        break;
                    case "moderator":
                        this.plugin.getClass();
                        incrementRankKills(pKiller.getUniqueId(), "lr_moderator_kills");
                        break;
                    case "vip":
                        this.plugin.getClass();
                        incrementRankKills(pKiller.getUniqueId(), "lr_vip_kills");
                        break;
                    case "veteran":
                        this.plugin.getClass();
                        incrementRankKills(pKiller.getUniqueId(), "lr_veteran_kills");
                        break;
                    case "platinum":
                        this.plugin.getClass();
                        incrementRankKills(pKiller.getUniqueId(), "lr_platinum_kills");
                }
            }
        }
    }

    private void incrementRankKills(UUID uuid, String propertyName) {
        this.plugin.getLonelyBadgesAPI().adjustGlobalBadgeProperty(uuid, propertyName, 1);
    }
}
