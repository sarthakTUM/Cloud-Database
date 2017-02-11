/**
 * @author Sarthak Gupta
 * Models the response received after processing of request by the Server.
 */

package app_kvServer;

import java.io.Serializable;
import java.util.List;

import app_kvServer.DataStoreWrapper.DBCommand;
import app_kvServer.DataStoreWrapper.DBRequestResult;
import common.messages.KVMessage.StatusType;


public class DatabaseResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	private DBCommand reuqestType;
	private DBRequestResult result;
	private String message;
	private String timestamp;
	private StatusType dbRequestStatus;
	private Byte[] misc;
	private String key;
	private String value;
	private List<Long> serverChecksumTable;
	
	/**
	 * Gets the tye of request
	 * @return The command supported by the Database
	 */
	public DBCommand getReuqestType() {
		return reuqestType;
	}
	
	/**
	 * 
	 * @param reuqestType the command server suppports.
	 */
	public void setReuqestType(DBCommand reuqestType) {
		this.reuqestType = reuqestType;
	}
	public DBRequestResult getResult() {
		return result;
	}
	public void setResult(DBRequestResult result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public StatusType getStatus() {
		return this.dbRequestStatus;
	}
	public void setStatus(StatusType dbRequestStatus) {
		this.dbRequestStatus = dbRequestStatus;
	}
	public void setKey(String key){
		this.key = key;
	}
	public String getKey(){
		return this.key;
	}
	public void setValue(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}
	public List<Long> getServerChecksumTable() {
		return serverChecksumTable;
	}
	public void setServerChecksumTable(List<Long> serverChecksumTable) {
		this.serverChecksumTable = serverChecksumTable;
	}
	
	
}
