package com.ne0nx3r0.lonely.ranks.ranks;

import org.bukkit.ChatColor;

public class LonelyRank
{
    private final String name;
    private final boolean staff;
    private final String prefix;
    private final ChatColor color;

    LonelyRank(String name, ChatColor color, boolean staff, String prefix)
    {
        this.name = name;
        this.color = color;
        this.staff = staff;
        this.prefix = prefix;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isStaff()
    {
        return this.staff;
    }

    public String getPrefix()
    {
        if (this.prefix == null) {
            return "";
        }
        return "[" + this.prefix + "] ";
    }

    public String getDisplayName()
    {
        return this.color + this.name + ChatColor.RESET;
    }

    public ChatColor getColor()
    {
        return this.color;
    }
}
