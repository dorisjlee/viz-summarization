import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Traversal {
	// Traversal methods take in a hashmap representing the materialized graph and return a maximal subgraph (list of node indices)
	Lattice lattice;
	Distance metric;
	public Traversal(Lattice lattice,Distance metric) {
		this.lattice = lattice;
		this.metric = metric;
	}
	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
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
			System.out.println("From all children:");
			for (Integer childID: children) {
				childNode = lattice.nodeList.get(childID);
				childVal = ArrayList2Array(lattice.id2MetricMap.get(childNode.get_id()));
				utility = metric.computeDistance(parentVal, childVal);
				childUtilities.put(childID,utility);
				System.out.println("<"+childID+","+lattice.nodeList.get(childID).get_id()+","+utility+">");
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
			System.out.println("Picked max Child: <"+ maxEntry.getKey() +","+lattice.nodeList.get(maxEntry.getKey()).get_id()+","+maxEntry.getValue()+">");
			lattice.maxSubgraph.add(maxEntry.getKey()); // adding this best child into the subgraph
			lattice.maxSubgraphUtility+=maxEntry.getValue();
			findBestChild( maxEntry.getKey(),k);
		}
	}
	public void greedyPicking(Integer k ) {
		System.out.println("---------------- Greedy Picking -----------------");
		 // Starting from Root,this trigger recursive call to findBestChild and updates maxSubgraph and maxSubgraphUtility
		findBestChild( 0, k);
		printMaxSubgraphSummary();
	}	
	public void printMaxSubgraphSummary() {
		// Summary of maximum subgraph 
		System.out.print("Max Subgraph: [");
		for (int j =0 ; j< lattice.maxSubgraph.size();j++) {
			if (j==lattice.maxSubgraph.size()-1) {
				System.out.print(Integer.toString(lattice.maxSubgraph.get(j))+"]\n");
			}else {
				System.out.print(Integer.toString(lattice.maxSubgraph.get(j))+",");
			}
		}
		System.out.print("[");
		for (int j =0 ; j< lattice.maxSubgraph.size();j++) {
			if (j==lattice.maxSubgraph.size()-1) {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+"]\n");
			}else {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+",");
			}
		}
		System.out.println("Total Utility:"+Double.toString(lattice.maxSubgraphUtility));
	}
	public static void main(String[] args) 
    {
       Lattice lattice = Hierarchia.generateFullyMaterializedLattice();
       System.out.println(lattice.nodeList);
       Hierarchia.print_map(lattice.id2IDMap);
       Hierarchia.print_map(lattice.id2MetricMap);
       Traversal tr = new Traversal(lattice,new Euclidean());
       tr.greedyPicking(10);
    }
}
