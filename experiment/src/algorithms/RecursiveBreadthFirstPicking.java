package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import distance.Distance;
import lattice.Lattice;

public class RecursiveBreadthFirstPicking extends LookAheadPicking
{
	private Integer numSteps;
	
	public RecursiveBreadthFirstPicking(Lattice lattice, Distance metric, Integer numSteps) {
		super(lattice, metric, "Recursive Breadth First Picking");
		this.numSteps = numSteps;
	}

	@Override
	protected HashMap<Integer, Float> updateExternal(ArrayList<Integer> localMaxSubgraph,
			HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k) 
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.contains(childId)) continue;
			double edgeUtility = super.calculateDistance(parentNodeId, childId, lattice, metric);
			BreadthFirstPicking bfp = new BreadthFirstPicking(lattice, metric);
			HashMap<Integer, Float> lookAheadSubgraph = bfp.pickVisualizations(Math.min(numSteps, k-localMaxSubgraph.size()), childId);
			double bfsUtility = Traversal.sumMapByValue(lookAheadSubgraph);
			double utility = edgeUtility + bfsUtility;
			
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}
}
