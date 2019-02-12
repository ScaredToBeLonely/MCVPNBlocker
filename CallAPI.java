package tokyo.nikokingames.shotbow.vpnblocker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import tokyo.nikokingames.shotbow.vpnblocker.container.Cache;

public class CallAPI
  implements Runnable
{
  private int mode;
  private ShotbowVPNBlocker plugin;
  private final Player player;
  private CommandSender sender;
  private String ip;
  
  public CallAPI(ShotbowVPNBlocker plugin, int i, Player player, CommandSender sender, String ip)
  {
    this.plugin = plugin;
    this.mode = i;
    this.player = player;
    this.sender = sender;
    this.ip = ip;
  }
  
  public void run()
  {
    try
    {
      if (this.mode == 0)
      {
        String rawJSON = getJSON(this.player.getAddress().getAddress().getHostAddress());
        JSONObject result = new JSONObject(rawJSON);
        
        ArrayList<UUID> notifyPlayers = (ArrayList)this.plugin.stream.data.get("notify");
        ArrayList<String> allowed_orgs = (ArrayList)this.plugin.stream.data.get("allowed_orgs");
        String api_package;
        if (result.get("status").equals("success"))
        {
          if (((this.plugin.cache instanceof Cache)) && (!this.plugin.isCached)) {
            this.plugin.cache.set_cache(this.player.getAddress().getAddress().getHostAddress(), rawJSON);
          }
          api_package = result.getString("package");
          if (!this.plugin.isCached) {
            this.plugin.api_package = api_package;
          }
          String org = result.getString("org");
          boolean isHost = result.getBoolean("host-ip");
          String country = null;String cc = null;String subdivision = null;String city = null;String postal = null;
          long lat = 0L;long lon = 0L;
          if (api_package.equals("Professional"))
          {
            if (!result.getJSONObject("country").isNull("name")) {
              country = result.getJSONObject("country").getString("name");
            }
            if (!result.getJSONObject("country").isNull("code")) {
              cc = result.getJSONObject("country").getString("code");
            }
            if (!result.getJSONObject("subdivision").isNull("name")) {
              subdivision = result.getJSONObject("subdivision").getString("name");
            }
            if (!result.isNull("city")) {
              city = result.getString("city");
            }
            if (!result.isNull("postal")) {
              postal = result.getString("postal");
            }
            if (!result.getJSONObject("location").isNull("lat")) {
              lat = result.getJSONObject("location").getLong("lat");
            }
            if (!result.getJSONObject("location").isNull("long")) {
              lon = result.getJSONObject("location").getLong("long");
            }
          }
          if ((!allowed_orgs.contains(org)) && (isHost))
          {
            HashMap history;
            if (this.plugin.configReader.getReportLogging())
            {
              history = (HashMap)this.plugin.stream.data.get("history");
              if (history.containsKey(this.player.getUniqueId()))
              {
                ArrayList<String> nested = (ArrayList)history.get(this.player.getUniqueId());
                if (!nested.contains(this.player.getAddress().getAddress().getHostAddress())) {
                  nested.add(this.player.getAddress().getAddress().getHostAddress());
                }
              }
              else
              {
                ArrayList<String> nested = new ArrayList();
                nested.add(this.player.getAddress().getAddress().getHostAddress());
                history.put(this.player.getUniqueId(), nested);
              }
            }
            new BukkitRunnable()
            {
              public void run()
              {
                try
                {
                  Thread.sleep(CallAPI.this.plugin.configReader.getCommandsDelay());
                  Iterator<String> iterator = CallAPI.this.plugin.configReader.getCommands().iterator();
                  while ((iterator.hasNext()) && (CallAPI.this.player != null))
                  {
                    String command = (String)iterator.next();
                    command = command.replace("%p", CallAPI.this.player.getName());
                    command = command.replace("%ip", CallAPI.this.player.getAddress().getAddress().getHostAddress());
                    command = ChatColor.translateAlternateColorCodes('&', command);
                    CallAPI.this.plugin.getServer().dispatchCommand(CallAPI.this.plugin.getServer().getConsoleSender(), command);
                  }
                }
                catch (InterruptedException ex)
                {
                  Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            }
            
              .runTask(this.plugin);
            for (UUID uuid : notifyPlayers)
            {
              Player notifyPlayer = this.plugin.getServer().getPlayer(uuid);
              if ((notifyPlayer != null) && (notifyPlayer.isOnline())) {
                notifyPlayer.sendMessage(this.plugin.msg(this.player.getName() + " tried connecting with a anonymizer."));
              }
            }
            if (this.plugin.configReader.getLogging()) {
              if (api_package.equals("Professional"))
              {
                this.plugin.getLogger().log(Level.INFO, "{0} is using a anonymizer: IP Details -> Org: {1}, Country: {2}, Subdivision: {3}, City: {4}", new Object[] {this.player
                  .getName(), org, country, subdivision, city });
              }
              else
              {
                this.plugin.getLogger().log(Level.INFO, "{0} is using a anonymizer: IP Details -> Org: {1}", new Object[] {this.player
                  .getName(), org });
                if ((!this.plugin.isCached) && (api_package.equals("Free")))
                {
                  int remaining_requests = result.getInt("remaining_requests");
                  this.plugin.getLogger().log(Level.INFO, "You have {0} API Requests remaining on your Free Package", Integer.valueOf(remaining_requests));
                }
              }
            }
          }
          else if (allowed_orgs.contains(org))
          {
            if (this.plugin.configReader.getLogging()) {
              this.plugin.getLogger().log(Level.INFO, "{0} connected from a whitelisted ISP/Organization.", this.player.getName());
            }
            if ((!this.plugin.isCached) && (api_package.equals("Free")))
            {
              int remaining_requests = result.getInt("remaining_requests");
              this.plugin.getLogger().log(Level.INFO, "You have {0} API Requests remaining on your Free Package.", Integer.valueOf(remaining_requests));
            }
          }
          else if (api_package.equals("Professional"))
          {
            Boolean country_toggle = (Boolean)this.plugin.stream.data.get("country_toggle");
            ArrayList<String> countryList = (ArrayList)this.plugin.stream.data.get("countries");
            String type = country_toggle.booleanValue() ? "Blacklist" : "Whitelist";
            final String countryName = country;
            if (country_toggle.booleanValue())
            {
              if (countryList.contains(cc))
              {
                new BukkitRunnable()
                {
                  public void run()
                  {
                    try
                    {
                      Thread.sleep(CallAPI.this.plugin.configReader.getCommandsDelay());
                      String command = CallAPI.this.plugin.configReader.getCountryDenyMessage();
                      command = command.replace("%p", CallAPI.this.player.getName());
                      if (countryName != null) {
                        command = command.replace("%countryname", countryName);
                      } else {
                        command = command.replace("%countryname", "Unknown");
                      }
                      CallAPI.this.player.kickPlayer(command);
                    }
                    catch (InterruptedException ex1)
                    {
                      Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                  }
                }
                
                  .runTask(this.plugin);
                if (this.plugin.configReader.getLogging()) {
                  this.plugin.getLogger().log(Level.INFO, "{0} was kicked because of the country {1}", new Object[] { this.player.getName(), type });
                }
              }
              else if (this.plugin.configReader.getLogging())
              {
                this.plugin.getLogger().log(Level.INFO, "{0} has passed ShotbowVPNBlocker checks.", this.player.getName());
              }
            }
            else if (!countryList.contains(cc))
            {
              new BukkitRunnable()
              {
                public void run()
                {
                  try
                  {
                    Thread.sleep(CallAPI.this.plugin.configReader.getCommandsDelay());
                    String command = CallAPI.this.plugin.configReader.getCountryDenyMessage();
                    command = command.replace("%p", CallAPI.this.player.getName());
                    if (countryName != null) {
                      command = command.replace("%countryname", countryName);
                    } else {
                      command = command.replace("%countryname", "Unknown");
                    }
                    CallAPI.this.player.kickPlayer(command);
                  }
                  catch (InterruptedException ex1)
                  {
                    Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex1);
                  }
                }
              }
              
                .runTask(this.plugin);
              if (this.plugin.configReader.getLogging()) {
                this.plugin.getLogger().log(Level.INFO, "{0} was kicked because of the country {1}", new Object[] { this.player.getName(), type });
              }
            }
            else if (this.plugin.configReader.getLogging())
            {
              this.plugin.getLogger().log(Level.INFO, "{0} has passed ShotbowVPNBlocker checks.", this.player.getName());
            }
          }
          else
          {
            if (this.plugin.configReader.getLogging()) {
              this.plugin.getLogger().log(Level.INFO, "{0} has passed ShotbowVPNBlocker checks.", this.player.getName());
            }
            if ((!this.plugin.isCached) && (api_package.equals("Free")))
            {
              int remaining_requests = result.getInt("remaining_requests");
              this.plugin.getLogger().log(Level.INFO, "You have {0} API Requests remaining on your Free Package.", Integer.valueOf(remaining_requests));
            }
          }
        }
        else if (result.get("status").equals("failed"))
        {
          if ((result.getString("msg").contains("Invalid API Key")) || (result.getString("msg").contains("Payment Overdue")))
          {
            this.plugin.getLogger().log(Level.SEVERE, "API Server Returned Error Message: {0} Shutting down server to prevent blacklisting on API Database", result.get("msg"));
            this.plugin.getServer().shutdown();
          }
          else if (!this.plugin.configReader.getBypassCheck())
          {
            new BukkitRunnable()
            {
              public void run()
              {
                CallAPI.this.player.kickPlayer(CallAPI.this.plugin.configReader.getBypassMessage());
              }
            }
            
              .runTask(this.plugin);
          }
          for (UUID uuid : notifyPlayers)
          {
            Player notifyPlayer = this.plugin.getServer().getPlayer(uuid);
            if ((notifyPlayer != null) && (notifyPlayer.isOnline())) {
              notifyPlayer.sendMessage(this.plugin.msg("API Server Returned Error: " + result.getString("msg")));
            }
          }
          this.plugin.getLogger().log(Level.SEVERE, "API Server Returned Error Message: {0} when {1} tried to connect", new Object[] { result.get("msg"), this.player.getName() });
        }
      }
      else if (this.mode == 1)
      {
        String rawJSON = getJSON(this.ip);
        JSONObject result = new JSONObject(rawJSON);
        if (result.get("status").equals("success"))
        {
          if (((this.plugin.cache instanceof Cache)) && (!this.plugin.isCached)) {
            this.plugin.cache.set_cache(this.ip, rawJSON);
          }
          String api_package = result.getString("package");
          if (!this.plugin.isCached) {
            this.plugin.api_package = api_package;
          }
          String org = result.getString("org");
          boolean isHost = result.getBoolean("host-ip");
          long lat = 0L;long lon = 0L;
          
          this.sender.sendMessage(this.plugin.msg("IP Address: " + this.ip));
          this.sender.sendMessage(this.plugin.msg("Organization: " + org));
          if (api_package.equals("Professional"))
          {
            if (!result.getJSONObject("country").isNull("name"))
            {
              String country = result.getJSONObject("country").getString("name");
              this.sender.sendMessage(this.plugin.msg("Country: " + country));
            }
            if (!result.getJSONObject("subdivision").isNull("name"))
            {
              String subdivision = result.getJSONObject("subdivision").getString("name");
              this.sender.sendMessage(this.plugin.msg("Subdivision: " + subdivision));
            }
            if (!result.isNull("city"))
            {
              String city = result.getString("city");
              this.sender.sendMessage(this.plugin.msg("City: " + city));
            }
            if (!result.isNull("postal"))
            {
              String postal = result.getString("postal");
              this.sender.sendMessage(this.plugin.msg("Postal Code: " + postal));
            }
            if (!result.getJSONObject("location").isNull("lat")) {
              lat = result.getJSONObject("location").getLong("lat");
            }
            if (!result.getJSONObject("location").isNull("long")) {
              lon = result.getJSONObject("location").getLong("long");
            }
          }
          if ((lat != 0L) && (lon != 0L)) {
            this.sender.sendMessage(this.plugin.msg("Latitude: " + lat + " Longitude: " + lon));
          }
          if ((!this.plugin.isCached) && (api_package.equals("Free")))
          {
            int remaining_requests = result.getInt("remaining_requests");
            this.sender.sendMessage(this.plugin.msg("You have " + remaining_requests + " API Requests Remaining on your Free Package."));
          }
          if (isHost) {
            this.sender.sendMessage(this.plugin.msg(this.ip + " belongs to a hosting organization."));
          } else {
            this.sender.sendMessage(this.plugin.msg(this.ip + " does not seem to belong to a hosting organization. If you believe this is an error please report it to the API provider to have it fixed."));
          }
        }
        else if (result.get("status").equals("failed"))
        {
          this.sender.sendMessage("I'm sorry, API server returned the following error message: " + result.get("msg"));
        }
      }
    }
    catch (IOException|JSONException|CommandException ex)
    {
      if (this.mode == 0)
      {
        if (!this.plugin.configReader.getBypassCheck()) {
          new BukkitRunnable()
          {
            public void run()
            {
              try
              {
                Thread.sleep(CallAPI.this.plugin.configReader.getCommandsDelay());
                CallAPI.this.player.kickPlayer(CallAPI.this.plugin.configReader.getBypassMessage());
              }
              catch (InterruptedException ex1)
              {
                Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex1);
              }
            }
          }
          
            .runTask(this.plugin);
        }
        ex.printStackTrace();
        this.plugin.getLogger().log(Level.SEVERE, "Unable to perform anonymization checks when {0} tried to connect.", this.player.getName());
      }
      else if (this.mode == 1)
      {
        this.plugin.getLogger().log(Level.SEVERE, "Error: {0}", ex);
        this.sender.sendMessage(this.plugin.msg("Unable to grab data from API Server, check console for error logs."));
      }
    }
  }
  
  public String getJSON(String ip)
    throws IOException
  {
    String api;
    String api;
    if (this.plugin.apiKey == null) {
      api = "http://api.vpnblocker.net/v2/json/" + ip;
    } else {
      api = "http://api.vpnblocker.net/v2/json/" + ip + "/" + this.plugin.apiKey;
    }
    int timeout = this.plugin.configReader.getTimeout();
    
    String userAgent = "ShotbowVPNBlocker v" + this.plugin.getServer().getPluginManager().getPlugin("ShotbowVPNBlocker").getDescription().getVersion() + " (Bukkit:" + this.plugin.getServer().getBukkitVersion() + ") on " + this.plugin.getServer().getPort();
    
    StringBuilder response = new StringBuilder();
    URLConnection connection = new URL(api).openConnection();
    connection.setRequestProperty("User-Agent", userAgent);
    connection.setConnectTimeout(timeout);
    if ((this.plugin.cache instanceof Cache))
    {
      if (this.plugin.cache.is_cached(ip))
      {
        this.plugin.isCached = true;
        response.append(this.plugin.cache.get_cache(ip));
      }
      else
      {
        this.plugin.isCached = false;
        response.append(this.plugin.cache.grab_url(api, this.plugin.configReader.getTimeout(), userAgent));
      }
    }
    else {
      response.append(new Cache().grab_url(api, this.plugin.configReader.getTimeout(), userAgent));
    }
    return response.toString();
  }
}
