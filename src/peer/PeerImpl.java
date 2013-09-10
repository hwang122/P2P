package peer;

import interfaces.PeerInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PeerImpl extends UnicastRemoteObject implements PeerInterface {

	/**
	 * Implementation of File sharing within peers
	 */
	private static final long serialVersionUID = 2L;

	protected PeerImpl() throws RemoteException {
	}

	@Override
	public byte[] share(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		String filePath = PeerInfo.local.path + filename;
		
		return fileToByte(filePath);
	}
	
	public byte[] fileToByte(String filename){
		byte[] byteFile = null;
		FileInputStream inputStream = null;
		BufferedInputStream bufferStream = null;
		try{
			File file = new File(filename);
			byteFile = new byte[(int)file.length()];
			inputStream = new FileInputStream(file);
			bufferStream = new BufferedInputStream(inputStream);
			bufferStream.read(byteFile);
			
			bufferStream.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				bufferStream.close();
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		return byteFile;
	}
}
