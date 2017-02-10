package common.messages;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import app_kvServer.DatabaseResponse;

public class Payload implements KVMessage, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String LOG = "LOG:PAYLOAD$:";
	private String requestType;
	private String key;
	private String value;
	private StatusType statusType;
	private MessageSource source;
	private List<Long> serverChecksumTable;
	private List<Map.Entry<Integer, Long>> instructionStream;
	
	
	public Payload(){
		
	}
	public Payload(String key, String value, String requestType){
		System.out.println(LOG + "creating payload in constructor with <K,V,T>" + key + ":" + value + ":" + requestType);
		this.key = key;
		this.value = value;
		this.requestType = requestType;
	}
	
	public Payload(byte[] messageBytes){
		/*
		 * TODO construct a payload with the received bytes.
		 */
		System.out.println(LOG + "message bytes received: " + messageBytes.length);
		String stringifyReceivedBytes = "";
		
		
		for(byte b : messageBytes){
			stringifyReceivedBytes += (char)b;
		}
		if(stringifyReceivedBytes != null){
			System.out.println(LOG + "stringify received message : " + stringifyReceivedBytes);
			String[] tokens = stringifyReceivedBytes.split("%");
			System.out.println(LOG + "payload tokens: " + tokens + " request type: " + tokens[1]);
			switch(tokens[1]){
			case "PUT":
				this.source = MessageSource.valueOf(tokens[0]);
				this.requestType = tokens[1];
				this.key = tokens[2];
				this.value = tokens[3];
				this.statusType = StatusType.valueOf(tokens[4]);
				break;
			case "GET":
				this.source = MessageSource.valueOf(tokens[0]);
				this.requestType = tokens[1];
				this.key = tokens[2];
				this.value = tokens[3];
				this.statusType = StatusType.valueOf(tokens[4]);
				break;
			case "start":
				this.source = MessageSource.valueOf(tokens[0]);
				this.requestType = tokens[1];
				break;
			case "META":
				this.source = MessageSource.valueOf(tokens[0]);
				this.requestType = tokens[1];
				break;
			case "SYNC":
				this.source = MessageSource.valueOf(tokens[0]);
				this.requestType = tokens[1];
				this.key = tokens[2];
				break;
				default:
					System.out.println(LOG + "unidentified request type received");
					break;
				
			}
		
		}
	}
	
	public Payload(DatabaseResponse response){
		this.requestType = response.getReuqestType().toString();
		this.statusType = response.getStatus();
		this.source = MessageSource.SERVER;
		this.key = response.getKey();
		this.value = response.getValue();
		this.serverChecksumTable = response.getServerChecksumTable();
		/*
		 * TODO set source to the server_name.
		 */
	}
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public StatusType getStatus() {
		// TODO Auto-generated method stub
		return this.statusType;
	}
	
	public void setKey(String key){
		
	}
	
	public void setValue(String value){
		
	}
	
	public void setRequestType(){
		
	}
	public String getRequestType(){
		return this.requestType;
	}
	
	public void setStatusType(StatusType statusType){
		this.statusType = statusType;
	}
	public void setSource(MessageSource source){
		this.source = source;
	}
	@Override
	public MessageSource getMessageSource() {
		// TODO Auto-generated method stub
		return this.source;
	}
	public List<Long> getServerChecksumTable() {
		return serverChecksumTable;
	}
	public void setServerChecksumTable(List<Long> serverChecksumTable) {
		this.serverChecksumTable = serverChecksumTable;
	}
	public List<Map.Entry<Integer, Long>> getInstructionStream() {
		return instructionStream;
	}
	public void setInstructionStream(
			List<Map.Entry<Integer, Long>> instructionStream) {
		this.instructionStream = instructionStream;
	}

}
