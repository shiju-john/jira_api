package com.flytxt.jira.client;

import java.util.Map;

import com.flytxt.jira.client.exception.JiraException;
/**
 * 
 * @author fly
 *
 */
public interface JiraApi {
	
	/**
	 * 
	 * @param searchParameters
	 * @param orderBy
	 * @param maxResult
	 * @return
	 */
	public String search(Map<String,String> searchParameters,
							String orderBy,int maxResult) throws JiraException;
		
	/**
	 * 
	 * @param chartType    	: P for pie chart,
	 * @param basedOnKey	: priority for priority chart, status for status based chart
	 * @param searchParameters 
	 * @param maxResult
	 * @param chartParameters
	 * @param chartTitle  Title of the chart 
	 * @return
	 * @throws JiraException
	 */
	
	
	public String getChart(String chartType,String basedOnKey, 
			Map<String,String> searchParameters,int maxResult, 
			Map<String,Object> chartParameters) throws JiraException;
	
	

}
