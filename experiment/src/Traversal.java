import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Traversal {
	// Traversal methods take in a hashmap representing the materialized graph and return a maximal subgraph (list of node indices)
	
	public static ArrayList<Integer> maxSubgraph =new ArrayList<Integer>();
	public static double maxSubgraphUtility=0;
	HashMap<String, ArrayList<Double>>  id2MetricMap;
	ArrayList<Node> nodeList;
	HashMap<String, Integer> id2IDMap;
	Distance metric;
	public Traversal(HashMap<String, ArrayList<Double>> id2MetricMap,  
			ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap ,Distance metric) {
		this.id2MetricMap = id2MetricMap;
		this.nodeList = nodeList;
		this.id2IDMap =id2IDMap;
		this.metric = metric;
	}
	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
	}
	public void findBestChild(Integer parentIndex) {
		//printMaxSubgraphSummary();
		// Variable Initialization
		double [] parentVal;
		double[]  childVal;
		Integer parentID;
		Node parentNode;
		Node childNode;
		double utility;
		if (parentIndex==0) { // Start from root (nodeID=0)
			parentVal =  ArrayList2Array(id2MetricMap.get("#"));
			parentID = id2IDMap.get("#");
			parentNode = nodeList.get(parentID);
			maxSubgraph.add(parentID); // maxSubgraph must contain root
			//findBestChild(parentID);
		} else {
			// What was the best Child is now the new parent.
			parentNode = nodeList.get(parentIndex);
			parentVal =  ArrayList2Array(id2MetricMap.get(parentNode.get_id()));
		}
		ArrayList<Integer> children = parentNode.get_child_list();
		if (children.size()==0 ) {//|| maxSubgraph.size()>5 
			// Terminate when hit leaf nodes (with no children)
			return ; 
		}else {
			HashMap<Integer,Double> childUtilities =new HashMap<Integer,Double> ();
			System.out.println("From all children:");
			for (Integer childID: children) {
				childNode = nodeList.get(childID);
				childVal = ArrayList2Array(id2MetricMap.get(childNode.get_id()));
				utility = metric.computeDistance(parentVal, childVal);
				childUtilities.put(childID,utility);
				System.out.println("<"+childID+","+nodeList.get(childID).get_id()+","+utility+">");
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
			System.out.println("Picked max Child: <"+ maxEntry.getKey() +","+nodeList.get(maxEntry.getKey()).get_id()+","+maxEntry.getValue()+">");
			maxSubgraph.add(maxEntry.getKey()); // adding this best child into the subgraph
			maxSubgraphUtility+=maxEntry.getValue();
			findBestChild(maxEntry.getKey());
		}
	}
	public void greedyPicking() {
		 
		System.out.println("---------------- Greedy Picking -----------------");
		 // Starting from Root,this trigger recursive call to findBestChild and updates maxSubgraph and maxSubgraphUtility
		findBestChild(0);
		printMaxSubgraphSummary();
	}	
	public void printMaxSubgraphSummary() {
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
				System.out.print(nodeList.get(maxSubgraph.get(j)).get_id()+"]\n");
			}else {
				System.out.print(nodeList.get(maxSubgraph.get(j)).get_id()+",");
			}
		}
		System.out.println("Total Utility:"+Double.toString(maxSubgraphUtility));
	}
}
