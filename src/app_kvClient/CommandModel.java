package app_kvClient;

public class CommandModel {

	private String commandInstruction;
	private String[] commandAttributes;
	private Handler handler;
	private String messageType;
	
	public CommandModel(){
		
	}
	
	public CommandModel(String messageType, Handler handler, String[] commandAttributes, String commandInstruction){
		this.setMessageType(messageType);
		this.setHandler(handler);
		this.setCommandInstruction(commandInstruction);
		this.setCommandAttributes(commandAttributes);
	}

	public Response checkValidity(){
		
		return null;
	}
	
	
	// TODO generate getters and setters
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
}
