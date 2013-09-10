package indexServer;

import interfaces.ServerInterface;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
	ServerInterface service;
	
	public Server() {
		try{
			service = new ServerImpl();	
			//registry
			LocateRegistry.createRegistry(ServerInfo.rmi.port);
			//binding
			Naming.bind(ServerInfo.rmi.rmiAdd, service);
			System.out.println("Index Service start!");
		} 
		catch(RemoteException e) {
			e.printStackTrace();
			System.out.println("Fail to create remote object!");
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("URL error!");
		} 
		catch (AlreadyBoundException e) {
			e.printStackTrace();
			System.out.println("Service already bound!");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Server();

	}

}
