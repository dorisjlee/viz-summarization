package evaluation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import algorithms.BreadthFirstPicking;
import algorithms.Experiment;
import algorithms.ProbablisticPicking;
import algorithms.ProbablisticPickingTest;
import algorithms.Traversal;
import distance.Distance;
import distance.Euclidean;

public class ProbabalisticTest {
	public static void main (String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
	   int numIterations = 50;
	   String aggFunc="SUM";
	   Experiment.experiment_name="../ipynb/dashboards/json/AlgoQualityPerformance";
	   Distance dist = new Euclidean();
	   // Baseline experiment
	   PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
	   writer.println("xAxis,yAxis,algo,groupby,total_time,total_utility");
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
	   Traversal algo = new ProbablisticPickingTest();
	   //Traversal algo = new BreadthFirstPicking();
	   Experiment exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", 10, algo, dist,0,0.8,false);
	   long duration = exp.timedRunOutput(exp);
	}
}
