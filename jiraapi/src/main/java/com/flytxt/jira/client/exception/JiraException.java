package com.flytxt.jira.client.exception;
/**
 * 
 * @author shijuj
 *
 */
public class JiraException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	
	public JiraException(Throwable e){
		super(e);
	}
	
	public JiraException(String message){
		super(message);
	}
	
	public JiraException(Throwable e,String message){
		super(message,e);
	}

}
