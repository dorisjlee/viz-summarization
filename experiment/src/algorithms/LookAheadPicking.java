package algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import distance.Distance;
import lattice.Lattice;


/**
 * In each iteration we pick a node with the "highest utility" from a set of "frontier" nodes.
 * The "highest utility" should be determined by a specific instantiation
 * 
 * @param k
 * @author saarkuzi
 */
public abstract class LookAheadPicking extends Traversal{

	public LookAheadPicking(Lattice lattice, Distance metric, String algoName) 
	{
		super(lattice, metric, algoName);
	}

	@Override
	public void pickVisualizations(Integer k) 
	{
		super.printAlgoName();
	    
		//a map in which keys are node IDs, and values are utilities (=interestingness)
		ArrayList<Integer> localMaxSubgraph = new ArrayList<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.add(rootId);
		HashMap<Integer,Float> externalNodesUtility = updateExternal(localMaxSubgraph, new HashMap<>(), rootId);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(externalNodesUtility.size() == 0) break;
			Integer selectedNodeID = Collections.max(externalNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			localMaxSubgraph.add(selectedNodeID);
			externalNodesUtility = updateExternal(localMaxSubgraph, externalNodesUtility, selectedNodeID);
		}			
		
		// improve the current solution by doing local changes
		LocalGraphImprove lgi = new LocalGraphImprove(lattice, metric);
		localMaxSubgraph = lgi.improveSubgraphLocally(localMaxSubgraph);
		
		// updated the final solution
		for(int nodeId : localMaxSubgraph)
			lattice.maxSubgraph.add(nodeId);
		
		super.updateSubGraphUtility();
		printMaxSubgraphSummary();	
	}
		
	/**
	 * Adding nodes to a frontier; the added nodes are the children of 
	 * a given parent node. A specific algorithm needs to decide what is the utility of
	 * the frontier node
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	protected abstract HashMap<Integer, Float> updateExternal(ArrayList<Integer> localMaxSubgraph, HashMap<Integer, Float> currentFrontier, Integer parentNodeId);
}
