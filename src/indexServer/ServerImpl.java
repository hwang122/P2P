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

	private static final long serialVersionUID = -1967438933815933170L;
	/**
	 * In this case, I use an arraylist instead of a database,
	 * since I only need to develop a simple p2p system
	 */
	private ArrayList<DataMgt> list = new ArrayList<DataMgt>();

	protected ServerImpl() throws RemoteException {
	}

	/**
	 * add a new object into the arraylist
	 * thread is used to
	 * improve the performance
	 */
	@Override
	public void registry(String peerid, String filename) throws RemoteException {
		RegisterRun run = new RegisterRun(peerid, filename);
		Thread thread = new Thread(run);
		thread.start();
		thread = null;
	}
	
	/**
	 * remove an object from the arraylist
	 * thread is used to 
	 * improve the performance
	 */
	@Override
	public void unregister(String peerid, String filename) throws RemoteException {
		UnRegisterRun run = new UnRegisterRun(peerid, filename);
		Thread thread = new Thread(run);
		thread.start();
		thread = null;
	}
	
	/**
	 * return an ArrayList to client,
	 * thread pool and callable is used to
	 * improve the performance
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> search(String filename) throws RemoteException {
		//find the exact name of a file and return the list of peerid
		ArrayList<String> peerIDList = new ArrayList<String>();
		//create a thread pool
		ExecutorService execPool = Executors.newCachedThreadPool();
		/**
		 * Cause in this case, we need an return value of integer array,
		 * instead of using Runable, we use Callable.
		 * After submit the call, we shall get a future object, and 
		 * the future object can be an integer array.
		 */
		Callable<ArrayList<String>> call = new SearchCall(filename);
		Future<ArrayList<String>> result = execPool.submit(call);
		
		try {
			if(result.get().size() != 0)
				peerIDList = (ArrayList<String>) result.get().clone();
			
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
			boolean exist = false;
			
			if(list.size() == 0){
				list.add(new DataMgt(id, name));
				System.out.println("From " + id + ", get file " + name);
			}
			else{
				for(int i = 0; i < list.size(); i++){
					if(list.get(i).get_peerid().equals(id)
					&& list.get(i).get_filename().equals(name)){
						exist = true;
						break;
					}
				}
				
				if(!exist){
					list.add(new DataMgt(id, name));
					System.out.println("From " + id + ", get file " + name);
				}
			}
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
	 * Anonymous Inner class implements Runnable interface
	 */
	class UnRegisterRun implements Runnable{
		private String peerid;
		private String filename;
		
		/**
		 * constructor, pass param to the class
		 * @param peerid
		 * @param filename
		 */
		public UnRegisterRun(String peerid, String filename) {
			setPeerid(peerid);
			setFilename(filename);
		}

		/**
		 * remove data from list
		 */
		@Override
		public void run() {
			String id = getPeerid();
			String name = getFilename();
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).get_peerid().equals(id)
				&& list.get(i).get_filename().equals(name)){
					list.remove(i);
					break;
				}
			}
			System.out.println("From " + id + ", remove file " + name);
		}

		/**
		 * getters and setters
		 * @return
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
	 */
	class SearchCall implements Callable<ArrayList<String>>{
		
		private ArrayList<String> peerIDList = new ArrayList<String>();
		private String filename = null;
		
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
