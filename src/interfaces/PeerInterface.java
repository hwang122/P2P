package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote {
	
	public byte[] share(String filename) throws RemoteException;
}
