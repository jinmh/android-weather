package com.demo.wanpeng.base.http;

public class Response {

	public boolean error;
	private int errorType;
	private String errorMessage;
	private String result;
	
	public boolean isError() {
		return error;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public int getErrorType() {
		return errorType;
	}
	
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
