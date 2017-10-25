import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

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
	public void naiveGreedyPicking(Integer k ) {
		/**
		 * Naive recursive level-wise picking strategy that picks one best node at each level.
		 * Resulting in a maximal subgraph of size MIN(k, max_depth) where max_depth = # of attributes 
		 * @param k
		 * @author dorislee
		 */
		System.out.println("---------------- Naive Greedy Picking -----------------");
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
	
	public void greedyPicking(Integer k) {
		/**
		 * Looping through all children of current node (over all node in nodeList)
		 * Greedily adding in nodes with larger utility than current max 
		 * Stop until reach k nodes in dashboard
		 */
	   System.out.println("---------------- Greedy Picking -----------------");
       double total_utility =0;
       ArrayList<Integer> dashboard = new ArrayList<Integer>();
       dashboard.add(0); // Adding root
       // Stop when dashboard exceeds desired size k 
       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
       {	
       	   double max_utility = 0;
           //System.out.println("Dashboard Size: "+dashboard.size());
           int next = -1;
           for(int i = 0; i < dashboard.size(); i++)
           {
               //System.out.println("Children of: "+node_list.get(dashboard.get(i)).get_id());
        	       // Looping through all children indexes 
               for(int j = 0; j < lattice.nodeList.get(dashboard.get(i)).get_dist_list().size(); j++)
               {
            	   	   
                   int flag = 0;
                   //System.out.println("Current Node: "+node_list.get(dashboard.get(i)).get_child_list().get(j));
                   for(int sp = 0; sp < dashboard.size(); sp++)
                   {
                       // Check if the node to be added is already in the dashboard 
                       if(lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j).equals(dashboard.get(sp)))
                       {
                           //System.out.println("Already in");
                           flag =1;
                           break;
                       }
                   }
                   if(flag == 0 && lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j) > max_utility)
                   {
                       max_utility = lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j);
                       next = lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j);
                   }
               }
           }
           dashboard.add(next);
           total_utility+=max_utility;
       }
       lattice.maxSubgraph= dashboard; 
       lattice.maxSubgraphUtility=total_utility;
       printMaxSubgraphSummary();
   }
	
	/**
	 * In each iteration we pick a node with the highest utility from a set of "frontier" nodes.
	 * Then, the children of the picked node are added to the frontier.
	 * 
	 * @param k
	 * @author saarkuzi
	 */
	public void frontierGreedyPicking(Integer k)
	{
		System.out.println("---------------- Frontier Greedy Picking -----------------");
	    lattice.maxSubgraph.clear();
	    lattice.maxSubgraphUtility = 0;
	    
		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		lattice.maxSubgraph.add(rootId);
		HashMap<Integer,Float> frontierNodesUtility = expandFrontier(new HashMap<>(), rootId);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(frontierNodesUtility.size() == 0) break;
			Integer selectedNodeID = Collections.max(frontierNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			lattice.maxSubgraphUtility += frontierNodesUtility.get(selectedNodeID);
			lattice.maxSubgraph.add(selectedNodeID);
			frontierNodesUtility = expandFrontier(frontierNodesUtility, selectedNodeID);
		}
		printMaxSubgraphSummary();
	}
	
	/**
	 * Adding nodes to a frontier group which are the children of some given parent node.
	 * This function can be using in one of the node picking functions.
	 * 
	 * @param currentFrontier, parentNodeId
	 * @author saarkuzi
	 */
	private HashMap<Integer, Float> expandFrontier(HashMap<Integer, Float> currentFrontier, Integer parentNodeId)
	{
		HashMap<Integer, Float> newFrontier = currentFrontier;
		newFrontier.remove(parentNodeId);
		Node parentNode = lattice.nodeList.get(parentNodeId);
		double[] parentVal = ArrayList2Array(lattice.id2MetricMap.get(parentNode.get_id()));
		for(Integer childId : parentNode.get_child_list())
		{	
			if(lattice.maxSubgraph.contains(childId)) continue;
			Node childNode = lattice.nodeList.get(childId);
			double[] childVal = ArrayList2Array(lattice.id2MetricMap.get(childNode.get_id()));
			double utility = metric.computeDistance(childVal, parentVal);
			if(newFrontier.containsKey(childId))
				newFrontier.put(childId, (float) Math.max(newFrontier.get(childId), utility));
			else
				newFrontier.put(childId, (float) utility);
		}
		return newFrontier;
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
	public static void mergeNodes(Lattice lattice) {
//		lattice.maxSubgraph;
//		lattice.id2MetricMap
		HashMap<ArrayList<Double>,ArrayList<Integer>> val2IDsMap=new HashMap<ArrayList<Double>,ArrayList<Integer>>();
		for (int id : lattice.maxSubgraph) {
			ArrayList<Double> value = lattice.id2MetricMap.get(lattice.nodeList.get(id).get_id());
			System.out.println(value);
			ArrayList<Integer> IDs = val2IDsMap.get(value);
			if (IDs!=null) {
				// exist previous entry, add to previous list
				val2IDsMap.get(value).add(id);
			}else {
				// No previous entry, add new array list with that ID
				val2IDsMap.put(value, new ArrayList<Integer>(id));
			}
		}
		//Hierarchia.print_map(val2IDsMap);
//		for (ArrayList<Double>,ArrayList<Integer> a :val2IDsMap.entrySet())
	}
	public static void main(String[] args) throws SQLException 
    {
	   Euclidean ed = new Euclidean();
	   //Hierarchia h = new Hierarchia("turn","has_list_fn");
	   //Hierarchia h = new Hierarchia("titanic","pc_class");
	   Hierarchia h = new Hierarchia("mushroom","type");
	   Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
       Traversal tr = new Traversal(lattice,new Euclidean());
       //Hierarchia.print_map(lattice.id2MetricMap);
       //Hierarchia.print_map(lattice.id2IDMap);
//       tr.naiveGreedyPicking(20);
//       tr.greedyPicking(20);
       tr.frontierGreedyPicking(20);
       mergeNodes(lattice);
    }
}
