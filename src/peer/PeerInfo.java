package peer;

import java.util.ArrayList;

public class PeerInfo {
	
	/**
	 * Information about rmi
	 */
	public static final class rmi{
		public static final String index= "rmi://localhost:8808/service";
		public static final String fileSharing = "rmi://localhost:8888/share";
		public static final int port = 8888;
	}
	
	/**
	 * Local information
	 */
	public static class local{
		public static String IP = "";
		public static String name = "";
		public static String path = "";
	}
	
	/**
	 * Destination information
	 */
	public static class dest{
		public static ArrayList<String> destList = null;
		public static String destination = "";
		public static String destRmi = "";
	}
}
