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
	public OnlineRandomWalk() {
		super("Online Random Walk in Lattice");
		this.exp = exp;
	}
	
	public void pickVisualizations(Experiment exp,Integer k) {
	   System.out.println("---------------- Online Random Walk -----------------");
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   this.h = exp.h;
	   Lattice rwResult = onlineRW(k);
       //lattice.maxSubgraphUtility=computeSubGraphUtility(rwResult);
       //printMaxSubgraphSummary();
	}

	public static Lattice onlineRW(Integer k) {
		Lattice lattice = new Lattice();
		lattice.id2MetricMap= new HashMap<String, ArrayList<Double>>();
        lattice.nodeList = new ArrayList<Node>();       
        lattice.id2IDMap = new HashMap<String, Integer>();
        Node root = new Node("#");
        ArrayList<Double> root_measure_values = h.compute_visualization(root,new ArrayList<String>(),new ArrayList<String>());
        long rootSize = root.getPopulation_size();
        System.out.println("Root size:"+rootSize);
        double  min_iceberg_support = iceberg_ratio*rootSize;
		System.out.println("Minimum Iceberg support:"+min_iceberg_support);
		lattice.add2Lattice(root, root_measure_values, 0);
        //At the root level, the current frontier is all children, pick random children from there
		double total_utility =0;
        ArrayList<Integer> children = deriveChildren(lattice,root);
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
        		ArrayList<Integer> parents = deriveParents(lattice, pickedNode);
        		System.out.println("derivedParents:"+parents);
        		ArrayList<Integer> informativeParentID = findInformativeParent(lattice,parents,pickedNode);
//        		if (informativeParentID!=-1) {
//        			dashboard.add(pickedNodeID);
//        			//Compute Utility
//        		}
        		dashboard.add(pickedNodeID);
        }
        lattice.maxSubgraph=dashboard;
        return lattice;
	}

	
	private static ArrayList<Integer> findInformativeParent(Lattice lattice,ArrayList<Integer> parents,Node pickedNode) {
		System.out.println("parents:"+parents);
		ArrayList<Integer> informative_parents = new ArrayList<Integer> ();
		if (parents.size()==1 && parents.get(0)==0) {
			// The only parent is root, it has to be informative;
			informative_parents.add(0);
			return informative_parents;
		}
		
		double min_distance = 1000000;
		// Make one pass over all potential parents to find min distance
		ArrayList<Double> dist_list = new ArrayList<Double>();
		try { 
			ArrayList<Double> current_visualization_measure_values = 
					Experiment.computeVisualization(exp,pickedNode.id);
			for (int i=0;i<parents.size();i++) {
				ArrayList<Double> parent_visualization_measure_values  = 
					Experiment.computeVisualization(exp,lattice.nodeList.get(parents.get(i)).id);
				double dist = tr.metric.computeDistance(current_visualization_measure_values, parent_visualization_measure_values);
				System.out.println("dist:"+dist);
				dist_list.add(dist);
				if(dist < min_distance)
                    min_distance = dist;
			}
	        System.out.println("min_distance:"+min_distance);
			
	        ArrayList<Double> ip_dist_list = new ArrayList<Double>();
	        
	        for (int i=0;i<parents.size();i++) {
	        		double dist = dist_list.get(i);
	            if(dist*tr.informative_critera <= min_distance)
	            {
	            		ip_dist_list.add(dist);
	            		informative_parents.add(parents.get(i));
                		System.out.println("Informative parent: "+lattice.nodeList.get(parents.get(i))+" -- "+dist);
	            }
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return informative_parents;
	}

	private static ArrayList<Integer> deriveParents(Lattice lattice , Node node ) {
		//System.out.println("-------- Parents of: "+ node.id+"--------");
		ArrayList<Integer> parents = new ArrayList<Integer>();
		System.out.println("node:"+node.id);
		if (node.id.equals("#")) {
			// Root has no parents 
			return parents;
		}else{
			String[] items = node.id.substring(1).split("#");
		    ArrayList<String> split_filters = new ArrayList<String>(Arrays.asList(items));
		    System.out.println("split_filters:"+split_filters);
		    if (split_filters.size()==1) {
		    		parents.add(0); // root is parent for all one-filter combos
		    }else {
			    for (int i=1;i<split_filters.size();i++) {
			    		ArrayList<ArrayList<String>> combo = Hierarchia.combination(split_filters, i);
			    		System.out.println("combo:"+combo);
			    		// Loop through the generated i-item combination and save as parent
			    		for (int j =0;j<combo.size();j++) {
			    			Node parent = new Node(String.join("#",combo.get(j)));
			    			System.out.println(parent.id); 
			    			lattice.add2Lattice(parent, null, lattice.nodeList.size());
			        		parents.add(lattice.nodeList.size());
			    		}
			    }
		    }
		}
		return parents;
	}

	private static ArrayList<Integer> deriveChildren(Lattice lattice, Node node) {
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
	        		lattice.add2Lattice(child, null, lattice.nodeList.size());
	        		children.add(lattice.nodeList.size());
	        }
	    }
	    	node.set_child_list(children);
		return children;
	}

	public static void main (String[] args) throws SQLException {
	    // Testing
		//ArrayList<Node> parents = deriveParents(h,new Node("#cap_color$b#cap_shape$x#type$e#"));
        //ArrayList<Node> children = deriveChildren(h,new Node("#cap_color$b#cap_shape$x#"));
		/*
    		Euclidean ed = new Euclidean();
    		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
    		Experiment exp = null;
    		tr = new OnlineRandomWalk();
		try {
			exp = new Experiment("mushroom","cap_surface","cap_surface",groupby, "COUNT", 10, tr, ed,0,0.8,true);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        tr.pickVisualizations(exp,8);*/
		Euclidean ed = new Euclidean();
		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
		Experiment exp = null;
		tr = new OnlineRandomWalk();
		try {
			exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", 10,tr, ed,0,0.8,true);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    tr.pickVisualizations(exp,8);
    }
}
