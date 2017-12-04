package algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
		HashMap<String, ArrayList<Double>> map_id_to_metric_values = new HashMap<String, ArrayList<Double>>();
        ArrayList<Node> node_list = new ArrayList<Node>();       
        HashMap<String, Integer> map_id_to_index = new HashMap<String, Integer>();
        Node root = new Node("#");
        ArrayList<Double> root_measure_values = h.compute_visualization(root,new ArrayList<String>(),new ArrayList<String>());
        long rootSize = root.getPopulation_size();
        System.out.println("Root size:"+rootSize);
        double  min_iceberg_support = iceberg_ratio*rootSize;
		System.out.println("Minimum Iceberg support:"+min_iceberg_support);
        map_id_to_metric_values.put("#", root_measure_values);
        node_list.add(root);
        map_id_to_index.put("#", 0);
        /*
        ArrayList <String> attribute_combination = new ArrayList<String>(); // List of attribute combination that excludes the xAxis item
        attribute_combination.addAll(h.attribute_names); // Deep copy original attribute names
        attribute_combination.remove(h.xAxis); // remove the xAxis item in the attribute list
        int n = h.attribute_names.size();
        */
        // how to get_child_list in an online manner? 
        //ArrayList<Node> children = deriveChildren(h,root);
        ArrayList<Node> children = deriveChildren(h,new Node("#cap_color$b#cap_shape$x#"));
        //ArrayList<Node> parents = deriveParents(h,root);
        ArrayList<Node> parents = deriveParents(h,new Node("#cap_color$b#cap_shape$x#type$e#"));
        /*
        Lattice lattice = new Lattice();
		double total_utility =0;
        ArrayList<Integer> dashboard = new ArrayList<Integer>();
        dashboard.add(0); // Adding root
	    // Stop when dashboard exceeds desired size k 
        while(dashboard.size()<k ) // can not ensure this: //&& dashboard.size() < lattice.nodeList.size())
        {	
       	   ArrayList<Integer> currentFrontier = new ArrayList<Integer>();
           //System.out.println("Dashboard Size: "+dashboard.size());
           int next = -1;
           for(int i = 0; i < dashboard.size(); i++)
           {
               //System.out.println("Children of: "+node_list.get(dashboard.get(i)).get_id());
        	       // Looping through all children indexes 
               for(int j = 0; j < lattice.nodeList.get(dashboard.get(i)).get_dist_list().size(); j++)
               { 
                   int flag = 0;
                   //System.out.println("Current Node: "+node_list.get(dashboard.get(i)).get_child_list().get(j));
                   for(int sp = 0; sp < dashboard.size(); sp++)
                   {
                       // Check if the node to be added is already in the dashboard 
                       if(lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j).equals(dashboard.get(sp)))
                       {
                           //System.out.println("Already in");
                           flag =1;
                           break;
                       }
                   }
                   if(flag == 0)
                   {
                       next = lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j);
                       currentFrontier.add(next);
                   }
               }
           }
           Random r = new Random(System.currentTimeMillis());
           int myRandomNumber = 0;
           myRandomNumber = r.nextInt(currentFrontier.size());
           dashboard.add(currentFrontier.get(myRandomNumber));
       }
       Lattice rwResult = new Lattice();
       rwResult.maxSubgraph= dashboard; 
       rwResult.maxSubgraphUtility=total_utility;
       return rwResult;
       */
       Lattice rwResult = new Lattice();
       return rwResult;
	}

	private static ArrayList<Node> deriveParents(Hierarchia h, Node node) {
		System.out.println("-------- Parents of: "+ node.id+"--------");
		ArrayList<Node> parents = new ArrayList<Node>();
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
		        		parents.add(parent);
		    		}
		    }
		}
		return parents;
	}

	private static ArrayList<Node> deriveChildren(Hierarchia h, Node node) {
		System.out.println("-------- Children of: "+node.id+"--------");
		ArrayList<Node> children = new ArrayList<Node>();
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
	        		children.add(child);
	        }
	    }
	    	//node.set_child_list(children); // ArrayList<Integer> not ArrayList<Node>
		return children;
	}

	public static void main (String[] args) throws SQLException {
	    /*    
	    	ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
		int r = 3;
        combination(pivot_children, r);
        */
    		Euclidean ed = new Euclidean();
    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
    		//Hierarchia h = new Hierarchia("titanic","survived");
    		//Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
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
