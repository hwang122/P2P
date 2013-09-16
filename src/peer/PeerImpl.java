package peer;

import interfaces.PeerInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implementation of File sharing within peers
 */
public class PeerImpl extends UnicastRemoteObject implements PeerInterface {

	private static final long serialVersionUID = -120810884040066791L;

	protected PeerImpl() throws RemoteException {
	}
	
	/**
	 * obtain(String) method
	 * Since RMI don't support FileInputStream, I convert the file into
	 * byte[] array and return this array to client
	 * And to make thread safe while there are multiple clients using obtain
	 * method, I create thread pool and using Callable to improve the performance
	 */
	@Override
	public byte[] obtain(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		String filePath = PeerInfo.local.path + "/" + filename;
		byte[] byteFile = null;
		//create a thread pool
		ExecutorService execPool = Executors.newCachedThreadPool();
		
		Callable<byte[]> call = new ShareCall(filePath);
		Future<byte[]> result = execPool.submit(call);
		try {
			int length = result.get().length;
			byteFile = new byte[length];
			System.arraycopy(result.get(), 0, byteFile, 0, length);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			execPool.shutdown();
		}
		
		return byteFile;
	}
	
	/**
	 * implements Callabe interface
	 * return byte[]
	 */
	class ShareCall implements Callable<byte[]>{
		private String filename = null;
		
		public ShareCall(String filename) {
			setFilename(filename);
		}

		@Override
		public byte[] call() throws Exception {
			//use fileToByte method to convert file to byte[]
			return fileToByte(getFilename());
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
		
	}
	
	/**
	 * Key method, used to convert file to byte[]
	 * Since RMI don't support FileInputStream, this is one way
	 * to transfer the file
	 * @param filename
	 * @return
	 */
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
