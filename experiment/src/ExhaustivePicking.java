import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * 
 * @param k
 * @author dorislee
 */
public class ExhaustivePicking extends Traversal{
	static int numCompletedGraph=0;
	public ExhaustivePicking(Lattice lattice, Distance metric) {
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
		pickChildren(k, rootSubgraph, lattice.nodeList.get(rootId));
		printMaxSubgraphSummary();
	}
	
	/*
	 * Implementing a recursive algorithm that picks all possible combinations of children
	 * @ param
	 * k: maximal number of nodes picked 
	 * G:  currently picked subgraph
	 * pivot : node of interest that we are picking the children from
	 */
	public void pickChildren(Integer k, ArrayList<Integer> G, Node pivot) {
		System.out.println("pivot="+pivot.id);
		System.out.println("pivot="+lattice.nodeList.indexOf(pivot));
		int n = G.size();
		ArrayList<Integer> pivot_children = pivot.get_child_list();
		int m = Math.min(k-n, pivot_children.size());
//		int m= k-n;
		for (int i =1; i<=m; i++) {
			System.out.println("i="+i);
			ArrayList<ArrayList<Integer>> child_combo_list = combination(pivot_children,i);
			//System.out.println("child_combo_list:"+child_combo_list);
			for (ArrayList<Integer> child_combo : child_combo_list) {
				//System.out.println("child_combo:"+child_combo);
				//System.out.println("G:"+G);
				ArrayList<Integer> newG =  (ArrayList<Integer>) Stream.concat(G.stream(), child_combo.stream())
                        						.collect(Collectors.toList());
				double totalUtility =0;
				if (newG.size()==k) {
					numCompletedGraph+=1;
					totalUtility=computeSubGraphUtility(newG);
					System.out.println("newG:"+newG+":"+totalUtility);
					for (int j=0; j<newG.size();j++) {
						System.out.print(lattice.nodeList.get(newG.get(j)).id);
					}
					System.out.println("\n");
					
					/*if (newG.get(1)==14) {
						System.out.println("newG:"+newG+":"+totalUtility);
					}*/
					if (totalUtility>lattice.maxSubgraphUtility) {
						//System.out.println("newG:"+newG);
						//System.out.println("totalUtility:"+totalUtility);
						lattice.maxSubgraph = newG;
						lattice.maxSubgraphUtility = totalUtility;
						//VizOutput.dumpGenerateNodeDicFromNoHierarchia(i, lattice, newG);
					}
				}
				//System.out.println("newG:"+newG);
				//System.out.println("newG.size():"+newG.size());
				if (newG.size()<k) {
					for (int childID: child_combo) {
						int childIdx = pivot.get_child_list().indexOf(childID);
						Node childNode  = lattice.nodeList.get(childID);
						pickChildren(k,newG, childNode);
					}
				}
			}
		}
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
        /*
        for (int i =0;i<all_combo.size();i++) {
        		System.out.println(all_combo.get(i));
        }
        */
        return all_combo;
    }
 
    
    public static void main (String[] args) throws SQLException {
	    
//	    	ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
//		int r = 3;
//        combination(pivot_children, r);
        

        Euclidean ed = new Euclidean();
//    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
    		Hierarchia h = new Hierarchia("titanic","survived");
    		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
    		System.out.println(lattice.nodeList.get(0).id+":"+lattice.nodeList.get(0).child_list);
    		System.out.println(lattice.nodeList.get(1).id+":"+lattice.nodeList.get(1).child_list);
    		System.out.println(lattice.nodeList.get(2).id+":"+lattice.nodeList.get(2).child_list);
    		System.out.println(lattice.nodeList.get(3).id+":"+lattice.nodeList.get(3).child_list);
    		System.out.println(lattice.nodeList.get(4).id+":"+lattice.nodeList.get(4).child_list);
    		System.out.println(lattice.nodeList.get(5).id+":"+lattice.nodeList.get(5).child_list);
        Traversal tr; 
        tr = new ExhaustivePicking(lattice,new Euclidean());
        tr.pickVisualizations(5);
        System.out.println(numCompletedGraph);
        
        tr = new GreedyPicking(lattice,new Euclidean());
        tr.pickVisualizations(5);
        
        tr = new BreadthFirstPicking(lattice,new Euclidean());
        tr.pickVisualizations(5);
        
        
        
        
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