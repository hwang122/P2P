package indexServer;


/**
 * This class is used to store the peer id and file names.
 * The data is treated as an object
 */
public class DataMgt {
	private String _peerid;
	private String _filename;
	
	public DataMgt(String id, String filename) {
		// TODO Auto-generated constructor stub
		set_peerid(id);
		set_filename(filename);
	}

	public String get_peerid() {
		return _peerid;
	}

	public void set_peerid(String _peerid) {
		this._peerid = _peerid;
	}

	public String get_filename() {
		return _filename;
	}

	public void set_filename(String _filename) {
		this._filename = _filename;
	}
	
	
}
