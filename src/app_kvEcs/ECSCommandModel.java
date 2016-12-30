/**
 * 
 */
package app_kvEcs;

import java.util.Arrays;
import java.util.Set;

import app_kvClient.Handler;

/**
 * @author Anant
 *
 */
public class ECSCommandModel {
	
	private String Instruction;
	private String[] parameters;
	private String messageType;
	private String[] permittedInstructions ={"start","stop","shutdown","add","remove"};
	private String[] permittedStrategies    ={"fifo","lifo"};
  
	public ECSCommandModel(String instruction) {
		super();
		String[] temp=instruction.split(" ");
		
		this.Instruction =(temp.length>0)?temp[0]:"";
		this.parameters[0] = (temp.length>1)?temp[1]:"";
		this.parameters[1] = (temp.length>2)?temp[2]:"";
		this.parameters[2] = (temp.length>3)?temp[3]:"";
	}
	public String getInstruction() {
		return Instruction;
	}
	public void setInstruction(String instruction) {
		Instruction = instruction;
	}
	public boolean isInstructionValid(String instruction) 
	{
		 return (Arrays.asList(permittedInstructions).contains(instruction));
			
	}
	
	public void isAttributeSetValid(String instruction) 
	{
		 //TODO to check the validity of the whole attributeset
			
	}
	public String[] getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	
	
}
