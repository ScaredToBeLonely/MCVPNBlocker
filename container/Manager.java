package tokyo.nikokingames.shotbow.vpnblocker.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import tokyo.nikokingames.shotbow.vpnblocker.ShotbowVPNBlocker;

public class Manager extends Str{
	
	public HashMap data;
	
	public Manager(ShotbowVPNBlocker plugin, String dataFile) {
		super(plugin, dataFile);
	}
	
	public void initDataStream() {
		ShotbowVPNBlocker plugin = super.getPlugin();
		try {
			super.init();
			this.data = super.getMap();
			pupulateDefaultData();
		}
		catch(IOException|ClassNotFoundException ex){
			plugin.getLogger().log(Level.INFO, "Creating data file: {0}", super.getDataFileName());
			this.data = new HashMap();
			pupulateDefaultData();
			saveDataStream();
		}
	}
	
	public void saveDataStream() {
		ShotbowVPNBlocker plugin = super.getPlugin();
		super.setMap(this.data);
		try {
			super.save();
		}
		catch(IOException ex) {
			Plu
			gin.getLogger().log(Level.SEVERE, "Save failed. {0}", super.getDataFileName();
			ex.printStackTrace();
		}
	}
	
	  private void populateDefaultData()
	  {
	    if (!this.data.containsKey("countries"))
	    {
	      ArrayList<String> country = new ArrayList();
	      this.data.put("countries", country);
	    }
	    if (!this.data.containsKey("country_toggle")) {
	      this.data.put("country_toggle", Boolean.valueOf(true));
	    }
	    if (!this.data.containsKey("subnet_toggle")) {
	      this.data.put("subnet_toggle", Boolean.valueOf(true));
	    }
	    if (!this.data.containsKey("subnets"))
	    {
	      ArrayList<String> subnets = new ArrayList();
	      this.data.put("subnets", subnets);
	    }
	    if (!this.data.containsKey("notify"))
	    {
	      ArrayList<UUID> notify = new ArrayList();
	      this.data.put("notify", notify);
	    }
	    if (!this.data.containsKey("allowed_orgs"))
	    {
	      ArrayList<String> allowed_orgs = new ArrayList();
	      this.data.put("allowed_orgs", allowed_orgs);
	    }
	    if (!this.data.containsKey("history"))
	    {
	      HashMap history = new HashMap();
	      this.data.put("history", history);
	    }
	  }
	}
