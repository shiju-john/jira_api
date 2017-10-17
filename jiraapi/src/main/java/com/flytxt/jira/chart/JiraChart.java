package com.flytxt.jira.chart;

import java.io.Serializable;
import java.util.Map;

import com.flytxt.jira.client.exception.JiraException;
/**
 * 
 * @author shijuj
 *
 */
public interface JiraChart  extends Serializable {
	
	/**
	 * 
	 * @param basedOnKey
	 * @param chartData
	 * @return
	 * @throws JiraException
	 */
	public String getChartBasedOn(String basedOnKey,Map<String, Integer> chartData) throws JiraException;
	
			
}
