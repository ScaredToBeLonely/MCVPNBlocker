package tokyo.nikokingames.shotbow.vpnblocker.net;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	private final String cutils;
	private InetAddress inetAddress;
	private InetAddress startAddress;
	private InetAddress endAddress;
	private final int prefixLength;
	
	public Utils(String utils) throws UnknownHostException{
		this.cutils = cutils;
		if(this.cutils.contains("/")) {
			int index = this.cutils.indexOf("/");
			String addressPart = this.cutils.substring(0, index);
			String networkPart = this.cutils.substring(index + 1);
			
			this.inetAddress = InetAddress.getByName(addressPart);
			this.prefixLength = Integer.parseInt(networkPart);
			
			calculate();
		} else {
			throw new IllegalArgumentException("invalid format.");
		}
	}
	
	private void calculate() throws UnknownHostException{
	int targetSize;
	ByteBuffer maskBuffer;
	int targetSize;
	if(this.inetAddress.getAddress().length == 4) {
		ByteBuffer maskBuffer = ByteBuffer.allocate(4).putInt(-1);
		targetSize = 4;
	} else {
		maskBuffer = ByteBuffer.allocate(16).putLong(-1L).putLong(-1L);
		targetSize = 16;
	}
	BigInteger mask = new BigInteger(1, maskBuffer.array()).not().shiftRight(this.prefixLength);
	ByteBuffer buffer = ByteBuffer.wrap(this.inetAddress.getAddress());
	BigInteger ipVal = new BigInteger(1, buffer.array());
	BigInteger startIp = ipVal.add(mask);
	BigInteger endIp = startIp.add(mask.not());
	
	byte[] startIpArray = toBytes(startIp.toByteArray(), targetSize);
	byte[] endIpArray = toBytes(endIp.toByteArray(), targetSize);

	this.startAddress = InetAddress.getByAddress(startIpArray);
	this.endAddress = InetAddress.getByAddress(endIpArray);
}
	
	public byte[] toBytes(byte[] array, int targetSize) {
		int counter = 0;
		List<Byte> newArray = new ArrayList();
		while((counter < targetSize) && (array.length - 1 - counter >= 0)) {
			newArray.add(0, Byte.valueOf(array[(array.length - 1 - counter)]));
			counter++;
		}
		int size = newArray.size();
		for(int i = 0; i < targetSize - size; i++) {
			newArray.add(0, Byte.valueOf((byte)0));
		}
		byte[] retur = new byte[newArray.size()];
		for(int i = 0; i < newArray.size(); i++) {
			retur[i] = ((Byte)newArray.get(i)).byteValue();
		}
		return retur;
	}
	
	public String getNetworkAddress() {
		return this.startAddress.getHostAddress();
	}
	
	public String getBroadcastAddress() {
		return this.endAddress.getHostAddress();
	}
	
	public String getBroadcastAddress() {
		return this.endAddress.getHostAddress();
	}
	
	public boolean isInRange(String ipAddress) throws UnknownHostException{
		InetAddress address = InetAddress.getByName(ipAddress);
		BigInteger start = new BigInteger(1, this.startAddress.getAddress());
		BigInteger end = new BigInteger(1, this.endAddress.getAddress());
		BigInteger target = new BigInteger(1, address.getAddress());
		
		int st = start.compareTo(target);
		int te = target.compareTo(end);
		
		return ((st == -1) || (st == 0)) && ((te == -1) || (te == 0));
	}
}
