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
	   String dataset_name="mushroom";
	   ArrayList<String> groupby = null;
	   String yAxis = null;
	   String xAxis = null; 
	   String aggType = null;
	   Distance dist = new Euclidean();
	   // Dataset #1 : Turn
	   if (dataset_name.equals("turn")){
		   	groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
				"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
			yAxis = "slots_millis_reduces";
			xAxis = "has_list_fn";
			aggType = "SUM";
	   }else if (dataset_name.equals("ct_police_stop")) {
		   // Dataset #2 : Police Stop
		   groupby = new ArrayList<String>(Arrays.asList(
			"driver_gender", "driver_race", "search_conducted",
			"contraband_found", "is_arrested", "stop_duration", 
			"stop_time_of_day", "driver_age_category"));
		   // "speeding_violations", "other_violations"
		   //"registration_plates_violations", "moving_violation","cell_phone_violations"
		   //"stop_outcome", 
		   yAxis = "id";
		   xAxis = "is_arrested";//"stop_outcome", 
		   aggType = "COUNT";
	   }else if (dataset_name.equals("mushroom")) {
		   // Dataset #3 : Mushroom 
		   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
		   yAxis = "type";
		   xAxis = "type";//"cap_surface"; 
		   aggType = "COUNT";
	   }else if (dataset_name.equals("titanic")) {
		   // Dataset #3 : Titanic 
		   groupby = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
		   yAxis = "id";
		   xAxis = "survived"; 
		   aggType = "COUNT";
	   }
	   	   
	   Traversal ourAlgo = new BreadthFirstPicking();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.9,false);
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
