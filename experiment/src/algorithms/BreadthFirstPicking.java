package algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import distance.Distance;
import lattice.Lattice;
import lattice.Node;

/**
 * In each iteration we pick a node with the highest utility from a set of "frontier" nodes.
 * Then, the children of the picked node are added to the frontier.
 * 
 * @param k
 * @author saarkuzi
 */
public class BreadthFirstPicking extends Traversal{

	public BreadthFirstPicking(Lattice lattice, Distance metric) {
		super(lattice, metric, "Frontier Greedy Picking");
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
			    
		//a map in which keys are node IDs, and values are utilities (=interestingness)
		HashMap<Integer,Float> localMaxSubgraph = new HashMap<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.put(rootId, 0f);
		HashMap<Integer,Float> externalNodesUtility = updateExternal(localMaxSubgraph, new HashMap<>(), rootId);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(externalNodesUtility.size() == 0) break;
			Integer selectedNodeID = Collections.max(externalNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			localMaxSubgraph.put(selectedNodeID, externalNodesUtility.get(selectedNodeID));
			localMaxSubgraph = updateUtilities(localMaxSubgraph, selectedNodeID);
			externalNodesUtility = updateExternal(localMaxSubgraph, externalNodesUtility, selectedNodeID);
		}			
		
		// save the current result to a temporary array
		ArrayList<Integer> maxSubgraphArray = new ArrayList<Integer>();
		for(Integer nodeId : localMaxSubgraph.keySet())
			maxSubgraphArray.add(nodeId);
		
		// improve the current solution by doing local changes
		LocalGraphImprove lgi = new LocalGraphImprove(lattice, metric);
		maxSubgraphArray = lgi.improveSubgraphLocally(maxSubgraphArray);
		
		// updated the final solution
		for(int nodeId : maxSubgraphArray)
			lattice.maxSubgraph.add(nodeId);
		super.updateSubGraphUtility();
		printMaxSubgraphSummary();
	}
	
	/**
	 * 
	 * When adding a new node to the subgraph, we should check if it already has children
	 * there (possible because node can have several informative parents).
	 * If it is the case, then the utility of the node needs to be updated by using the most 
	 * informative parent.
	 * 
	 * @param currentMaxSubgraph, nodeId
	 */
	protected HashMap<Integer,Float> updateUtilities(HashMap<Integer,Float> currentMaxSubgraph, int nodeId)
	{
		Node currentNode = lattice.nodeList.get(nodeId);
		for(int childId : currentNode.child_list)
		{
			if(!currentMaxSubgraph.containsKey(childId)) continue;
			Double newUtility = super.calculateDistance(nodeId, childId, lattice, metric);
			Float currentUtility = currentMaxSubgraph.get(childId);
			currentMaxSubgraph.put(childId, (float) Math.max(currentUtility, newUtility));
		}	
		return currentMaxSubgraph;
	}
	
	/**
	 * Adding nodes to a frontier; the added nodes are the children of 
	 * a given parent node.
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	protected HashMap<Integer, Float> updateExternal(HashMap<Integer, Float> localMaxSubgraph, HashMap<Integer, Float> currentFrontier, Integer parentNodeId)
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.containsKey(childId)) continue;
			double utility = super.calculateDistance(parentNodeId, childId, lattice, metric);
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}
		
}