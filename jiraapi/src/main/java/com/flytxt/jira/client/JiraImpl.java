package com.flytxt.jira.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.flytxt.jira.chart.ChartType;
import com.flytxt.jira.chart.JiraBaseChart;
import com.flytxt.jira.chart.JiraChart;
import com.flytxt.jira.client.exception.JiraException;
/**
 * 
 * @author shijuj
 *
 */
public class JiraImpl implements JiraApi{

	private JiraClient jiraClient = null;
	private static volatile JiraApi jiraApi = null;
	
	/**
	 * 
	 * @param bASE_URL
	 * @param userId
	 * @param userSecret
	 */
	private JiraImpl(String bASE_URL, String userId, String userSecret) {
		jiraClient = JiraClient.getInstance(bASE_URL, userId, userSecret);
	}
	
	
	/**
	 * 
	 * @param bASE_URL
	 * @param oauthToken
	 * @param consumerKey
	 * @param signatureMethod
	 * @param privatekey
	 */
	private JiraImpl(String bASE_URL, String oauthToken,String consumerKey,String signatureMethod,String privatekey) {
		jiraClient = JiraClient.getInstance(bASE_URL,oauthToken,consumerKey,signatureMethod,privatekey);
	}


	@Override
	public String search(Map<String, String> searchParameters, 
					String orderBy, int maxResult) throws JiraException{
		return jiraClient.search(searchParameters, orderBy, 0,maxResult,null);
	}

	/**	
	 * 
	 * @param BASE_URL
	 * @param userId
	 * @param userSecret
	 * @return
	 */
	public static JiraApi getInstance(String BASE_URL, String userId, String userSecret){
		if(null==jiraApi){
			synchronized (JiraImpl.class) {
				if(null== jiraApi){
					jiraApi = new JiraImpl(BASE_URL, userId, userSecret);
				}				
			}
		}
		return jiraApi;
	}
	
	/**
	 * 
	 * @param BASE_URL
	 * @param oauthToken
	 * @param consumerKey
	 * @param signatureMethod
	 * @param privatekey
	 * @return
	 */
	public static JiraApi getInstance(String BASE_URL, String oauthToken,
								String consumerKey,String signatureMethod,String privatekey){
		if(null==jiraApi){
			synchronized (JiraImpl.class) {
				if(null== jiraApi){
					jiraApi = new JiraImpl(BASE_URL,oauthToken,consumerKey,signatureMethod,privatekey);
				}				
			}
		}
		return jiraApi;
	}


	@Override
	public String getChart(String chartType,String basedOnKey,Map<String, String> searchParameters,
								int maxResult , Map<String,Object> chartParameters) throws JiraException {		
		ChartType type = ChartType.getChartType(chartType);
		JiraChart jiraChart = type.getChart(chartParameters);
		return jiraChart.getChartBasedOn(basedOnKey, getChartData(basedOnKey,searchParameters, null, maxResult) );				
	}

	/**
	 * 
	 * @param basedOnKey
	 * @param searchParameters
	 * @param orderby
	 * @param maxResult
	 * @return
	 * @throws JiraException
	 */
	private Map<String, Integer> getChartData(String basedOnKey, Map<String, String> searchParameters, String orderby,
			int maxResult) throws JiraException {
		try {
			String fields[] = {"id","key",basedOnKey};
			Map<String,Integer> chartData =  new HashMap<>();
			boolean hasNext = false;
			int startAt=0;
			do {
				String jsonArray = jiraClient.search(searchParameters, null, startAt,maxResult,fields);				
				chartData = JiraBaseChart.filterJsonArrayBasedOnKey(jsonArray, basedOnKey, chartData);
				Object[] result = JiraBaseChart.hasPaginated(jsonArray);
				if(null==result)
					throw new JiraException("Search Data Not found/Unable to parse the result data : "+jsonArray);
				hasNext = (boolean)result[0];
				startAt= (int)result[1];
			}while(hasNext);
			return chartData;
		} catch (IOException  | JSONException e) {
			throw new JiraException(e, e.getMessage());
		} 
	}

}
