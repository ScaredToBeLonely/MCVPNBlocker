package tokyo.nikokingames.shotbow.vpnblocker;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import tokyo.nikokingames.shotbow.vpnblocker.container.Cache;
import tokyo.nikokingames.shotbow.vpnblocker.net.Manager;

public class VPNBlocker
  extends JavaPlugin
  implements Listener
{
  public boolean isCached = false;
  public String apiKey = null;
  public String api_package = null;
  public ConfigReader configReader;
  public Cache cache;
  public final String CACHEFOLDER = "cache" + File.separator;
  public Manager container;
  
  public void onLoad()
  {
    File file = new File(getDataFolder(), "config.yml");
    if (!file.exists()) {
      saveDefaultConfig();
    }
  }
  
  public void onEnable()
  {
    this.container = new Manager(this, getDataFolder() + File.separator + "settings.dat");
    this.container.initDataStream();
    
    this.configReader = new ConfigReader(this);
    getServer().getPluginManager().registerEvents(this, this);
    if (this.configReader.getAPICache())
    {
      new File(getDataFolder() + File.separator + this.CACHEFOLDER).mkdir();
      this.cache = new Cache(getDataFolder() + File.separator + this.CACHEFOLDER);
      this.cache.set_cache_extension(".json");
      this.cache.set_cache_time(this.configReader.getAPICacheTime());
    }
    if (this.configReader.getAPIKey().isEmpty()) {
      getLogger().log(Level.INFO, "No API key specified, using free package.");
    } else {
      this.apiKey = this.configReader.getAPIKey();
    }
    getCommand("vpnguard").setExecutor(new Commands(this));
  }
  
  public void onDisable()
  {
    this.container.saveDataStream();
    this.configReader.onDisable();
  }
  
  @EventHandler
  public void onLogin(PlayerJoinEvent e)
  {
    String address = e.getPlayer().getAddress().getAddress().getHostAddress();
    if (this.configReader.getLogging()) {
      getLogger().log(Level.INFO, "Player {0} is trying to connect with IP: {1}", new Object[] { e.getPlayer().getName(), address });
    }
    if ((e.getPlayer().isOp()) && (this.configReader.getBypassOps()))
    {
      if (this.configReader.getLogging()) {
        getLogger().log(Level.INFO, "Player {0} is operative bypassing check", e.getPlayer().getName());
      }
    }
    else if (e.getPlayer().hasPermission("vpnguard.permission.bypass"))
    {
      if (this.configReader.getLogging()) {
        getLogger().log(Level.INFO, "Bypassing check for player {0}", e.getPlayer().getName());
      }
    }
    else if (((Boolean)this.container.data.get("subnet_toggle")).booleanValue())
    {
      Manager manager = new Manager(this, e.getPlayer().getAddress().getAddress().getHostAddress());
      if (manager.isBanned())
      {
        e.getPlayer().kickPlayer(this.configReader.getSubnetMessage());
        if (this.configReader.getLogging()) {
          getLogger().log(Level.INFO, "{0} is joining from a IP Range that you have subnet banned.", e.getPlayer().getName());
        }
      }
      else
      {
        callAPI(e.getPlayer());
      }
    }
    else
    {
      callAPI(e.getPlayer());
    }
  }
  
  private void callAPI(Player player)
  {
    getServer().getScheduler().runTaskAsynchronously(this, new CallAPI(this, 0, player, null, null));
  }
  
  public String msg(String msg)
  {
    return this.configReader.getPrefix() + msg;
  }
}
