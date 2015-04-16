package com.ne0nx3r0.lonely.ranks.motd;

public class MessageOfTheDayManager
{
    private String motd = "The Loneliest MC Server!";

    public void setMOTD(String message)
    {
        this.motd = message;
    }

    public String getMOTD()
    {
        return this.motd;
    }

    String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    String HOSTNAME_REGEX = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    public boolean isSafeMOTD(String motd)
    {
        if (motd.matches(this.IP_REGEX)) {
            return false;
        }
        if (motd.matches(this.HOSTNAME_REGEX)) {
            return false;
        }
        return true;
    }
}
