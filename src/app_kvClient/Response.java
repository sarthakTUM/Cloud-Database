package app_kvClient;

public class Response {

	public enum ResponseSource{
		CLIENT, SERVER, ADMIN
	}
	public enum ResponseResult{
		SUCCESS, FAIL, WARNING
	}
	
	
	private String responseMessage;
	private ResponseSource responseSource;
	private ResponseResult responseResult;
	
	
	public Response(ResponseSource responseSource, ResponseResult responseResult, String responseMessage){
		this.responseMessage = responseMessage;
		this.responseResult = responseResult;
		this.responseSource = responseSource;
	}
	
	public String getResponseMessage(){
		return this.responseMessage;
	}
	public void setResponseMessage(String responseMessage){
		this.responseMessage = responseMessage;
	}
	public ResponseSource getResponseSource(){
		return this.responseSource;
	}
	public void setResponseSource(ResponseSource responseSource){
		this.responseSource = responseSource;
	}
	public ResponseResult getResponseResult(){
		return this.responseResult;
	}
	public void setResponseResult(ResponseResult responseResult){
		this.responseResult = responseResult;
	}
	
	public void buildResponse(ResponseResult responseResult, String responseMessage, ResponseSource responseSource){
		this.responseMessage = responseMessage;
		this.responseResult = responseResult;
		this.responseSource = responseSource;
	}
}
