import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Traversal {
	// Traversal methods take in a hashmap representing the materialized graph and return a subgraph 
//	public static void main(String[] args) {
//		System.out.print("Hello world");
//		HashMap<String, ArrayList<Double>>  hmap = Hierarchia.computeVisualizationMap();
//		Hierarchia.print_map(hmap);
//		Euclidean ed = new Euclidean();
//		greedyPicking(hmap,ed);
//	}
	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
	}
	public static void greedyPicking(HashMap<String, ArrayList<Double>> id2MetricMap,  
									ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap ,Distance metric) {
		ArrayList<Integer> maxSubgraph =new ArrayList(); 
		System.out.println("---------------- Greedy Picking -----------------");
		// Variable Initialization
		double [] parentVal;
		double[]  childVal;
		Integer parentID;
		Node parentNode;
		Node childNode;
		double utility;
		// Start from root (nodeID=0)
		double [] parentVal =  ArrayList2Array(id2MetricMap.get("#"));
		Integer parentID = id2IDMap.get("#");
		Node parentNode = nodeList.get(parentID);
		maxSubgraph.add(parentID); // maxSubgraph must contain root
		while (parentNode.get_child_list()!=null) { // Terminate when hit leaf nodes (with no children)
			if (parentID!=0) {
				// Start from root (nodeID=0)
				parentVal =  ArrayList2Array(id2MetricMap.get("#"));
				parentID = id2IDMap.get("#");
				parentNode = nodeList.get(parentID);
				maxSubgraph.add(parentID); // maxSubgraph must contain root
			}
			ArrayList<Integer> children = parentNode.get_child_list();
			HashMap<Integer,Double> childUtilities =new HashMap<Integer,Double> (); 
			for (Integer childID: children) {
//				System.out.println(childID);
				childNode =  nodeList.get(childID);
				childVal = ArrayList2Array(id2MetricMap.get(childNode.get_id()));
				utility = metric.computeDistance(parentVal, childVal);
//				System.out.println(utility);
				childUtilities.put(childID,utility);
			}
			// System.out.print(Collections.max(childUtilities.values()));
			
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
		}
		
	}
	// Find children, compute utility, pick best children, repeat
	
	//	double [] parentVals = id2MetricMap.get("A$1#C$0#D$1#E$0#G$1#");
	//	double [] childVals = id2MetricMap.get("A$1#C$0#D$1#E$0#G$1#");
	//	double utility = metric.computeDistance(parentVals, childVals);
	//	System.out.println("done!");
	// base case if no children, then end
	
	
	
}
