package tokyo.nikokingames.shotbow.vpnblocker.reportsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ConnectToHTML implements Runnable{
	
	private String REPORT_FOLDER;
	private static final DateFormat SDF = new DateFormat("yyyy-MM-dd_HH-mm-ss");
	private String plugin_version;
	private CommandSender sender;
	private ShotbowVPNBlocker plugin;
	
	public MakeReport(ShotbowVPNBlocker plugin, CommandSender sender) {
		this.plugin = plugin;
		this.sender = sender;
		this.REPORT_FOLDER = (plugin.getDataFolder() + File.separator + "reports" + File.separator);
		if(!new File(this.REPORT_FOLDER).isDirectory()) {
			new File(this.REPORT_FOLDER).mkdirs();
		}
		this.plugin_version = plugin.getServer().getPluginManager("ShotbowVPNBlocker").getDescription().getVersion();
	}
	
	public void run() {
		try {
			Date date = new Date();
			String filename = "rp_" + SDF.format(date) + ".html";
			File f = new File(this.REPORT_FOLDER + filename);
			this.sender.sendMessage(this.plugin.msg("Making report..." + filename));
			String table = createUserTable();
			
			InputStream in = getClass().getResourceAsStream("/tokyo/nikokingames/shotbow/vpnblocker/reportsystem/index.html");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.replace("$filename$", filename);
				line = line.replace("$version$", filename);
				line = line.replace("$usertable$", filename);
				writer.write(line);
			}
			reader.close();
			writer.close();
			
			HashMap<Object, Object> history = (HashMap)this.plugin.stream.data.get("history");
			history.clear();
			this.sender.sendMessage(this.plugin.msg("Report: " + this.REPORT_FOLDER + filename));
		}
		catch(Exception e) {
			this.sender.sendMessage(this.plugin.msg("Couldn't create report, check console for error messages."));
			e.printStackTrace();
		}
	}
	
	  private String generateUserTable()
	  {
	    StringBuilder out = new StringBuilder();
	    out.append("<table class=\"table table-striped\"><thead><tr><th></th><th>Player</th><th>IP Addresses</th></tr></thead><tbody>");
	    
	    HashMap<Object, Object> history = (HashMap)this.plugin.stream.data.get("history");
	    for (Map.Entry<Object, Object> entry : history.entrySet())
	    {
	      UUID uuid = (UUID)entry.getKey();
	      OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(uuid);
	      String playerUsername = player.getName();
	      
	      ArrayList<String> nested = (ArrayList)entry.getValue();
	      
	      StringBuilder ipAppender = new StringBuilder();
	      for (String ip : nested)
	      {
	        String api;
	        String api;
	        if (this.plugin.apiKey == null)
	        {
	          api = "http://api.vpnblocker.net/v2/html/" + ip;
	        }
	        else
	        {
	          String api;
	          if ((this.plugin.api_package.equals("Basic")) || (this.plugin.api_package.equals("Professional"))) {
	            api = "https://api.vpnblocker.net/v2/html/" + ip + "/" + this.plugin.apiKey;
	          } else {
	            api = "http://api.vpnblocker.net/v2/html/" + ip + "/" + this.plugin.apiKey;
	          }
	        }
	        ipAppender.append("<a target=\"blank\" href=\"" + api + "\">" + ip + "</a>&nbsp;");
	      }
	      out.append("<tr><td><img src=\"https://minotar.net/helm/" + playerUsername + "/64.png\"></td>" + "<td>Username: " + playerUsername + "<br>UUID: " + uuid
	      
	        .toString() + "</td>" + "<td>" + ipAppender
	        .toString() + "</td>" + "</tr>");
	      
	      System.out.println();
	    }
	    out.append("</tbody></table>");
	    
	    return out.toString();
	  }
	}

