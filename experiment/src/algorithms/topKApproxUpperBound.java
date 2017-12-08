package algorithms;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Ordering;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;
import lattice.Tuple;

/**
 * Pick Top-K edges to approximate upper bound for subgraph
 * Only Work for Offline Scenarioes
 */
public class topKApproxUpperBound extends Traversal{
	public HashMap<Tuple,Double> allUtilities =new HashMap<Tuple,Double> ();
	public topKApproxUpperBound() {
		super("Top K Approximated Upper Bound");
	}
	
	public void pickVisualizations(Experiment exp) {
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   System.out.println("---------------- Top K Approximated Upper Bound -----------------");
	   // Do BFS once to traverse through the graph and compute utilities, then find top k
	   levelwiseBFS(0);
	   // Get top k utilities
	   List<Double> topkList = Ordering.natural().greatestOf(allUtilities.values(), exp.k);
	   lattice.maxSubgraph=null;
	   lattice.maxSubgraphUtility=0;
	   // Print out and sum up the top k utilities
	   System.out.println("Top k edges [parent --> child] (ignore duplicate values, printing artifact):");
	   for (int i=0;i< exp.k;i++) {
		   for (Entry<Tuple,Double> entry:allUtilities.entrySet()) {
			   lattice.maxSubgraphUtility+=entry.getValue();
			   if (entry.getValue().equals(topkList.get(i))) {
				   System.out.print(entry.getKey()+",");
			   }
		   }
	   }
	   System.out.println("\nTop k utilities:"+topkList);
    	   System.out.println("Upper Bound:"+lattice.maxSubgraphUtility);
   }
	public void levelwiseBFS(Integer parentIndex) {
		Integer parentID;
		Node parentNode;
		double utility;
		if (parentIndex==0) { // Start from root (nodeID=0)
			parentID = lattice.id2IDMap.get("#");
			parentNode = lattice.nodeList.get(parentID);
			//levelwiseBFS(parentID);
		} else {
			// What was the best Child is now the new parent.
			parentNode = lattice.nodeList.get(parentIndex);
		}
		ArrayList<Integer> children = parentNode.get_child_list();
		if (children.size()==0) { 
			// Terminate when hit leaf nodes (with no children)
			return ; 
		}else {
			for (Integer childID: children) {
				utility = super.calculateNormalizedDistance(parentIndex, childID, exp);
				//System.out.println(utility);
				Tuple nodePair = new Tuple(parentIndex,childID);
				if (!allUtilities.containsKey(nodePair)) {
					allUtilities.put(nodePair,utility);
				}
				System.out.println("<"+nodePair.toString()+":"+utility+">");
				levelwiseBFS(childID);
			}
		}
	}
	public static void main (String[] args) throws SQLException {
	     
		Euclidean ed = new Euclidean();
		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	    String yAxis = "slots_millis_reduces";
	    String xAxis = "has_list_fn";
		Experiment exp = null;
		Traversal tr = new topKApproxUpperBound();
		try {
			exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", 10,tr, ed,0,0.8,false);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    tr.pickVisualizations(exp);
    }
}
