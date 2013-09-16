package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of Peer
 */
public interface PeerInterface extends Remote {
	
	public byte[] obtain(String filename) throws RemoteException;
}
