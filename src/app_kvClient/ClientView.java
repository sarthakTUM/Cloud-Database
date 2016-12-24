package app_kvClient;

public class ClientView {

	
	private final String PROMPT = "SERVER>>";
	private static final String LOG = "LOG:CLNTVIEW:";
	public void printResponse(){
		System.out.println(PROMPT + "response");
	}
	
	public void printResponse(String response){
		System.out.println(PROMPT + response);
	}
	
	public void printResponse(Response response){
		System.out.println(PROMPT + response.getResponseMessage());
	}
}
