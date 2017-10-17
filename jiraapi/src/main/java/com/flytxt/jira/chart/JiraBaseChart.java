package com.flytxt.jira.chart;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.ui.RectangleEdge;
import org.json.JSONException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flytxt.jira.client.exception.DrowingException;
/**
 * 
 * @author shijuj
 *
 */
public abstract class JiraBaseChart implements JiraChart {

	private Map<String, Object> chartParameters;	
	
	private static final String CHART_TITLE = "CHART_TITLE";
	private static final String CHART_WIDTH ="CHART_WIDTH";
	private static final String CHART_HEIGHT = "CHART_HEIGHT";
	private static final String CHART_IMAGE_PATH = "CHART_IMAGE_PATH";
	private static final String CHART_3D_ENABLED = "CHART_3D_ENABLED";
	private static final String CHART_IMAGE_EXTENSION = "CHART_IMAGE_EXTENSION";
	private static final String CHART_LEGEND_POSITION = "CHART_LEGEND_POSITION";

	/**
	 * 
	 * @param chartParameters
	 */
	public JiraBaseChart(Map<String, Object> chartParameters) {
		if(null!=chartParameters)
			this.chartParameters = chartParameters;
		else{
			this.chartParameters  = new HashMap<>();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected int getChartHeight(){
		return chartParameters.containsKey(CHART_HEIGHT) ? 
				(int)chartParameters.get(CHART_HEIGHT) : 400;
	}
	
	/**
	 * 
	 * @return
	 */	
	protected boolean  has3DEnabled(){
		return chartParameters.containsKey(CHART_3D_ENABLED) ? 
				(boolean)chartParameters.get(CHART_3D_ENABLED) : false;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getChartTitle(){
		return chartParameters.containsKey(CHART_TITLE) ? 
				(String)chartParameters.get(CHART_TITLE) :"Chart Title";
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getImageSavePath(){
		return chartParameters.containsKey(CHART_IMAGE_PATH) ? 
				(String)chartParameters.get(CHART_IMAGE_PATH) :"images";
	}
	
	
	/**
	 * 
	 * @return
	 */
	protected int getChartWidth(){
		return chartParameters.containsKey(CHART_WIDTH) ? 
				(int)chartParameters.get(CHART_WIDTH) :400;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getImageExtenstion() {
		return chartParameters.containsKey(CHART_IMAGE_EXTENSION) ? 
				(String)chartParameters.get(CHART_IMAGE_EXTENSION) :"PNG";
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Map<String,Integer> filterJsonArrayBasedOnKey(String jsonArray,String chartKey,
										Map<String,Integer> result ) throws IOException, JSONException {
		 final ObjectMapper mapper = new ObjectMapper();
		 jsonArray = "["+jsonArray+"]";
		 @SuppressWarnings("rawtypes")
		 HashMap[] nodes =  mapper.readValue(jsonArray,HashMap[].class) ; 
//		 Map<String,Integer> result =  new HashMap<>();
		    for (@SuppressWarnings("rawtypes") Map node : nodes) {
		    	@SuppressWarnings({ "unchecked", "rawtypes" })
				List<Map> issues =(List<Map>) node.get("issues");
		    	for(@SuppressWarnings("rawtypes") Map issue: issues){		    	    
//		    		System.out.println(issue.keySet());
		    		@SuppressWarnings("rawtypes")		    	     
					String value = (String)((Map)((Map)issue.get("fields")).get(chartKey)).get("name");
		    	     if(!result.containsKey(value)){
		    	    	 result.put(value, 0);
		    	     }
		    	     result.put(value, result.get(value)+1);
		    	}		       
		    }
		    return result;
		}
	
	/**
	 * 
	 * @param jsonArray
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static Object[] hasPaginated(String jsonArray) throws IOException, JSONException {
		
		final ObjectMapper mapper = new ObjectMapper();
		jsonArray = "["+jsonArray+"]";
		@SuppressWarnings("rawtypes")
		HashMap[] nodes =  mapper.readValue(jsonArray,HashMap[].class) ; 
		//Map<String,Integer> result =  new HashMap<>();
		for (@SuppressWarnings("rawtypes") Map node : nodes) {
			System.out.println(node.keySet());
			int totalRecord = (int)node.get("total");
			int maxResults = (int)node.get("maxResults"); 
			int startPosition = (int)node.get("startAt");
			if(totalRecord>startPosition +maxResults){
				Object []result =  {true,startPosition +maxResults};
				return result;
			}else{
				Object []result =  {false,startPosition +maxResults};
				return result;
			}
		}
		return null;			
	}
	
	/**
	 * 
	 * @return
	 */
	public RectangleEdge getLegendPostion() {
		String legendPosition = (String)chartParameters.get(CHART_LEGEND_POSITION);
		switch (legendPosition) {
			case "LEFT":
				return RectangleEdge.LEFT;
			case "RIGHT" :
				return RectangleEdge.RIGHT;
			case "TOP" :
				return RectangleEdge.TOP;			
			default:
				return RectangleEdge.BOTTOM;
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 * @param chart
	 * @return
	 * @throws DrowingException
	 */
	public String saveChartasImage(String fileName, JFreeChart chart) throws DrowingException {
		try {
    		File file = new File(fileName+"."+getImageExtenstion());    		
    		if("SVG".equalsIgnoreCase(getImageExtenstion())){
    			SVGGraphics2D g2 = new SVGGraphics2D(getChartWidth(), getChartHeight());
    	        Rectangle r = new Rectangle(0, 0, getChartHeight(),getChartHeight());
    	        chart.draw(g2, r);    	           	      
    			SVGUtils.writeToSVG(file, g2.getSVGElement());     			    							
    		}else{
    			ChartUtilities.saveChartAsPNG(file,chart, getChartWidth(), getChartHeight()); 			
    		}
    		return file.getAbsolutePath();    	
		} catch (IOException e) {			
			throw new DrowingException(e,"Exception while saving the image");
		}	
	}

}
