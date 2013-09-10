package peer;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import interfaces.PeerInterface;

public class Peer {	
	PeerInterface share;
	
	public Peer() {
		try {
			share = new PeerImpl();
			//register
			LocateRegistry.createRegistry(PeerInfo.rmi.port);
			//bind
			Naming.bind(PeerInfo.rmi.fileSharing, share);
			System.out.println("File sharing service start!");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Fail to create remote object!");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("URL error!");
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Service already bound!");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Initializing...");
		
		new Peer();
		
		PeerFunc peerFunction = new PeerFunc();
		peerFunction.intialize();
		
	}

}
