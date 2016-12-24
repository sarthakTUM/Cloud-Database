package app_kvClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import app_kvClient.Response.ResponseResult;
import app_kvClient.Response.ResponseSource;

public class ServerCommand extends CommandModel {
	
	private static final String LOG = "LOG:SERVRCMD:";
	public ServerCommand(){
		
	}
	public ServerCommand(String messageType, Handler handler, String[] commandAttributes, String commandInstruction){
		
		super(messageType, handler, commandAttributes, commandInstruction);
		
	}
	public Response checkValidity(){
		
		/**
		 * TODO add logic for checking validity of a Server command.
		 * Checks can include length, proper command, etc...
		 */
		Boolean valid = false;
		Response response = new Response(null, ResponseResult.FAIL, null);
		switch(this.getCommandInstruction()){
		case "CONNECT":
			// check if 3 attributes are there
			if(this.getCommandAttributes().length == 3){
				String serverAddressIPv4 = this.getCommandAttributes()[1];
				String serverPort = this.getCommandAttributes()[2];
				serverAddressIPv4 = serverAddressIPv4.trim();
				// check length
			    if ((serverAddressIPv4.length() < 6) & (serverAddressIPv4.length() > 15)){
			    	response.buildResponse(ResponseResult.FAIL, "Inappropriate length of IPv4 address", ResponseSource.CLIENT);
			    }	
			    else{
			    	try {
				    	// match the IPv4 pattern
				        Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
				        Matcher matcher = pattern.matcher(serverAddressIPv4);
				        valid =  matcher.matches() & (Integer.valueOf(serverPort) >= 0 && Integer.valueOf(serverPort) <= 65535);
				        if(valid){
				        	response.buildResponse(ResponseResult.SUCCESS, "Command Valid", ResponseSource.CLIENT);
				        }
				    } catch (PatternSyntaxException ex) {
				    	response.buildResponse(ResponseResult.FAIL, "Some exception occurred", ResponseSource.CLIENT);
				    } catch (Exception e){
				    	response.buildResponse(ResponseResult.FAIL, "Some exception occurred", ResponseSource.CLIENT);
				    }
			    } 
			}
			else{
				response.buildResponse(ResponseResult.FAIL, "Number of arguments not valid. \n it should be of the type: CONNECT <ADDRESS> <PORT>", ResponseSource.CLIENT);
			}
			break;
		case "PUT":
			
		}
		return response;
	}
}
