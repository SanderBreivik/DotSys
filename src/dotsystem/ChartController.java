package dotsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class ChartController {
 	@FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private static CategoryAxis xAxis;

    private static ObservableList<String> names = FXCollections.observableArrayList();
	    
	public void initialize() {
		for (Person p : DotSystemController.list) {
			names.add(p.getName());
		}
		xAxis.setCategories(names);
	}
	
	public void setDotData() {
		int[] dotCounter = new int[100];
		for (Person p : DotSystemController.list) {
			int dots = p.getDots();
			dotCounter[dots]++;
		}
		
		XYChart.Series<String, Integer> series = createDotDataSeries(dotCounter);
        barChart.getData().add(series);
	}


	private Series<String, Integer> createDotDataSeries(int[] dotCounter) {
		 XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();

	        for (int i = 0; i < dotCounter.length-1; i++) {
	            XYChart.Data<String, Integer> dotData = new XYChart.Data<String,Integer>(names.get(i), dotCounter[i]);
	            series.getData().add(dotData);
	        }

	        return series;
	}
}
