package tokyo.nikokingames.shotbow.vpnblocker;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader
{
  private static Integer timeout = null;
  private static Integer apicachetime = null;
  private static Integer commandsdelay = null;
  private static Boolean logging = null;
  private static Boolean bypassCheck = null;
  private static Boolean bypassOps = null;
  private static Boolean apicache = null;
  private static Boolean reportlogging = null;
  private static List<String> commands = null;
  private static String prefix = null;
  private static String api = null;
  private static String bypassMessage = null;
  private static String countryDenyMessage = null;
  private static String subnetMessage = null;
  private final ShotbowVPNBlocker instance;
  private static FileConfiguration config;
  
  public ConfigReader(ShotbowVPNBlocker instance)
  {
    this.instance = instance;
    config = instance.get
    		Config();
  }
  
  public String getPrefix()
  {
    if (prefix != null) {
      return prefix;
    }
    prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
    return prefix;
  }
  
  public String getAPIKey()
  {
    if (api != null) {
      return api;
    }
    api = config.getString("api-key");
    return api;
  }
  
  public List<String> getCommands()
  {
    if (commands != null) {
      return commands;
    }
    commands = config.getStringList("commands");
    return commands;
  }
  
  public int getCommandsDelay()
  {
    if (commandsdelay != null) {
      return commandsdelay.intValue();
    }
    commandsdelay = Integer.valueOf(config.getInt("commands-delay"));
    return commandsdelay.intValue();
  }
  
  public boolean getAPICache()
  {
    if (apicache != null) {
      return apicache.booleanValue();
    }
    apicache = Boolean.valueOf(config.getBoolean("api-cache"));
    return apicache.booleanValue();
  }
  
  public int getAPICacheTime()
  {
    if (apicachetime != null) {
      return apicachetime.intValue();
    }
    apicachetime = Integer.valueOf(config.getInt("api-cache-time"));
    return apicachetime.intValue();
  }
  
  public boolean getReportLogging()
  {
    if (reportlogging != null) {
      return reportlogging.booleanValue();
    }
    reportlogging = Boolean.valueOf(config.getBoolean("report-logging"));
    return reportlogging.booleanValue();
  }
  
  public String getCountryDenyMessage()
  {
    if (countryDenyMessage != null) {
      return countryDenyMessage;
    }
    countryDenyMessage = ChatColor.translateAlternateColorCodes('&', config.getString("country-deny-message"));
    return countryDenyMessage;
  }
  
  public String getSubnetMessage()
  {
    if (subnetMessage != null) {
      return subnetMessage;
    }
    subnetMessage = ChatColor.translateAlternateColorCodes('&', config.getString("subnet-banned-message"));
    return subnetMessage;
  }
  
  public int getTimeout()
  {
    if (timeout != null) {
      return timeout.intValue();
    }
    timeout = Integer.valueOf(config.getInt("timeout"));
    return timeout.intValue();
  }
  
  public boolean getLogging()
  {
    if (logging != null) {
      return logging.booleanValue();
    }
    logging = Boolean.valueOf(config.getBoolean("logging"));
    return logging.booleanValue();
  }
  
  public boolean getBypassCheck()
  {
    if (bypassCheck != null) {
      return bypassCheck.booleanValue();
    }
    bypassCheck = Boolean.valueOf(config.getBoolean("bypass-check"));
    return bypassCheck.booleanValue();
  }
  
  public boolean getBypassOps()
  {
    if (bypassOps != null) {
      return bypassOps.booleanValue();
    }
    bypassOps = Boolean.valueOf(config.getBoolean("bypass-ops"));
    return bypassOps.booleanValue();
  }
  
  public String getBypassMessage()
  {
    if (bypassMessage != null) {
      return bypassMessage;
    }
    bypassMessage = ChatColor.translateAlternateColorCodes('&', config.getString("bypass-message"));
    return bypassMessage;
  }
  
  public void onDisable()
  {
    prefix = null;
    config = null;
    commands = null;
    commandsdelay = null;
    countryDenyMessage = null;
    reportlogging = null;
    timeout = null;
    logging = null;
    bypassCheck = null;
    bypassOps = null;
    apicache = null;
    bypassMessage = null;
    apicachetime = null;
  }
}