package evaluation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
	   String aggFunc="SUM";
	   Experiment.experiment_name="../ipynb/dashboards/json/UserStudyBaseline";
	   // Dataset #1  
//	   String dataset_name = "turn";
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//	   String yAxis = "slots_millis_reduces";
//	   String xAxis = "has_list_fn";
	   
	   // Dataset #2 
	   // Generate ct_police_stop.csv 
	   Database db = new Database();
	   //ArrayList<String> colArrs = new ArrayList<String>(Arrays.asList("driver_gender", "driver_race","contraband_found", "stop_outcome", "is_arrested", "stop_duration"));
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("driver_gender", "driver_race", "search_conducted", "contraband_found", "stop_outcome", "is_arrested", "stop_duration", "stop_time_of_day", "speeding_violations", "other_violations", "registration_plates_violations", "moving_violation", "cell_phone_violations", "lights_violations", "seat_belt_violations", "stop_sign_light_violations", "safe_movement_violations", "equipment_violations", "driver_age_category"));
	   ResultSet rs = Database.viz_query("ct_police_stop", groupby, "stop_outcome", "COUNT", new ArrayList<String>(Arrays.asList()));
	   
	   Database.resultSet2csv(rs,"ct_police_stop",groupby,"COUNT");
	   System.out.println("Finished Materializing GroupBy Results");
	   String dataset_name = "ct_police_stop";
	   String yAxis = "id";
	   String xAxis = "stop_outcome";
	   String aggType = "COUNT";
	   
	   Distance dist = new Euclidean();
	   Traversal BFS = new RandomWalk();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, BFS, dist,0,0.1,false);
	   exp.runTableLayoutOutput(exp);   
	   
//	   Traversal clustering = new BaselineKmeans();
//	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, clustering, dist,0,0.1,false);
//	   exp.runTableLayoutOutput(exp);   
	   
//	   Traversal ourAlgo = new ExhaustivePicking();
//	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, ourAlgo, dist,0,0.8,false);
//	   exp.runOutput(exp);
	   Traversal ourAlgo = new BreadthFirstPicking();
	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, ourAlgo, dist,0,0.8,false);
	   exp.runOutput(exp);
	}
}
