import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
		HashMap<Integer,Float> externalNodesUtility = updateExternal(new HashMap<>(), rootId);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(externalNodesUtility.size() == 0) break;
			Integer selectedNodeID = Collections.max(externalNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			localMaxSubgraph.put(selectedNodeID, externalNodesUtility.get(selectedNodeID));
			localMaxSubgraph = updateUtilities(localMaxSubgraph, selectedNodeID);
			externalNodesUtility = updateExternal(externalNodesUtility, selectedNodeID);
		}			
		
		// save the current result to a temporary array
		ArrayList<Integer> maxSubgraphArray = new ArrayList<Integer>();
		for(Integer nodeId : localMaxSubgraph.keySet())
			maxSubgraphArray.add(nodeId);
		
		// improve the current solution by doing local changes
		maxSubgraphArray = improveSubgraphLocally(maxSubgraphArray);
		
		// updated the final solution
		for(int nodeId : maxSubgraphArray)
			lattice.maxSubgraph.add(nodeId);
		super.updateSubGraphUtility();
		printMaxSubgraphSummary();
	}
	
	/**
	 * Take a subgraph and perform local changes that give added value
	 * 
	 * @param subgraph
	 */
	public ArrayList<Integer> improveSubgraphLocally(ArrayList<Integer> subgraph)
	{
		HashMap<Integer, Float> subgraphWithUtilities = new HashMap<>();
		for(Integer nodeId : subgraph)
			subgraphWithUtilities.put(nodeId, 0f);
		
		for(Integer nodeId : subgraph)
			subgraphWithUtilities = updateUtilities(subgraphWithUtilities, nodeId);
		
		Float oldUtility = super.sumMapByValue(subgraphWithUtilities);
		Float newUtility = oldUtility;
		
		do {
			oldUtility = newUtility;
			HashMap<Integer,Float> external = getExternal(subgraphWithUtilities);
			subgraphWithUtilities = performMaximalLocalChange(subgraphWithUtilities, external);
			newUtility = super.sumMapByValue(subgraphWithUtilities);
		}
		while(newUtility > oldUtility);
			
		subgraph.clear();
		for(Integer nodeId : subgraphWithUtilities.keySet())
			subgraph.add(nodeId);
		
		return subgraph;
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
	private HashMap<Integer,Float> updateUtilities(HashMap<Integer,Float> currentMaxSubgraph, int nodeId)
	{
		Node currentNode = lattice.nodeList.get(nodeId);
		for(int childId : currentNode.child_list)
		{
			if(!currentMaxSubgraph.containsKey(childId)) continue;
			Double newUtility = super.calculateDistance(nodeId, childId);
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
	private HashMap<Integer, Float> updateExternal(HashMap<Integer, Float> currentFrontier, Integer parentNodeId)
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(lattice.maxSubgraph.contains(childId)) continue;
			double utility = super.calculateDistance(parentNodeId, childId);
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}

	/**
	 * Replacing one node in the subgraph with one node from the frontier
	 * 
	 * @param currentSubgraph, frontierNodesUtility
	 */
	private HashMap<Integer,Float> performMaximalLocalChange(HashMap<Integer,Float> currentSubgraph, HashMap<Integer,Float> externalNodes)
	{
		Float maximalUtility = super.sumMapByValue(currentSubgraph);
		int selectedExternalNodeId = -1;
		for(int externalNodeId : externalNodes.keySet())
		{
			HashMap<Integer,Float> tempMaxSubgraph = swapSingleNode(externalNodeId, externalNodes.get(externalNodeId), currentSubgraph);
			Float tempMaxUtility = super.sumMapByValue(tempMaxSubgraph);
			if( tempMaxUtility > maximalUtility)
			{
				maximalUtility = tempMaxUtility;
				currentSubgraph = tempMaxSubgraph;
				selectedExternalNodeId = externalNodeId;
			}
		}
		
		if(selectedExternalNodeId > 0)
			currentSubgraph = updateUtilities(currentSubgraph, selectedExternalNodeId);
		
		return currentSubgraph;
	}
	
	/**
	 * Adding a candidate node to a subgraph at the expense of removing another
	 * 
	 * @param candidateId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer, Float> swapSingleNode(int candidateId, Float candidateUtility, HashMap<Integer,Float> maxSubgraph)
	{
		Float maxUtility = super.sumMapByValue(maxSubgraph);
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(maxSubgraph);

		for(int nodeId : maxSubgraph.keySet())
		{
			HashMap<Integer,Float> tempSubgraph = replace2Nodes(nodeId, candidateId, candidateUtility, maxSubgraph);
			Float newUtility = super.sumMapByValue(tempSubgraph);
			if(newUtility > maxUtility)
			{
				maxUtility = newUtility;
				newMaxSubgraph = tempSubgraph;
			}
		}
		return newMaxSubgraph;
	}
	
	/**
	 * Adding one node, and deleting another. If it is not possible then the original 
	 * subgraph is returned.
	 * 
	 * @param outNodeId, inNodeId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer,Float> replace2Nodes(int outNodeId, int inNodeId, Float candidateUtility, HashMap<Integer,Float> originalMaxSubgraph)
	{
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(originalMaxSubgraph);
		newMaxSubgraph.put(inNodeId, candidateUtility);
		newMaxSubgraph.remove(outNodeId);
		
		// first, if the node has no children we can safely remove it
		ArrayList<Integer> childrenInSubgraph = new ArrayList<>();
		for(int childId : lattice.nodeList.get(outNodeId).child_list)
		{
			if(newMaxSubgraph.containsKey(childId))
				childrenInSubgraph.add(childId);
		}
		if(childrenInSubgraph.size() == 0) 
			return newMaxSubgraph;
		
		// if the node has children, then maybe these children have several parents;
		// so it will still be okay to remove the node
		for(int childId : childrenInSubgraph)
		{
			HashMap<Integer,Float> otherParents = new HashMap<>();
			for(int possibleParentId : newMaxSubgraph.keySet())
			{
				Node possibleParentNode = lattice.nodeList.get(possibleParentId);
				if(possibleParentNode.child_list.contains(childId))
				{
					double dist = super.calculateDistance(possibleParentId, childId);
					otherParents.put(possibleParentId, (float) dist);
				}
			}
			if(otherParents.isEmpty())
				return originalMaxSubgraph;
			
			Float newUtility = Collections.max(otherParents.entrySet(), Map.Entry.comparingByValue()).getValue();
			newMaxSubgraph.remove(childId);
			newMaxSubgraph.put(childId, newUtility);
		}
		return newMaxSubgraph;
	}

	/**
	 * Get a list of nodes that can be added given a current subgraph.
	 * The value in the map is the expected utility of adding this specific node.
	 * 
	 * @param localMaxSubgraph
	 */
	private HashMap<Integer,Float> getExternal(HashMap<Integer,Float> localMaxSubgraph)
	{
		HashMap<Integer,Float> externalNodesUtility = new HashMap<>();
		for(Map.Entry<Integer, Float> e : localMaxSubgraph.entrySet())
			externalNodesUtility = updateExternal(externalNodesUtility, e.getKey());
		for(Map.Entry<Integer, Float> e : localMaxSubgraph.entrySet())
			externalNodesUtility.remove(e.getKey());
		return externalNodesUtility;
	}
}