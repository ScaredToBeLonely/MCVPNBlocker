package tokyo.nikokingames.shotbow.vpnblocker.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import tokyo.nikokingames.shotbow.vpnblocker.ShotbowVPNBlocker;

public class Manager {
	
	private ShotbowVPNBlocker plugin;
	private String ip;
	
	public Manager(ShotbowVPNBlocker plugin, String ip) {
		this.ip = ip;
		this.plugin = plugin;
	}
	
	public boolean isBanned() {
		ArrayList<String> subnets = (ArrayList)this.plugin.stream.data.get("subnets");
		boolean banned = false;
		for(String subnet : subnets) {
			try {
				Utils utils = new Utils(subnet);
				long IPMin = ipToLong(InetAddress.getByName(utils.getNetworkAddress()));
				long IPMax = ipToLong(InetAddress.getByName(utils.getBroadcastAddress()));
				long IP = ipToLong(InetAddress.getByName(this.ip));
				
				banned = (IP <= IPMax) && (IP >= IPMin);
				if(banned) {
					break;
				}
			}
			catch(UnknownHostException ex) {
				banned = false;
			}
		}
		return banned;
	}
	
	private long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0L;
		for(byte octet : octets) {
			result <<= 8;
			result |= octet & 0xFF;
		}
		return result;
	}

}
