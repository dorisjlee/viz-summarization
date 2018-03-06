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
	   //String dataset_name="Turn";
	   // Dataset #1 : Turn
//	   String dataset_name = "turn";
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   															"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//	   String yAxis = "slots_millis_reduces";
//	   String xAxis = "has_list_fn";
//	   String aggType = "SUM";
//	   Distance dist = new Euclidean();
	   
//	   // Dataset #2 : Police Stop
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList(
//		"driver_gender", "driver_race", "search_conducted",
//		"contraband_found", "is_arrested", "stop_duration", 
//		"stop_time_of_day", "driver_age_category"));
//	   // "speeding_violations", "other_violations"
//	   //"registration_plates_violations", "moving_violation","cell_phone_violations"
//	   //"stop_outcome", 
//	   String dataset_name = "ct_police_stop";
//	   String yAxis = "id";
//	   String xAxis = "is_arrested";//"stop_outcome", 
//	   String aggType = "COUNT";
//	   Distance dist = new Euclidean();
	   
	   // Dataset #3 : Mushroom 
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
	   String dataset_name = "mushroom";
	   String yAxis = "type";
	   String xAxis = "type";//"cap_surface"; 
	   String aggType = "COUNT";
	   Distance dist = new Euclidean();
	   
	   // Dataset #3 : Titanic 
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
//	   String dataset_name = "titanic";
//	   String yAxis = "id";
//	   String xAxis = "survived"; 
//	   String aggType = "COUNT";
//	   Distance dist = new Euclidean();
	   
	   	   
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
