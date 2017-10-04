import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Traversal {
	// Traversal methods take in a hashmap representing the materialized graph and return a maximal subgraph (list of node indices)

	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
	}
	public static void greedyPicking(HashMap<String, ArrayList<Double>> id2MetricMap,  
									ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap ,Distance metric) {
		ArrayList<Integer> maxSubgraph =new ArrayList<Integer>(); 
		System.out.println("---------------- Greedy Picking -----------------");
		// Variable Initialization
		double [] parentVal;
		double[]  childVal;
		Integer parentID;
		Node parentNode;
		Node childNode;
		double utility;
		double maxSubgraphUtility=0;
		// Start from root (nodeID=0)
		parentVal =  ArrayList2Array(id2MetricMap.get("#"));
		parentID = id2IDMap.get("#");
		parentNode = nodeList.get(parentID);
		maxSubgraph.add(parentID); // maxSubgraph must contain root
		while (parentNode.get_child_list().size()!=0) { // Terminate when hit leaf nodes (with no children)
			ArrayList<Integer> children = parentNode.get_child_list();
			HashMap<Integer,Double> childUtilities =new HashMap<Integer,Double> (); 
			for (Integer childID: children) {
				childNode =  nodeList.get(childID);
				childVal = ArrayList2Array(id2MetricMap.get(childNode.get_id()));
				utility = metric.computeDistance(parentVal, childVal);
				childUtilities.put(childID,utility);
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
			System.out.println(maxEntry.getKey()); // max child ID
			System.out.println(maxEntry.getValue()); // max child's utility
			maxSubgraphUtility+=maxEntry.getValue();
			// Best Child is now the new parent
			parentID = maxEntry.getKey();
			parentNode = nodeList.get(parentID);
			parentVal =  ArrayList2Array(id2MetricMap.get(parentNode.get_id()));
			maxSubgraph.add(parentID);  // adding this best child into the subgraph
		}
		// Summary of maximum subgraph 
		System.out.print("Max Subgraph: [");
		for (int j =0 ; j< maxSubgraph.size();j++) {
			if (j==maxSubgraph.size()-1) {
				System.out.print(Integer.toString(maxSubgraph.get(j))+"]\n");
			}else {
				System.out.print(Integer.toString(maxSubgraph.get(j))+",");
			}
		}
		System.out.print("[");
		for (int j =0 ; j< maxSubgraph.size();j++) {
			if (j==maxSubgraph.size()-1) {
				System.out.print(nodeList.get(j).get_id()+"]\n");
			}else {
				System.out.print(nodeList.get(j).get_id()+",");
			}
		}
		
		
		
		System.out.println("Total Utility:"+Double.toString(maxSubgraphUtility));
		
	}	
}
