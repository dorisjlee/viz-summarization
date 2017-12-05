package algorithms;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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
	static Traversal tr; 
	static Experiment exp;
	static Hierarchia h;
	public OnlineRandomWalk(Experiment exp) {
		super(exp, "Online Random Walk in Lattice");
		this.exp = exp;
		this.h = exp.h;
	}
	
	public void pickVisualizations(Integer k) {
	   System.out.println("---------------- Online Random Walk -----------------");
	   Lattice rwResult = onlineRW(k);
       //lattice.maxSubgraphUtility=computeSubGraphUtility(rwResult);
       //printMaxSubgraphSummary();
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
        // 	Stop when dashboard exceeds desired size k
        while (dashboard.size()<k && dashboard.size() < lattice.nodeList.size()) {
        		ArrayList<Integer> currentFrontier = RandomWalk.getFrontier(lattice,dashboard);
        		System.out.println("nodeList.size:"+lattice.nodeList.size());
        		System.out.println("dashboard:"+dashboard);
        		System.out.println("currentFrontier:"+currentFrontier);
        		// Pick one from the given current frontier
        		
        		int randInt =  r.nextInt(currentFrontier.size()-1);
        		int pickedNodeID = currentFrontier.get(randInt);
        		System.out.println("pickedNodeID:"+pickedNodeID);
        		Node pickedNode = lattice.nodeList.get(pickedNodeID);
        		ArrayList<Integer> parents = deriveParents(h, pickedNode, lattice.nodeList);
        		int informativeParentID = findInformativeParent(lattice,parents,pickedNode);
//        		if (informativeParentID!=-1) {
//        			dashboard.add(pickedNodeID);
//        			//Compute Utility
//        		}
        		dashboard.add(pickedNodeID);
        }
        lattice.maxSubgraph=dashboard;
        return lattice;
	}

	
	private static int findInformativeParent(Lattice lattice,ArrayList<Integer> parents,Node pickedNodeID) {
		System.out.println(tr.informative_critera);
		for (int i=0;i<parents.size();i++) {
			try {
				Experiment.computeVisualization(exp,lattice.nodeList.get(parents.get(i)).id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return 0;
	}

	private static ArrayList<Integer> deriveParents(Hierarchia h, Node node, ArrayList<Node> node_list) {
		//System.out.println("-------- Parents of: "+ node.id+"--------");
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
		    			//System.out.println(parent.id);
		    			node_list.add(parent);
		        		parents.add(node_list.size());
//		        		parents.add(parent);
		    		}
		    }
		}
		return parents;
	}

	private static ArrayList<Integer> deriveChildren(Hierarchia h, Node node, ArrayList<Node> node_list) {
		//System.out.println("-------- Children of: "+node.id+"--------");
		ArrayList<Integer> children = new ArrayList<Integer>();
		//System.out.println("uniqueAttributeKeyVals:"+h.uniqueAttributeKeyVals);
		//System.out.println("attribute names:"+h.getAttribute_names());
		//System.out.println("Removed xAxis:"+h.xAxis);
		h.uniqueAttributeKeyVals.remove(h.xAxis);// remove the xAxis item in the attribute list
		// Remove the existing attributes in the node
		for (String split_filter:node.id.split("#")) {
			if (split_filter.indexOf("$")!=-1) {
				String existing_attribute = split_filter.substring(0,split_filter.indexOf("$"));
				//System.out.println("Remove:"+existing_attribute);
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
	        		//System.out.println(node.id+pair.getKey()+"$"+val+"#");
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
    		//Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
    		Experiment exp = null;
		try {
			exp = new Experiment("mushroom","cap_surface","cap_surface",groupby, "COUNT", 10, "Online Random Walk", ed,0,0.8,true);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        tr = new OnlineRandomWalk(exp);
        tr.pickVisualizations(8);
        /*
        tr = new GreedyPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
        
        tr = new BreadthFirstPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
        */
    }
}
