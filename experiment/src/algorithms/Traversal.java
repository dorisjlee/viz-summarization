package algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;

/**
 * Traversal algorithms take in a HashMap representing the materialized graph 
 * and return a presumably maximal subgraph (list of node indices) 
 */
public abstract class Traversal {

	Lattice lattice;
	Distance metric;
	String algoName;
	static double iceberg_ratio;
	static double informative_critera;
	public Traversal(Lattice lattice,Distance metric, String algoName) 
	{
		this.lattice = lattice;
		this.metric = metric;
		this.algoName = algoName;
		this.lattice.maxSubgraph.clear();
		this.lattice.maxSubgraphUtility = 0;
	}
	
	public Traversal(Distance metric, double iceberg_ratio, double informative_criteria, String algoName) 
	{
		//Online Traversal Overridden method
		this.lattice = new Lattice();
		this.iceberg_ratio = iceberg_ratio;
		this.informative_critera = informative_criteria;
		this.metric = metric;
		this.algoName = algoName;
		this.lattice.maxSubgraphUtility = 0;
	}
	
	public String getAlgoName() {
		return algoName;
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}
	
	public void printAlgoName()
	{
		System.out.println("---------------- " + algoName +  " -----------------");
	}
	
	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
	}
	
	public abstract void pickVisualizations(Integer k);

	public void printMaxSubgraphSummary() {
		// Summary of maximum subgraph 
		System.out.print("Max Subgraph: [");
		for (int j =0 ; j< lattice.maxSubgraph.size();j++) {
			if (j==lattice.maxSubgraph.size()-1) {
				System.out.print(Integer.toString(lattice.maxSubgraph.get(j))+"]\n");
			}else {
				System.out.print(Integer.toString(lattice.maxSubgraph.get(j))+",");
			}
		}
		
		
		System.out.print("[");
		for (int j =0 ; j< lattice.maxSubgraph.size();j++) {
			Node n = lattice.nodeList.get(lattice.maxSubgraph.get(j));
			ArrayList<Double> dist = lattice.id2MetricMap.get(n.get_id());
			/*
			// Printing Value of Each Node
			if (j==lattice.maxSubgraph.size()-1) {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+ dist + "]\n");
			}else {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+ dist +",");
			}*/
			if (j==lattice.maxSubgraph.size()-1) {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+"]\n");
			}else {
				System.out.print(lattice.nodeList.get(lattice.maxSubgraph.get(j)).get_id()+",");
			}
		}
		updateSubGraphUtility();
		System.out.println("Total Utility:"+Double.toString(lattice.maxSubgraphUtility));
	}
	
	/**
	 * 
	 * Provides a unified way for computing utility of a subgraph
	 * to be used in the different algorithms. Each node might have several
	 * informative parents - in such case we add the maximal interestingness to 
	 * the overall utility.
	 * 
	 */
	protected void updateSubGraphUtility() 
	{
		lattice.maxSubgraphUtility =  computeSubGraphUtility(lattice.maxSubgraph);
	}
