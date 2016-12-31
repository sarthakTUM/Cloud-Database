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
		
		for(int ctr	=0;ctr<temp.length;ctr++)
		{
			if(ctr==0) this.Instruction=temp[ctr];
			else this.parameters[ctr-1]=temp[ctr];
				
		}
		
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
