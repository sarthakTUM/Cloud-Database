package common.messages;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String LOG = "LOG:MESSAGE$:";
	private static final String separator = " ";
	private static final String ctrSeparator = "%";
	private static final char ctrEOM = '\r';
	private Payload payload;
	private String stringifyPayload;
	
	public Message(){
		
	}
	public Message(Payload payload){
		System.out.println(LOG + "creating message request with the payload <K,V,T>" + payload.getKey() + ":" + payload.getValue() + ":" + payload.getRequestType());
		this.payload = payload;
		stringifyPayload = toString(this.payload);
		System.out.println(LOG + "stringifyPayload: " + stringifyPayload);
		addCtrChars(stringifyPayload);
		System.out.println(LOG + "stringifyPayload after adding ctrChars: " + stringifyPayload);
	}
	
	private String toString(Payload payload){
		
		String requestType = payload.getRequestType();
		String key = payload.getKey();
		String value = payload.getValue();
		String messageSource = payload.getMessageSource().toString();
		String statusType = payload.getStatus().toString();
		
		// TODO stringify payload.
		String stringifyPayload = messageSource + separator + requestType + separator + key + separator + value + separator + statusType;
		return stringifyPayload;
	}
	
	private void addCtrChars(String stringifyPayload){
		// TODO the value might also contain spaces.
		this.stringifyPayload = stringifyPayload.replace(separator, ctrSeparator);
		//this.stringifyPayload.concat(ctrEOM);
	}
	
	public byte[] serializeMessage(){
		byte[] requestBytes = this.stringifyPayload.getBytes();
		/*for(byte b:requestBytes){
			System.out.println(b);
		}*/
		return requestBytes;
	}
	
	public byte[] serializeExtensionMessage(){
		byte[] requestBytes = this.stringifyPayload.getBytes();
		/*for(byte b:requestBytes){
			System.out.println(b);
		}*/
		return requestBytes;
	}
	

}
