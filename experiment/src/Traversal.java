import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Traversal algorithms take in a HashMap representing the materialized graph 
 * and return a presumably maximal subgraph (list of node indices) 
 */
public abstract class Traversal {

	Lattice lattice;
	Distance metric;
	String algoName;
	
	public Traversal(Lattice lattice,Distance metric, String algoName) 
	{
		this.lattice = lattice;
		this.metric = metric;
		this.algoName = algoName;
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
	 * @author saarkuzi
	 */
	private void updateSubGraphUtility() 
	{
		lattice.maxSubgraphUtility = 0;
		
		HashMap<Integer,Float> nodeID2utility = new HashMap<>();
		for(int nodeId : lattice.maxSubgraph)
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
			lattice.maxSubgraphUtility += nodeID2utility.get(nodeId);
	}
	
	public static void main(String[] args) throws SQLException 
    {
	   String[] datasets = {"turn", "titanic", "mushroom"};
	   String[] xAxis = {"has_list_fn", "pc_class", "type"};
	   int dataset_id = 0;
	   
	   Euclidean ed = new Euclidean();
	   Hierarchia h = new Hierarchia(datasets[dataset_id], xAxis[dataset_id]);
	   Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
	   
       Traversal tr; 
       //tr = new NaiveGreedyPicking(lattice,new Euclidean());
       tr = new FrontierGreedyPicking(lattice,new Euclidean());
       //tr = new GreedyPicking(lattice,new Euclidean());
       tr.pickVisualizations(20);
       //Hierarchia.print_map(lattice.id2MetricMap);
       //Hierarchia.print_map(lattice.id2IDMap);
       //Hierarchia.mergeNodes(lattice); 
    }
}
