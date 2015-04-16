package com.ne0nx3r0.lonely.ranks;

import com.earth2me.essentials.Essentials;
import com.ne0nx3r0.badges.LonelyBadgesAPI;
import com.ne0nx3r0.lonely.ranks.commands.*;
import com.ne0nx3r0.lonely.ranks.listeners.LonelyRanksPlayerListener;
import com.ne0nx3r0.lonely.ranks.listeners.LonelyRanksServerListener;
import com.ne0nx3r0.lonely.ranks.motd.MessageOfTheDayManager;
import com.ne0nx3r0.lonely.ranks.nickname.NicknameManager;
import com.ne0nx3r0.lonely.ranks.ranks.RankManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class LonelyRanksPlugin
        extends JavaPlugin
{
    public NicknameManager nicknameManager;
    public Economy economy;
    public Permission permissions;
    public RankManager rankManager;
    private Essentials essentials;
    private MessageOfTheDayManager motdm;
    private LonelyBadgesAPI lb;
    public final String LR_VETERAN_KILLS = "lr_veteran_kills";
    public final String LR_VIP_KILLS = "lr_vip_kills";
    public final String LR_MODERATOR_KILLS = "lr_moderator_kills";
    public final String LR_ADMIN_KILLS = "lr_admin_kills";
    public final String LR_ABOVE_NEWBIE_RANK = "lr_above_newbie_rank";
    public final String LR_NE0NX3R0_KILLS = "lr_killed_ne0nx3r0";
    public final String LR_PLATINUM_KILLS = "lr_platinum_kills";
    public final String LR_WAFFLE_SAID = "lr_waffle_said";
    public final String LR_HAS_ALL_FRIENDLY_EGGS = "lr_has_friendly_eggs";
    public final String LR_HAS_DRAGON_EGG = "lr_has_dragon_egg";
    public final String LR_GOLD_SWORD_KILLS = "lr_gold_sword_kills";
    public final String LR_RANK_VIP = "lr_rank_vip";

    public void onEnable()
    {
        try
        {
            getDataFolder().mkdirs();

            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                copy(getResource("config.yml"), configFile);
            }
        }
        catch (IOException ex)
        {
            getLogger().log(Level.INFO, "Unable to load config! Disabling");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }
        reloadConfig();
        if ((!setupPermissions()) || (!setupEconomy()))
        {
            getLogger().log(Level.INFO, "Unable to load economy or permissions! Disabling");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }
        this.rankManager = new RankManager(this);

        this.nicknameManager = new NicknameManager(this);

        this.essentials = ((Essentials)getServer().getPluginManager().getPlugin("Essentials"));

        this.motdm = new MessageOfTheDayManager();

        getServer().getPluginManager().registerEvents(new LonelyRanksPlayerListener(this), this);

        getServer().getPluginManager().registerEvents(new LonelyRanksServerListener(this), this);

        getCommand("rank").setExecutor(new Rank(this));
        getCommand("mc").setExecutor(new MC(this));
        getCommand("nickname").setExecutor(new Nickname(this));
        getCommand("namecolor").setExecutor(new Namecolor(this));
        getCommand("commandspy").setExecutor(new CommandSpy());
        getCommand("namespy").setExecutor(new NameSpy());
        getCommand("list").setExecutor(new List(this));
        getCommand("buymotd").setExecutor(new BuyMOTD(this));
        getCommand("rename").setExecutor(new Rename(this));


        LonelyBadgesAPI lb = (LonelyBadgesAPI)getServer().getPluginManager().getPlugin("LonelyBadges");
        if ((lb != null) && (lb.isEnabled()))
        {
            this.lb = lb;

            getClass();this.lb.registerBadgeProperty("lr_veteran_kills", "# of veteran ranked player kills");
            getClass();this.lb.registerBadgeProperty("lr_vip_kills", "# of vip ranked player kills");
            getClass();this.lb.registerBadgeProperty("lr_platinum_kills", "# of platinum ranked player kills");
            getClass();this.lb.registerBadgeProperty("lr_moderator_kills", "# of moderator ranked player kills");
            getClass();this.lb.registerBadgeProperty("lr_admin_kills", "# of admin ranked player kills");
            getClass();this.lb.registerBadgeProperty("lr_above_newbie_rank", "Player is above newbie rank (=1)");
            getClass();this.lb.registerBadgeProperty("lr_killed_ne0nx3r0", "# of Ne0nx3r0 kills");
            getClass();this.lb.registerBadgeProperty("lr_waffle_said", "# of times said the word waffle");
            getClass();this.lb.registerBadgeProperty("lr_has_friendly_eggs", "1 if player has the eggs");
            getClass();this.lb.registerBadgeProperty("lr_has_dragon_egg", "1 if player has a dragon egg");
            getClass();this.lb.registerBadgeProperty("lr_gold_sword_kills", "number of times a player has killed another player with a gold sword");
            getClass();this.lb.registerBadgeProperty("lr_rank_vip", "Ranked to VIP");
        }
        final LonelyRanksPlugin plugin = this;
        getServer().getScheduler().runTaskTimer(this, new Runnable()
        {
            public void run()
            {
                for (Player p : plugin.getServer().getOnlinePlayers())
                {
                    if (hasAllEggs(p)) {
                        plugin.getLonelyBadgesAPI().setGlobalBadgeProperty(p.getUniqueId(), "lr_has_friendly_eggs", 1);
                    }
                    if (p.getInventory().contains(Material.DRAGON_EGG)) {
                        plugin.getLonelyBadgesAPI().setGlobalBadgeProperty(p.getUniqueId(), "lr_has_dragon_egg", 1);
                    }
                }
            }

            byte[] allEggsData = { 65, 93, 92, 96, 98, 90, 91, 94, 95 };

            public boolean hasAllEggs(Player player)
            {
                ArrayList<Byte> eggs = new ArrayList();
                for (ItemStack is : player.getInventory().getContents()) {
                    if ((is != null) && (is.getType().equals(Material.MONSTER_EGG))) {
                        eggs.add(Byte.valueOf(is.getData().getData()));
                    }
                }
                for (byte b : this.allEggsData) {
                    if (!eggs.contains(Byte.valueOf(b))) {
                        return false;
                    }
                }
                return true;
            }
        }, 1200L, 1200L);
    }

    public void copy(InputStream in, File file)
            throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = ((Economy)rsp.getProvider());

        return this.economy != null;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

        this.permissions = ((Permission)rsp.getProvider());

        return this.permissions != null;
    }

    public Essentials getEssentials()
    {
        if (!this.essentials.isEnabled()) {
            this.essentials = ((Essentials)getServer().getPluginManager().getPlugin("Essentials"));
        }
        return this.essentials;
    }

    public MessageOfTheDayManager getMOTDManager()
    {
        return this.motdm;
    }

    public LonelyBadgesAPI getLonelyBadgesAPI()
    {
        return this.lb;
    }
}
