package evaluation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import algorithms.BaselineBFS;
import algorithms.BaselineKmeans;
import algorithms.BreadthFirstPicking;
import algorithms.ExhaustivePicking;
import algorithms.Experiment;
import algorithms.MultipleRandomWalk;
import algorithms.RandomWalk;
import algorithms.Traversal;
import distance.Distance;
import distance.Euclidean;
import lattice.Database;

public class UserStudyBaseline {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
		
	   Experiment exp;
	   int k =10;
	   Experiment.experiment_name="../ipynb/dashboards/json/UserStudyBaseline";
	   // Dataset #1  
//	   String dataset_name = "turn";
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//	   String yAxis = "slots_millis_reduces";
//	   String xAxis = "has_list_fn";
	   
	   // Dataset #2 
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList(
		"state", "driver_gender", "driver_race", "search_conducted",
		"contraband_found", "is_arrested", "stop_duration", 
		"stop_time_of_day", "driver_age_category"));
	   // "speeding_violations", "other_violations"
	   //"registration_plates_violations", "moving_violation","cell_phone_violations"
	   //"stop_outcome", 
	   
	   // Generate ct_police_stop.csv 
	   Database db = new Database();
	   ResultSet rs = Database.viz_query("ct_police_stop", groupby, "stop_outcome", "COUNT", new ArrayList<String>(Arrays.asList()));
	   Database.resultSet2csv(rs,"ct_police_stop",groupby,"COUNT");
	   System.out.println("Finished Materializing GroupBy Results");
	   String dataset_name = "ct_police_stop";
	   String yAxis = "id";
	   String xAxis = "is_arrested";//"stop_outcome", 
	   String aggType = "COUNT";
	   Distance dist = new Euclidean();
	   	   
	   Traversal ourAlgo = new BreadthFirstPicking();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.8,false);
	   exp.setAlgo(ourAlgo);
	   exp.runOutput(exp);
	   
	   Traversal clustering = new BaselineKmeans();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.001,false);
	   exp.setAlgo(clustering);
	   exp.runTableLayoutOutput(exp);   
	   
	   Traversal BBFS = new BaselineBFS();
	   exp.setAlgo(BBFS);
	   exp.runTableLayoutOutput(exp);   
	   
	   Traversal randWalk = new RandomWalk();
	   exp.setAlgo(randWalk);
	   exp.runTableLayoutOutput(exp);   
	   
	}
}
