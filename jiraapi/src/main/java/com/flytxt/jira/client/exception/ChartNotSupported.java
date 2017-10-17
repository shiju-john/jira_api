package com.flytxt.jira.client.exception;
/**
 * 
 * @author shijuj
 *
 */
public class ChartNotSupported extends JiraException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	
	public ChartNotSupported(Throwable e){
		super(e);
	}
	
	public ChartNotSupported(String message){
		super(message);
	}
	
	public ChartNotSupported(Throwable e,String message){
		super(e,message);
	}
}