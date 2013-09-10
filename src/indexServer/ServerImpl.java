package indexServer;

import interfaces.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = 1L;
	/**
	 * In this case, I use an arraylist instead of a database,
	 * since I only need to develop a simple p2p system
	 */
	private ArrayList<DataMgt> list = new ArrayList<DataMgt>();

	protected ServerImpl() throws RemoteException {
	}

	@Override
	public void registry(String peerid, String filename) throws RemoteException {
		/**
		 * add a new object into the arraylist
		 * cause there maybe more than one client to register
		 * thread is used
		 */
		RegisterRun run = new RegisterRun(peerid, filename);
		Thread thread = new Thread(run);
		thread.start();
		thread = null;
	}

	@Override
	public ArrayList<String> search(String filename) throws RemoteException {
		//find the exact name of a file and return the list of peerid
		ArrayList<String> peerIDList = null;
		//create a thread pool
		ExecutorService execPool = Executors.newCachedThreadPool();
		
		/**
		 * Cause in this case, we need an return value of integer array,
		 * instead of using Runable, we use Callable.
		 * After submit the call, we shall get a future object, and 
		 * the future object can be an integer array.
		 */
		try {
			Callable<ArrayList<String>> call = new SearchCall(filename);
			Future<ArrayList<String>> result = execPool.submit(call);
			
			peerIDList = new ArrayList<String>(result.get());
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			execPool.shutdown();
		}
		
		return peerIDList;
	}
	
	/**
	 * Anonymous Inner class implements Runnable interface
	 * used to solve multiple client register
	 */
	class RegisterRun implements Runnable{
		private String peerid;
		private String filename;
		
		/**
		 * @param peerid
		 * @param filename
		 * use constructor to pass param to the class
		 */
		public RegisterRun(String peerid, String filename) {
			setPeerid(peerid);
			setFilename(filename);
		}
		
		/**
		 * override method, add data to arraylist
		 */
		@Override
		public void run() {
			String id = getPeerid();
			String name = getFilename();
			list.add(new DataMgt(id, name));
			System.out.println("From " + id + ", get file " + name);
		}
		
		/**
		 * getters and setters
		 * generate automatically
		 */
		public String getPeerid() {
			return peerid;
		}

		public void setPeerid(String peerid) {
			this.peerid = peerid;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
	
	/**
	 * Anonymous Inner class implements Callable interface
	 * used to solve multiple client search
	 */
	class SearchCall implements Callable<ArrayList<String>>{
		
		private ArrayList<String> peerIDList = new ArrayList<String>();
		private String filename;
		
		/**
		 * @param filename
		 * use constructor to pass param filename
		 */
		public SearchCall(String filename) {
			// TODO Auto-generated constructor stub
			setFilename(filename);
		}
		
		
		/**
		 * get exact the filename from list
		 * and return the array of peer ID
		 */
		@Override
		public ArrayList<String> call() throws Exception {
			for(int i = 0; i < list.size(); i++){
				//find the exact name of file
				if(list.get(i).get_filename().equals(getFilename())){
					//add peer ID to the list
					setPeerIDList(list.get(i).get_peerid());
				}
			}
			return getPeerIDList();
		}
		
		/**
		 * getters and setters
		 * automatically generated
		 */
		public ArrayList<String> getPeerIDList() {
			return peerIDList;
		}
		
		public void setPeerIDList(String ID) {
			this.peerIDList.add(ID);
		}
		
		public String getFilename() {
			return filename;
		}
		
		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
}
