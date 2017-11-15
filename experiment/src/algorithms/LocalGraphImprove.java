package algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import distance.Distance;
import lattice.Lattice;
import lattice.Node;

public class LocalGraphImprove extends BreadthFirstPicking{
	
	public LocalGraphImprove(Lattice lattice, Distance metric)
	{
		super(lattice, metric);
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
		
		Float oldUtility = Traversal.sumMapByValue(subgraphWithUtilities);
		Float newUtility = oldUtility;
		
		do {
			oldUtility = newUtility;
			HashMap<Integer,Float> external = getExternal(subgraphWithUtilities);
			subgraphWithUtilities = performMaximalLocalChange(subgraphWithUtilities, external);
			newUtility = Traversal.sumMapByValue(subgraphWithUtilities);
		}
		while(newUtility > oldUtility);
			
		subgraph.clear();
		for(Integer nodeId : subgraphWithUtilities.keySet())
			subgraph.add(nodeId);
		
		return subgraph;
	}

	/**
	 * Replacing one node in the subgraph with one node from the frontier
	 * 
	 * @param currentSubgraph, frontierNodesUtility
	 */
	private HashMap<Integer,Float> performMaximalLocalChange(HashMap<Integer,Float> currentSubgraph, HashMap<Integer,Float> externalNodes)
	{
		Float maximalUtility = Traversal.sumMapByValue(currentSubgraph);
		int selectedExternalNodeId = -1;
		for(int externalNodeId : externalNodes.keySet())
		{
			HashMap<Integer,Float> tempMaxSubgraph = insertSingleNode(externalNodeId, externalNodes.get(externalNodeId), currentSubgraph);
			Float tempMaxUtility = Traversal.sumMapByValue(tempMaxSubgraph);
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
	private HashMap<Integer, Float> insertSingleNode(int candidateId, Float candidateUtility, HashMap<Integer,Float> maxSubgraph)
	{
		Float maxUtility = Traversal.sumMapByValue(maxSubgraph);
		HashMap<Integer,Float> newMaxSubgraph = Traversal.cloneMap(maxSubgraph);

		for(int nodeId : maxSubgraph.keySet())
		{
			HashMap<Integer,Float> tempSubgraph = swap2nodes(nodeId, candidateId, candidateUtility, maxSubgraph);
			Float newUtility = Traversal.sumMapByValue(tempSubgraph);
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
	private HashMap<Integer,Float> swap2nodes(int outNodeId, int inNodeId, Float candidateUtility, HashMap<Integer,Float> originalMaxSubgraph)
	{
		HashMap<Integer,Float> newMaxSubgraph = Traversal.cloneMap(originalMaxSubgraph);
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
					double dist = Traversal.calculateDistance(possibleParentId, childId, lattice, metric);
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
			externalNodesUtility = updateExternal(localMaxSubgraph, externalNodesUtility, e.getKey());
		for(Map.Entry<Integer, Float> e : localMaxSubgraph.entrySet())
			externalNodesUtility.remove(e.getKey());
		return externalNodesUtility;
	}
}
