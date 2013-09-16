package peer;

import interfaces.PeerInterface;
import interfaces.ServerInterface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
			//get service from server
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
			//store local machines IP and name
			InetAddress addr = InetAddress.getLocalHost();
			PeerInfo.local.IP = addr.getHostAddress();
			PeerInfo.local.name = addr.getHostName();
			PeerInfo.local.ID = PeerInfo.local.IP + ":" + PeerInfo.rmi.port;
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
	public void register(String ID, String filename){
		FileWriter writer = null;
		try {
			writer = new FileWriter("./log.txt", true);
			//using index server's service to register file
			indexService.registry(ID, filename);
			//add file to local file list
			PeerInfo.local.fileList.add(filename);
			//write the action to log file
			System.out.println("File " + filename + " is registered!");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			writer.write(time + "\t\tFile " + filename 
					+ " is registered on the index server!\r\n");
			writer.close();
		} 
		catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * remove related file information from index server
	 * @param ID
	 * @param filename
	 */
	public void unregister(String ID, String filename){
		FileWriter writer = null;
		try{
			writer = new FileWriter("./log.txt", true);
			//using index server's service to unregister file
			indexService.unregister(ID, filename);
			//write the action to log file
			System.out.println("File " + filename + " is unregistered!");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			writer.write(time + "\t\tFile " + filename 
					+ " is unregistered on the index server!\r\n");
			writer.close();
		}
		catch(RemoteException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get search service from server and Get List
	 */
	@SuppressWarnings("unchecked")
	public boolean search(String filename){
		boolean found = false;
		FileWriter writer = null;
		try{
			writer = new FileWriter("./log.txt", true);
			//Get arrrylist of peer ip from server and clone it to local arraylist
			PeerInfo.dest.destList = (ArrayList<String>) indexService.search(filename).clone();
			
			if(PeerInfo.dest.destList.size() != 0){
				PeerInfo.dest.destination = PeerInfo.dest.destList.get(0);
				System.out.println("File " + filename + " is found!");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = df.format(new Date());
				writer.write(time + "\t\tRequesting File " + filename 
						+ " is found!\r\n");
				writer.close();
				//change the destination's rmi address
				PeerInfo.dest.destRmi = "rmi://" + 
						PeerInfo.dest.destination + "/share";
				found = true;
			}
			else{
				System.out.println("File " + filename + " is not found!");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = df.format(new Date());
				writer.write(time + "\t\tRequesting File " + filename 
						+ " is not found!\r\n");
				writer.close();
			}
		}
		catch(RemoteException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}
	
	/**
	 * Get the file from server(client)
	 * using service obtain(String) from server
	 */
	public void download(String filename){
		FileWriter writer = null;
		try {
			writer = new FileWriter("./log.txt", true);
			fileService = (PeerInterface)Naming.lookup(PeerInfo.dest.destRmi);
			//call the method writeFile to write file
			writeFile(filename, fileService.obtain(filename));
			//write action to log file
			String downloadPath = PeerInfo.local.path.replaceAll("\\\\\\\\", "\\\\");
			downloadPath += (File.separator + filename);
			System.out.println("File has been downloaded at " + downloadPath);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			writer.write(time + "\t\tFile " + filename 
					+ " is downloaded at " + downloadPath + "!\r\n");
			writer.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Since RMI don't support FileInputStream, I convert the file into byte[],
	 * thus the file must be converted from byte[] to original file
	 * @param filename
	 * @param fileContent
	 */
	public void writeFile(String filename, byte[] fileContent){
		FileOutputStream outStr = null;
		BufferedOutputStream buffer = null;
		try{
			outStr = new FileOutputStream(new File(PeerInfo.local.path + "/" + filename));
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
