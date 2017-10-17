package com.flytxt.jira.chart;

import java.util.Map;

import com.flytxt.jira.client.exception.ChartNotSupported;
/**
 * 
 * @author shijuj
 *
 */
public enum ChartType {
	PIE_CHART("P") {
		@Override
		public JiraChart getChart(Map<String, Object> chartParameters) {		
			return new JiraPieChart(chartParameters);
		}
	};
	
	private String key;
	
	private ChartType(String key){
		this.key = key ;
	}
	
	public abstract JiraChart getChart(Map<String, Object> chartParameters);
	
	public static ChartType getChartType(String key) throws ChartNotSupported{
		for(ChartType chartType : ChartType.values()){
			if(chartType.key.equals(key)){
				return chartType;
			}
		}
		throw new ChartNotSupported("Chart with type :"+ key +" is not supported ");
	}

}
