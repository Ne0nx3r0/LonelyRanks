package com.ne0nx3r0.lonely.ranks.listeners;

import com.ne0nx3r0.lonely.ranks.LonelyRanksPlugin;
import com.ne0nx3r0.lonely.ranks.motd.MessageOfTheDayManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class LonelyRanksServerListener
        implements Listener
{
    private final MessageOfTheDayManager motdm;

    public LonelyRanksServerListener(LonelyRanksPlugin plugin)
    {
        this.motdm = plugin.getMOTDManager();
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onPing(ServerListPingEvent e)
    {
        e.setMotd(this.motdm.getMOTD());
    }
}
