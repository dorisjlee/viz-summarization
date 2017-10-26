

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Naive recursive level-wise picking strategy that picks one best node at each level.
 * Resulting in a maximal subgraph of size MIN(k, max_depth) where max_depth = # of attributes 
 * @param k
 * @author dorislee
 */
public class NaiveGreedyPicking extends Traversal {

	public NaiveGreedyPicking(Lattice lattice, Distance metric) {
		super(lattice, metric, "Naive Greedy Picking");
	}
	
	
	
	
	public void pickVisualizations(Integer k) {

		super.printAlgoName();
		
		 // Starting from Root,this trigger recursive call to findBestChild and updates maxSubgraph and maxSubgraphUtility
		findBestChild(0, k);
		printMaxSubgraphSummary();
	}	
	
	public void findBestChild(Integer parentIndex,Integer k) {
		//printMaxSubgraphSummary();
		// Variable Initialization
		double [] parentVal;
		double[]  childVal;
		Integer parentID;
		Node parentNode;
		Node childNode;
		double utility;
		if (parentIndex==0) { // Start from root (nodeID=0)
			parentVal =  ArrayList2Array(lattice.id2MetricMap.get("#"));
			parentID = lattice.id2IDMap.get("#");
			parentNode = lattice.nodeList.get(parentID);
			lattice.maxSubgraph.add(parentID); // maxSubgraph must contain root
			//findBestChild(parentID);
		} else {
			// What was the best Child is now the new parent.
			parentNode = lattice.nodeList.get(parentIndex);
			parentVal =  ArrayList2Array(lattice.id2MetricMap.get(parentNode.get_id()));
		}
		ArrayList<Integer> children = parentNode.get_child_list();
		if (children.size()==0 || lattice.maxSubgraph.size()>k) {//|| maxSubgraph.size()>5 
			// Terminate when hit leaf nodes (with no children)
			return ; 
		}else {
			HashMap<Integer,Double> childUtilities =new HashMap<Integer,Double> ();
			//System.out.println("From all children:");
			for (Integer childID: children) {
				childNode = lattice.nodeList.get(childID);
				childVal = ArrayList2Array(lattice.id2MetricMap.get(childNode.get_id()));
				utility = metric.computeDistance(parentVal, childVal);
				childUtilities.put(childID,utility);
				//System.out.println("<"+childID+","+lattice.nodeList.get(childID).get_id()+","+utility+">");
			}
			// Find the child with the max utility
			HashMap.Entry<Integer, Double> maxEntry = null;
			for (HashMap.Entry<Integer, Double> entry : childUtilities.entrySet())
			{
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			    {
			        maxEntry = entry;
			    }
			}
			//System.out.println("Picked max child: <"+ maxEntry.getKey() +","+lattice.nodeList.get(maxEntry.getKey()).get_id()+","+maxEntry.getValue()+">");
			lattice.maxSubgraph.add(maxEntry.getKey()); // adding this best child into the subgraph
			lattice.maxSubgraphUtility+=maxEntry.getValue();
			findBestChild(maxEntry.getKey(),k);
		}
	}
}
