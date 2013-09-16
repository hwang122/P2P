package peer;

import java.util.ArrayList;

public class PeerInfo {
	
	/**
	 * Information about rmi
	 */
	public static final class rmi{
		public static final String index= "rmi://localhost:8808/service";
		public static final int[] portList = {8888, 8889, 8890};
		public static int port = 0;
		public static String fileSharing = "";
	}
	
	/**
	 * Local information
	 */
	public static class local{
		public static String IP = "";
		public static String name = "";
		public static String ID = "";
		public static String path = "./share";
		public static ArrayList<String> fileList = new ArrayList<String>();
	}
	
	/**
	 * Destination(Peer Server) information
	 */
	public static class dest{
		public static ArrayList<String> destList = new ArrayList<String>();
		public static String destination = "";
		public static String destRmi = "";
		public static String destPath = "";
	}
}
