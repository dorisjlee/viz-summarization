package lattice;
import java.util.ArrayList;
import java.util.HashMap;

public class Lattice {
	public ArrayList<Integer> maxSubgraph;
	public double maxSubgraphUtility;
	public HashMap<String, ArrayList<Double>>  id2MetricMap;
	public ArrayList<Node> nodeList;
	public HashMap<String, Integer> id2IDMap;
	public Lattice(HashMap<String, ArrayList<Double>> id2MetricMap,  
			ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap) {
		this.id2MetricMap = id2MetricMap;
		this.nodeList = nodeList;
		this.id2IDMap =id2IDMap;
		this.maxSubgraph =new ArrayList<Integer>();
		this.maxSubgraphUtility=0;
	}
	public Lattice() {
		// Simply a container for subgraph (used in RW)
		this.maxSubgraph =new ArrayList<Integer>();
		this.maxSubgraphUtility=0;
	}
}