public static double computeSubGraphUtility(Lattice lattice) {
	    ArrayList<Integer> subgraph = lattice.maxSubgraph;
		double maxSubgraphUtility = 0;
		HashMap<Integer,Float> nodeID2utility = new HashMap<>();
		for(int nodeId : subgraph)
			nodeID2utility.put(nodeId, 0f);
		
		for(int i : nodeID2utility.keySet())
		{	
			Node currentNode = lattice.nodeList.get(i);
			for(int j = 0; j < currentNode.child_list.size(); j++)
			{
				int childId = currentNode.child_list.get(j);
				if(nodeID2utility.containsKey(childId))
				{
					double edgeWeight = currentNode.dist_list.get(j);
					Float currentUtility = nodeID2utility.get(childId); 
					nodeID2utility.put(childId, (float) Math.max(currentUtility, edgeWeight));
				}
			}
		}
		
		for(int nodeId : nodeID2utility.keySet())
			maxSubgraphUtility += nodeID2utility.get(nodeId);
		return maxSubgraphUtility;
	}
	public double computeSubGraphUtility(ArrayList<Integer> subgraph) {
		
		double maxSubgraphUtility = 0;
		HashMap<Integer,Float> nodeID2utility = new HashMap<>();
		for(int nodeId : subgraph)
			nodeID2utility.put(nodeId, 0f);
		
		for(int i : nodeID2utility.keySet())
		{	
			Node currentNode = lattice.nodeList.get(i);
			for(int j = 0; j < currentNode.child_list.size(); j++)
			{
				int childId = currentNode.child_list.get(j);
				if(nodeID2utility.containsKey(childId))
				{
					double edgeWeight = currentNode.dist_list.get(j);
					Float currentUtility = nodeID2utility.get(childId); 
					nodeID2utility.put(childId, (float) Math.max(currentUtility, edgeWeight));
				}
			}
		}
		
		for(int nodeId : nodeID2utility.keySet())
			maxSubgraphUtility += nodeID2utility.get(nodeId);
		return maxSubgraphUtility;
	}
	/**
	 * Calculate interestingness score between parent and child nodes
	 */
	public static double calculateDistance(int nodeId1, int nodeId2, Lattice l, Distance m)
	{
		Node node1 = l.nodeList.get(nodeId1);
		Node node2 = l.nodeList.get(nodeId2);
		double[] node1Val = ArrayList2Array(l.id2MetricMap.get(node1.get_id()));
		double[] node2Val = ArrayList2Array(l.id2MetricMap.get(node2.get_id()));
		double utility = m.computeDistance(node1Val, node2Val);
		return utility;
	}
	
	public static Float sumMapByValue(HashMap<Integer,Float> map)
	{
		Float sum = 0f;
		for(Float val : map.values())
		{
			sum += val;
		}
		return sum;
	}
	
	public static HashMap<Integer,Float> cloneMap(HashMap<Integer,Float> inputMap)
	{
		HashMap<Integer,Float> outputMap = new HashMap<>();
		for(Map.Entry<Integer, Float> entry : inputMap.entrySet())
		{
			outputMap.put(entry.getKey(), entry.getValue());
		}
		return outputMap;
	}
	
	public static ArrayList<Integer> getKeysList(HashMap<Integer, Float> inputMap)
	{
		ArrayList<Integer> outputList = new ArrayList<>();
		for(Integer key : inputMap.keySet())
		{
			outputList.add(key);
		}
		return outputList;
	}
	
 	public static void main(String[] args) throws SQLException 
    {
	   String[] datasets = {"turn", "titanic", "mushroom"};
	   String[] xAxis = {"has_list_fn", "pc_class", "type"};
	   int dataset_id = 0;
	   int k = 20;
	   
	   Euclidean ed = new Euclidean();
	   Hierarchia h = new Hierarchia(datasets[dataset_id], xAxis[dataset_id]);
	   Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
       //Hierarchia.print_map(lattice.id2MetricMap);
       //Hierarchia.print_map(lattice.id2IDMap);
	   
       Traversal tr; 
       //tr = new NaiveGreedyPicking(lattice,new Euclidean());
       tr = new BreadthFirstPicking(lattice,new Euclidean());
       tr.pickVisualizations(k);
       
       tr = new TwoStepLookAheadalgorithm(lattice,new Euclidean(), "max");
       tr.pickVisualizations(k);
       
       tr = new TwoStepLookAheadalgorithm(lattice,new Euclidean(), "sum");
       tr.pickVisualizations(k);
       
       tr = new RecursiveBreadthFirstPicking(lattice, new Euclidean(), 2);
       tr.pickVisualizations(k);
       
       tr = new RecursiveNaiveGreedyPicking(lattice, new Euclidean(), 2);
       tr.pickVisualizations(k);
       
       tr = new NaiveGreedyPicking(lattice,new Euclidean());
       tr.pickVisualizations(k);
       
       //Hierarchia.print_map(lattice.id2MetricMap);
       //Hierarchia.print_map(lattice.id2IDMap);
       //Hierarchia.mergeNodes(lattice); 
    }
}
