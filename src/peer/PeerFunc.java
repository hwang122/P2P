package peer;

import interfaces.PeerInterface;
import interfaces.ServerInterface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class PeerFunc {
	/**
	 * Interface of Server
	 */
	public static ServerInterface indexService;
	public static PeerInterface fileService;
	
	/**
	 * Constructor, initialization
	 */
	public PeerFunc() {
		try {
			indexService = (ServerInterface)Naming.lookup(PeerInfo.rmi.index);
		} 
		catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get local host's IP address and name as peer ID
	 */
	public void intialize(){
		try{
			InetAddress addr = InetAddress.getLocalHost();
			PeerInfo.local.IP = addr.getHostAddress();
			PeerInfo.local.name = addr.getHostName();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get service from server and register
	 * @param IP
	 * @param filename
	 */
	public void register(String IP, String filename){
		try {
			indexService.registry(IP, filename);
		} 
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get search service from server and Get List
	 */
	public void search(String filename){
		try{
			PeerInfo.dest.destList = new ArrayList<String>(indexService.search(filename));
			PeerInfo.dest.destination = PeerInfo.dest.destList.get(0);
			
			if(PeerInfo.dest.destination != null){
				System.out.println("Find file!");
				PeerInfo.dest.destRmi = "rmi://" + PeerInfo.dest.destination + ":8888/share";
			}
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtain the file from server(client)
	 */
	public void obtain(String filename){
		try {
			fileService = (PeerInterface)Naming.lookup(PeerInfo.dest.destRmi);
			writeFile(filename, fileService.share(filename));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeFile(String filename, byte[] fileContent){
		FileOutputStream outStr = null;
		BufferedOutputStream buffer = null;
		try{
			outStr = new FileOutputStream(new File(filename));
			buffer = new BufferedOutputStream(outStr);
			buffer.write(fileContent);
			buffer.flush();
			buffer.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Fail to find file!");
			e.printStackTrace();
		}
		catch(IOException e){
			System.out.println("Fail to write file content!");
			e.printStackTrace();
		}
		finally{
			try{
				buffer.close();
				outStr.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
