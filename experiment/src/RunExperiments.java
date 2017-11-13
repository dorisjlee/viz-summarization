import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class RunExperiments {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
//	   Experiment exp;
//	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
//	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
//	   String [] algoList = {"frontierGreedy","naiveGreedy","greedy","exhaustive"};
//	   int numIterations = 10;
//	   int k =10;
//	   String aggFunc="SUM";
//	   experiment_name="../ipynb/dashboards/json/"+"baseline";
//	   //Debugging Exhaustive
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("has_list_sum_range_fn","has_corr_list_fn","has_prof_clicks_tbl","has_est_distinct","has_list_sum_fn","has_impressions_tbl","is_profile_query","has_prof_engagement_events_tbl"));
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "frontierGreedy", new Euclidean(),0,0.8);
//	   long duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "greedy", new Euclidean(),0,0.8);
//	   duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
//	   
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "exhaustive", new Euclidean(),0,0.8);
//	   long duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
	   
//	   groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//	   exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", 30, "frontierGreedy", new Euclidean(),0,0.1);
//	   exp.runOutput();
////	   int[] array = new int[exp.lattice.nodeList.size()];
//	   ArrayList<Integer> list = new ArrayList<Integer>();
//	   for (int i =0;i<50;i=i+2)
//	   {
//		   list.add(i);
//	   }
//	   VizOutput.dumpGenerateNodeDicFromNoHierarchia(99, exp.lattice,list); 
////	   VizOutput.dumpGenerateNodeDicFromNoHierarchia(99, exp.lattice,exp.lattice.maxSubgraph);
//	   System.out.println(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#"));
//	   System.out.println(exp.lattice.nodeList.get(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#")).get_child_list());
//	   for (int i: exp.lattice.nodeList.get(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#")).get_child_list()) {
//		   System.out.println(exp.lattice.nodeList.get(i).get_id());
//	   }
	   /*
	   // Baseline experiment
	   PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
	   writer.println("xAxis,yAxis,algo,groupby,total_time,total_utility");
	   for (int i=0;i<numIterations;i++) {
		   System.out.println("---------------- Iteration #"+i+"----------------");
		   // We are picking 8 random dimensions in the groupby and one random measure value, since 35C5 combinations is too much, we are just picking random samples of potential dashboards in our experiments.
		   ArrayList<String> groupby = pickNRandom(all_dimensions, 8);
		   String yAxis = all_measures.get(new Random().nextInt(all_measures.size()));
		   String xAxis = groupby.get(new Random().nextInt(groupby.size()));
		   //System.out.println(groupby);
		   //System.out.println(xAxis+","+yAxis);
		   for (String algo : algoList) {
			   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, new Euclidean(),0,0.8);
			   long duration = exp.timedRunOutput();
			   writer.println(xAxis+","+yAxis+","+algo+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+duration+","+exp.lattice.maxSubgraphUtility);
		   }
	   }
	   writer.close();
	   */
	   
	   // Generating all possible outputs for frontend to use
	   Distance [] distList = {new KLDivergence(),new MaxDiff(),new EarthMover(),new Euclidean()};
       String [] algoList = {"frontierGreedy","naiveGreedy","greedy"};
       //experiment_name="../ipynb/dashboards/json/"+"vary_all";
       double [] ip_vals = {0.1,0.3,0.5,0.7,0.9,1};
       double [] ic_vals = {0,0.05,0.1,0.15,0.2};
       int [] k_vals = {15,20,25,30};
       
   	   for (double ip: ip_vals) {
   		   for (double ic: ic_vals) {
   			   for (Distance dist:distList) {
   				   ArrayList<String>  titanic_groupby = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
		   		   Experiment titanic = new Experiment("titanic", "survived", "id",titanic_groupby, "COUNT", dist,ic,ip);
		   		   ArrayList<String> turn_groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
		   				   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
		   		   Experiment turn = new Experiment("turn", "has_list_fn", "slots_millis_reduces",turn_groupby,"SUM",dist,ic,ip);
		   		   
		   		   ArrayList<String> mushroom_groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
		   		   Experiment mushroom1  = new Experiment("mushroom","type","type",mushroom_groupby, "COUNT",dist,ic,ip);
		   		   Experiment mushroom2 = new Experiment("mushroom","cap_surface","cap_surface",mushroom_groupby, "COUNT",dist,ic,ip);
		   		   System.out.println("mushroom1.h.datasetName:"+mushroom1.getH().datasetName);
		   		   System.out.println("titanic.h.datasetName:"+titanic.getH().datasetName);
			    	   for (String algo:algoList) {
	    	   			   for (int k : k_vals) {
//	    	   				   try {
						    	   titanic.runOutput(k, algo);
						    	   turn.runOutput(k, algo);
						    	   mushroom1.runOutput(k, algo);
						    	   mushroom2.runOutput(k, algo);
//	    	   				   }
//	    	   				   catch (Exception e){
//	    	   					   System.out.println("Failed at:"+k+","+ic+","+ip+","+algo);
//	    	   				   }
	    	   			   }
	    	   		   }
	    	   	   }
	       }
       }
       
       
       /*
		// Testing different algo on different datasets
       String algo = "frontierGreedy";
       double [] ip_vals = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
       experiment_name="../ipynb/dashboards/json/"+"vary_dataset_ip";
       for (double ip: ip_vals) {
    	   	   System.out.println("ip:"+ip);
	    	   exp = new Experiment("titanic", "survived", "COUNT(id)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("turn", "has_list_fn", "SUM(slots_millis_reduces)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","type", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","cap_surface", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp.h.db.c.close();
       }
	   */	   
	   
	}
}
