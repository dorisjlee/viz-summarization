package evaluation;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	   // Dataset #1  // Dataset #2 
	   Experiment.experiment_name="../ipynb/dashboards/json/UserStudyBaseline";
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
	   
	   
	   Distance dist = new Euclidean();
	   Traversal BFS = new RandomWalk();
	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, BFS, dist,0,0.1,false);
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
