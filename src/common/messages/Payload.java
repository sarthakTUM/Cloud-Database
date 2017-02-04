package common.messages;

import app_kvServer.DatabaseResponse;

public class Payload implements KVMessage{

	private static final String LOG = "LOG:PAYLOAD$:";
	private String requestType;
	private String key;
	private String value;
	private StatusType statusType;
	private MessageSource source;
	
	
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

}
