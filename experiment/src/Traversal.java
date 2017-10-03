import java.util.ArrayList;
import java.util.HashMap;

public class Traversal {
	// Traversal methods take in a hashmap representing the materialized graph and return a subgraph 
	public static void main(String[] args) {
		System.out.print("Hello world");
		HashMap<String, ArrayList<Double>>  hmap = Hierarchia.computeVisualizationMap();
		Hierarchia.print_map(hmap);
		Euclidean ed = new Euclidean();
		greedyPicking(hmap,ed);
	}
	public static void greedyPicking(HashMap<String, ArrayList<Double>>  hmap, Distance metric) {
		// Start from root (nodeID=0)
		// Find children, compute utility, pick best children, repeat
		double [] parentVals = hmap.get("A$1#C$0#D$1#E$0#G$1#");
		double [] childVals = hmap.get("A$1#C$0#D$1#E$0#G$1#");
		double utility = metric.computeDistance(parentVals, childVals);
		// base case if no children, then end
	}
	
	
	
}
