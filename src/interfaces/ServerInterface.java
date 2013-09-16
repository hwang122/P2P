package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface of IndexServer
 */
public interface ServerInterface extends Remote {
	
	public void registry(String peerid, String filename) throws RemoteException;
	
	public void unregister(String peerid, String filename) throws RemoteException;
	
	public ArrayList<String> search(String filename) throws RemoteException;

}
