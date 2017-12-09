package lattice;
import java.util.ArrayList;
import java.util.HashMap;

public class Lattice {
	/*
	 * id: string made up of filters 
	 */
	public HashMap<String, ArrayList<Double>>  id2MetricMap;
	public ArrayList<Node> nodeList;
	public HashMap<String, Integer> id2IDMap;
	public Lattice(HashMap<String, ArrayList<Double>> id2MetricMap,  
			ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap) {
		this.id2MetricMap = id2MetricMap;
		this.nodeList = nodeList;
		this.id2IDMap =id2IDMap;
	}
	public Lattice() {
		this.id2MetricMap= new HashMap<String, ArrayList<Double>>();
		this.nodeList = new ArrayList<Node>();       
		this.id2IDMap = new HashMap<String, Integer>();
	}
	public void add2Lattice(Node node, ArrayList<Double> measure_values, Integer ID) {
		nodeList.add(node);
		id2MetricMap.put(node.id,measure_values);
        id2IDMap.put(node.id,ID);
	}
}
