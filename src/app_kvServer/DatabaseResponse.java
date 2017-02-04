package app_kvServer;

import common.messages.KVMessage.StatusType;

import app_kvServer.DataStoreWrapper.DBCommand;
import app_kvServer.DataStoreWrapper.DBRequestResult;


public class DatabaseResponse {
 
	private DBCommand reuqestType;
	private DBRequestResult result;
	private String message;
	private String timestamp;
	private StatusType dbRequestStatus;
	private Byte[] misc;
	private String key;
	private String value;
	
	public DBCommand getReuqestType() {
		return reuqestType;
	}
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
	
	
}
