package tokyo.nikokingames.shotbow.vpnblocker.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import tokyo.nikokingames.shotbow.vpnblocker.ShotbowVPNBlocker;

public class Str {
	
	private ShotbowVPNBlocker plugin;
	private String dataFile;
	private HashMap map;
	
	public Str(ShotbowVPNBlocker plugin, String dataFile) {
		this.plugin = plugin;
		this.dataFile = dataFile;
	}
	
	public void init() throws FileNotFoundException, IOException, ClassNotFoundException{
		this.plugin.getLogger().log(Level.INFO, "Loading:  {0} ", getDataFileName());
		FileInputStream fis = new FileInputStream(this.dataFile);
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		this.map = ((HashMap)ois.readObject());
		ois.close();
		this.plugin.getLogger().log(Level.INFO, "{0} load complete:", getDataFileName());
	}
	
	public void save() throws IOException{
		this.plugin.getLogger().log(Level.INFO, "Saving: {0}", getDataFileName());
		FileOutputStream fos = new FileOutputStream(this.dataFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this.map);
		oos.close();
		this.plugin.getLogger().log(Level.INFO, "{0} load complete:", getDataFileName());
		FileOutputStream fos = new FileOutputStream(this.dataFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this.map);
		this.plugin.getLogger().log(Level.INFO, "Saving: {0}", getDataFileName());
	}
	
	public ShotbowVPNBlocker getPlugin() {
		return this.plugin;
	}
	
	public String getDataFileName() {
		return new File(this.dataFile).getName();
	}
	
	public HashMap getMap() {
		return this.map;
	}
	
	public void setMap(HashMap map) {
		this.map = map;
	}

}
