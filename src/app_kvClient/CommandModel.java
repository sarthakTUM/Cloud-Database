/**
 * @author Sarthak Gupta
 * Represents the Command.
 */

package app_kvClient;

public class CommandModel {

	private String commandInstruction;
	private String[] commandAttributes;
	private Handler handler;
	private String messageType;
	private String messageSource;
	
	public CommandModel(){
		
	}
	
	/**
	 * 
	 * @param messageType: Client or Server
	 * @param handler: Relevant Handler for the Command Type
	 * @param commandAttributes: Attributes for the command (Keym Value, etc)
	 * @param commandInstruction: GET, PUT, SYNC, etc.
	 * @param messageSource: from where to message is originating.
	 */
	public CommandModel(String messageType, Handler handler, String[] commandAttributes, String commandInstruction, String messageSource){
		this.setMessageType(messageType);
		this.setHandler(handler);
		this.setCommandInstruction(commandInstruction);
		this.setCommandAttributes(commandAttributes);
		this.setMessageSource(messageSource);
	}

	/**
	 * checks the validity of the command
	 * @return whether the command is valid or not.
	 */
	public Response checkValidity(){
		
		return null;
	}
	
	/*
	 * Getters and Setters.
	 */
	public void setMessageType(String messageType){
		
		this.messageType = messageType;
	}
	public String getMessageType(){
		return this.messageType;
	}
	public String getCommandInstruction() {
		return commandInstruction;
	}
	public void setCommandInstruction(String commandInstruction) {
		this.commandInstruction = commandInstruction;
	}
	public String[] getCommandAttributes() {
		return commandAttributes;
	}
	public void setCommandAttributes(String[] commandAttributes) {
		this.commandAttributes = commandAttributes;
	}
	public Handler getHandler() {
		return this.handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public void setMessageSource(String messageSource){
		this.messageSource = messageSource;
	}
	public String getMessageSource(){
		return this.messageSource;
	}
}
