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
import distance.Euclidean;
import lattice.Database;

public class AlgoQualityPerformance {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
		
	   Experiment exp;
	   int k =10;
	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
	   
	   
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
	   // All Algo Experiments:
	   //exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, dist,0,0.8,false);
	   Traversal frontierGreedy = new BreadthFirstPicking();
	   Traversal levelWiseGreedy = new GreedyPicking();
	   Traversal exhaustive = new ExhaustivePicking();
	   Traversal SRW = new RandomWalk();
	   Traversal MRW = new MultipleRandomWalk(10000);
	   Traversal LA2max = new TwoStepLookAheadalgorithm("max");
	   Traversal LA2sum = new TwoStepLookAheadalgorithm("sum");
	   Traversal LA5 = new RecursiveBreadthFirstPicking(5);
	   Traversal LA5_levelwise = new RecursiveNaiveGreedyPicking(5);
	   //String [] algoList = {"frontierGreedy","multipleRandomWalk","naiveExhaustive"};
	   Traversal[] algoList= {frontierGreedy,SRW,MRW,LA2max,LA2sum,LA5,LA5_levelwise};//exhaustive,
	   for (Traversal algo : algoList) {
		   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, dist,0,0.8,false);
		   long duration = exp.timedRunOutput(exp);
		   writer.println(xAxis+","+yAxis+","+algo.getAlgoName()+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+duration+","+exp.lattice.maxSubgraphUtility);
	   }
	   /*
	    * tr = new TwoStepLookAheadalgorithm(lattice,new Euclidean(), "max");
       tr.pickVisualizations(k);
       
       tr = new TwoStepLookAheadalgorithm(lattice,new Euclidean(), "sum");
       tr.pickVisualizations(k);
       
       tr = new RecursiveBreadthFirstPicking(lattice, new Euclidean(), 2);
       tr.pickVisualizations(k);
       
       tr = new RecursiveNaiveGreedyPicking(lattice, new Euclidean(), 2);
       tr.pickVisualizations(k);
	    */
	   writer.close();
	    
	    
	  	   
	}
}
