package com.flytxt.jira.client.exception;

/**
 * 
 * @author shijuj
 *
 */
public class DrowingException extends JiraException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	
	public DrowingException(Throwable e){
		super(e);
	}
	
	public DrowingException(String message){
		super(message);
	}
	
	public DrowingException(Throwable e,String message){
		super(e,message);
	}			
}