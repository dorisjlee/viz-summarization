package algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class OnlineRandomWalk extends Traversal{
	static Hierarchia h;
	public OnlineRandomWalk(Hierarchia h, Distance metric, double iceberg_ratio, double informative_criteria) {
		super(metric, iceberg_ratio,informative_criteria, "Online Random Walk in Lattice");
		this.h = h;
	}
	
	public void pickVisualizations(Integer k) {
	   System.out.println("---------------- Online Random Walk -----------------");
       Lattice rwResult = onlineRW(k);
       lattice.maxSubgraph= rwResult.maxSubgraph; 
       lattice.maxSubgraphUtility=rwResult.maxSubgraphUtility;
       printMaxSubgraphSummary();
   }

	public static Lattice onlineRW(Integer k) {
		Lattice lattice = new Lattice();
		HashMap<String, ArrayList<Double>> map_id_to_metric_values = new HashMap<String, ArrayList<Double>>();
        lattice.nodeList = new ArrayList<Node>();       
        HashMap<String, Integer> map_id_to_index = new HashMap<String, Integer>();
        Node root = new Node("#");
        ArrayList<Double> root_measure_values = h.compute_visualization(root,new ArrayList<String>(),new ArrayList<String>());
        long rootSize = root.getPopulation_size();
        System.out.println("Root size:"+rootSize);
        double  min_iceberg_support = iceberg_ratio*rootSize;
		System.out.println("Minimum Iceberg support:"+min_iceberg_support);
        map_id_to_metric_values.put("#", root_measure_values);
        lattice.nodeList.add(root);
        map_id_to_index.put("#", 0);
         
        //At the root level, the current frontier is all children, pick random children from there
		double total_utility =0;
        ArrayList<Integer> children = deriveChildren(h,root,lattice.nodeList);
        ArrayList<Integer> dashboard = new ArrayList<Integer>();
        dashboard.add(0); // Adding root
        Random r = new Random(System.currentTimeMillis());
        int myRandomNumber = r.nextInt(children.size());
        dashboard.add(children.get(myRandomNumber));
        System.out.println(dashboard);
        // 	Stop when dashboard exceeds desired size k
        while (dashboard.size()<k) {
        		ArrayList<Integer> currentFrontier = RandomWalk.getFrontier(lattice,dashboard);
        		System.out.println(currentFrontier);
        		// Pick one from the given current frontier
        		int randInt =  r.nextInt(currentFrontier.size());
        		dashboard.add(currentFrontier.get(randInt));
        }
        
        Lattice rwResult = new Lattice();
        rwResult.maxSubgraph= dashboard; 
        rwResult.maxSubgraphUtility=total_utility;
        return rwResult;
	}

	
	private static ArrayList<Integer> deriveParents(Hierarchia h, Node node, ArrayList<Node> node_list) {
		System.out.println("-------- Parents of: "+ node.id+"--------");
		ArrayList<Integer> parents = new ArrayList<Integer>();
		if (node.equals("#")) {
			// Root has no parents 
			return parents;
		}else {
			String[] items = node.id.substring(1).split("#");
		    ArrayList<String> split_filters = new ArrayList<String>(Arrays.asList(items));
		    for (int i=1;i<split_filters.size();i++) {
		    		ArrayList<ArrayList<String>> combo = Hierarchia.combination(split_filters, i);
		    		// Loop through the generated i-item combination and save as parent
		    		for (int j =0;j<combo.size();j++) {
		    			Node parent = new Node(String.join("#",combo.get(j)));
		    			System.out.println(parent.id);
		    			node_list.add(parent);
		        		parents.add(node_list.size());
//		        		parents.add(parent);
		    		}
		    }
		}
		return parents;
	}

	private static ArrayList<Integer> deriveChildren(Hierarchia h, Node node, ArrayList<Node> node_list) {
		System.out.println("-------- Children of: "+node.id+"--------");
		ArrayList<Integer> children = new ArrayList<Integer>();
		System.out.println("uniqueAttributeKeyVals:"+h.uniqueAttributeKeyVals);
		System.out.println("attribute names:"+h.getAttribute_names());
		System.out.println("Removed xAxis:"+h.xAxis);
		h.uniqueAttributeKeyVals.remove(h.xAxis);// remove the xAxis item in the attribute list
		// Remove the existing attributes in the node
		for (String split_filter:node.id.split("#")) {
			if (split_filter.indexOf("$")!=-1) {
				String existing_attribute = split_filter.substring(0,split_filter.indexOf("$"));
				System.out.println("Remove:"+existing_attribute);
				h.uniqueAttributeKeyVals.remove(existing_attribute);
			}
		}
		//combinable_attributes.remove(o)
		Iterator it = h.uniqueAttributeKeyVals.entrySet().iterator();
        int n = h.attribute_names.size();
	    	while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        for (String val: (ArrayList<String>) pair.getValue()) {
	        		// A Child is the existing node values plus one additional filter.
	        		System.out.println(node.id+pair.getKey()+"$"+val+"#");
	        		Node child = new Node(node.id+pair.getKey()+"$"+val+"#");
	        		node_list.add(child);
	        		children.add(node_list.size());
	        }
	    }
	    	node.set_child_list(children);
		return children;
	}

	public static void main (String[] args) throws SQLException {
	    // Testing
		//ArrayList<Node> parents = deriveParents(h,new Node("#cap_color$b#cap_shape$x#type$e#"));
        //ArrayList<Node> children = deriveChildren(h,new Node("#cap_color$b#cap_shape$x#"));
    		Euclidean ed = new Euclidean();
    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
    		//Hierarchia h = new Hierarchia("titanic","survived");
        Traversal tr; 
        tr = new OnlineRandomWalk(h,ed,0.001,0.8);
        tr.pickVisualizations(8);
        /*
        tr = new GreedyPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
        
        tr = new BreadthFirstPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
        */
    }
}
