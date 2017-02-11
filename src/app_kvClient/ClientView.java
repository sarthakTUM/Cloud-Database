/**
 * @author Sarthak Gupta
 */

package app_kvClient;

public class ClientView {

	
	private final String PROMPT = "SERVER>>";
	private static final String LOG = "LOG:CLNTVIEW:";
	
	/**
	 * prints the response that it receives to the Client console.
	 */
	public void printResponse(){
		System.out.println(LOG + "printResponse() called");
		System.out.println(PROMPT + "response");
		
	}
	
	public void printResponse(String response){
		System.out.println(LOG + "printResponse() with <String> parameter called");
		System.out.println(PROMPT + response);
	}
	
	public void printResponse(Response response){
		System.out.println(LOG + "printResponse() with <Response> parameter called");
		System.out.println(PROMPT + response.getResponseMessage());
	}
}
