package com.flytxt.jira.chart;

import java.awt.Font;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import com.flytxt.jira.client.exception.DrowingException;

/**
 * 
 * @author shijuj
 *
 */
public class JiraPieChart extends JiraBaseChart {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public JiraPieChart(Map<String, Object> chartParameters) {
		super(chartParameters);
	}

	/**
	 * 
	 * @param title
	 * @param dataset
	 * @param fileName
	 * @return
	 * @throws DrowingException
	 */
	private  String createChart(PieDataset dataset,
							String fileName) throws DrowingException {
		 
		JFreeChart chart = null;		
		if (!has3DEnabled()){
			chart = ChartFactory.createPieChart(super.getChartTitle(),dataset, true,false,true); 
		}else{
			chart = ChartFactory.createPieChart3D(super.getChartTitle(),dataset, true,false,true); 
		}
						 
		PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 7));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLegendLabelGenerator(new
        	    StandardPieSectionLabelGenerator("{0}:{1}"));
        plot.setLabelGap(0.02);        
        plot.setSimpleLabels(true);	
        
        LegendTitle legend = chart.getLegend();
        legend.setPosition(getLegendPostion());        
        chart.getTitle().setFont(new Font("Tahoma", Font.PLAIN, 12));       
    	return super.saveChartasImage(fileName,chart);
	 }
	 
	@Override
	public String getChartBasedOn(String basedOnKey, Map<String, Integer> chartData) throws DrowingException {
		PieDataset dataset = createChartDataset(chartData);
		File directory = new File(String.valueOf(super.getImageSavePath()));
		if (!directory.exists()) {
			directory.mkdir();
		}
		return createChart(dataset, super.getImageSavePath() + "/chart_" + basedOnKey);

	}
	 	 
	 
	 /**
	  * 
	  * @param result
	  * @return
	  */
	 private PieDataset createChartDataset(Map<String,Integer> result) {
	 	DefaultPieDataset dataset = new DefaultPieDataset();
	 	for(Entry<String, Integer> value : result.entrySet()){
	 		dataset.setValue(value.getKey(), value.getValue());
	 	}
        return dataset;        
	 }

}
