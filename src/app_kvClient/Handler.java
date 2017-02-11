/**
 * @author Sarthak Gupta
 */

package app_kvClient;

public interface Handler {

	/**
	 * processes the request by routing it to server or client as 
	 * appropriate by respective handlers.
	 * @param command: CommandModel pertaining to relevant command
	 * @return result of the command.
	 */
	public Response processCommand(CommandModel command);
	
}
