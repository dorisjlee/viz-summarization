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
public class FrontierGreedyPicking extends Traversal{

	public FrontierGreedyPicking(Lattice lattice, Distance metric) {
		super(lattice, metric, "Frontier Greedy Picking");
	}

	public void pickVisualizations(Integer k)
	{
		super.printAlgoName();
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
}
