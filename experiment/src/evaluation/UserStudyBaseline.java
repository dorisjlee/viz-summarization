package evaluation;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import algorithms.BreadthFirstPicking;
import algorithms.ExhaustivePicking;
import algorithms.Experiment;
import algorithms.GreedyPicking;
import algorithms.MultipleRandomWalk;
import algorithms.RandomWalk;
import algorithms.RecursiveBreadthFirstPicking;
import algorithms.RecursiveNaiveGreedyPicking;
import algorithms.Traversal;
import algorithms.TwoStepLookAheadalgorithm;
import distance.Distance;
import distance.EarthMover;
import distance.Euclidean;
import distance.KLDivergence;
import distance.MaxDiff;
import lattice.Database;

public class UserStudyBaseline {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
		
	   Experiment exp;
	   int k =10;
	   String aggFunc="SUM";
	   Experiment.experiment_name="../ipynb/dashboards/json/UserStudyBaseline";
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
	   // All Algo Experiments:
	   Traversal frontierGreedy = new BreadthFirstPicking();
	   Traversal cluster = new Kmeans();
       Traversal[] algoList= {frontierGreedy};
	   Distance dist = new Euclidean();
	   
	   
	   for (Traversal algo : algoList) {
		   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, dist,0,0.8,false);
		   long duration = exp.timedRunOutput(exp);   
	   }		   
	}
}
