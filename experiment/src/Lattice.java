import java.util.ArrayList;
import java.util.HashMap;

public class Lattice {
	ArrayList<Integer> maxSubgraph =new ArrayList<Integer>();
	double maxSubgraphUtility=0;
	HashMap<String, ArrayList<Double>>  id2MetricMap;
	ArrayList<Node> nodeList;
	HashMap<String, Integer> id2IDMap;
	public Lattice(HashMap<String, ArrayList<Double>> id2MetricMap,  
			ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap) {
		this.id2MetricMap = id2MetricMap;
		this.nodeList = nodeList;
		this.id2IDMap =id2IDMap;
		this.maxSubgraph = null;
		this.maxSubgraphUtility=0;
	}
}
