package algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;

/**
 * Naive Exhaustive Picking Baseline
 * 
 * @param k
 * @author dorislee
 */
public class NaiveExhaustivePicking extends Traversal{
	public NaiveExhaustivePicking(Lattice lattice, Distance metric) {
		super(lattice, metric, "Exhaustive Picking");
	}

	/**
	 * 
	 * Implementation of the traversal algorithm for generating a subgraph
	 * with maximal utility of k nodes
	 * 
	 * @param k
	 */
	public void pickVisualizations(Integer k)
	{
		super.printAlgoName();
		
	    lattice.maxSubgraph.clear();
	    lattice.maxSubgraphUtility = 0;
	    
		//a map in which keys are node IDs, and values are utilities (interestingness)
		HashMap<Integer,Float> localMaxSubgraph = new HashMap<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.put(rootId, 0f);
		ArrayList<Integer> rootSubgraph = new ArrayList<Integer>(Arrays.asList(rootId));
		
//		combination(ArrayList<Integer> arr,   int r)
//		lattice.nodeList
//		printMaxSubgraphSummary();
	}
	
	
    static void combinationUtil(ArrayList<ArrayList<Integer>> all_combo,ArrayList<Integer> arr, ArrayList<Integer> data, int start,
                                int end, int index, int r)
    {
        if (index == r)
        {
        		ArrayList<Integer> combo = new ArrayList<Integer>();
            for (int j=0; j<r; j++)
            		combo.add(data.get(j));
            all_combo.add(combo);
            return;
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data.set(index,arr.get(i)) ;
            combinationUtil(all_combo, arr, data, i+1, end, index+1, r);
        }
    }
 
	/* Create all possible combination of the children nodes of size r
	 * @param 
	 * r: number of children to pick in the combination
	 */
    static ArrayList<ArrayList<Integer>> combination(ArrayList<Integer> arr,   int r)
    {
    		ArrayList<ArrayList<Integer>> all_combo = new ArrayList<ArrayList<Integer>>(); 
        // A temporary array to store all combination one by one
    		ArrayList<Integer> data = (ArrayList<Integer>) arr.clone();
        combinationUtil(all_combo,arr, data, 0, arr.size()-1, 0, r);
        // Print all combination
        
        for (int i =0;i<all_combo.size();i++) {
        		System.out.println(all_combo.get(i));
        }
        
        return all_combo;
    }
 
    
    public static void main (String[] args) throws SQLException {
	    
	    	ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
		int r = 3;
        combination(pivot_children, r);
        

        Euclidean ed = new Euclidean();
//    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
    		Hierarchia h = new Hierarchia("titanic","survived");
    		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
        Traversal tr; 
        tr = new NaiveExhaustivePicking(lattice,new Euclidean());
        tr.pickVisualizations(5);
        
        
//        tr = new GreedyPicking(lattice,new Euclidean());
//        tr.pickVisualizations(5);
//        
//        tr = new BreadthFirstPicking(lattice,new Euclidean());
//        tr.pickVisualizations(5);
        
        
        
        
// 	   Experiment exp;
// 	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
// 	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
// 	   String [] algoList = {"frontierGreedy","naiveGreedy","greedy","exhaustive"};
// 	   int numIterations = 10;
// 	   int k =10;
// 	   String aggFunc="SUM";
// 	   experiment_name="../ipynb/dashboards/json/"+"baseline";
// 	   //Debugging Exhaustive
// 	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("has_list_sum_range_fn","has_corr_list_fn","has_prof_clicks_tbl","has_est_distinct","has_list_sum_fn","has_impressions_tbl","is_profile_query","has_prof_engagement_events_tbl"));
// 	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "frontierGreedy", new Euclidean(),0,0.8);
// 	   long duration = exp.timedRunOutput();
// 	   System.out.println("Duration:"+duration);
// 	   exp.algo.printMaxSubgraphSummary();
// 	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "greedy", new Euclidean(),0,0.8);
// 	   duration = exp.timedRunOutput();
// 	   System.out.println("Duration:"+duration);
// 	   exp.algo.printMaxSubgraphSummary();
// 	   
// 	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "exhaustive", new Euclidean(),0,0.8);
// 	   long duration = exp.timedRunOutput();
// 	   System.out.println("Duration:"+duration);
// 	   exp.algo.printMaxSubgraphSummary();
    }
}