package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import distance.Distance;
import lattice.Lattice;

public class TwoStepLookAheadalgorithm extends LookAheadPicking{

	public TwoStepLookAheadalgorithm(Lattice lattice, Distance metric) {
		super(lattice, metric, "Two Step Look Ahead Algorithm");
	}

	@Override
	protected HashMap<Integer, Float> updateExternal(ArrayList<Integer> localMaxSubgraph,
			HashMap<Integer, Float> currentFrontier, Integer parentNodeId) {
		// TODO Auto-generated method stub
		return null;
	}

}
