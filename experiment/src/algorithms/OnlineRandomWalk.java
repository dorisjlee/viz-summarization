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
import lattice.Dashboard;
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
	}
	
	public void pickVisualizations(Experiment exp) {
	   System.out.println("---------------- Online Random Walk -----------------");
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   Dashboard rwResult = onlineRW(exp.k);
       rwResult.maxSubgraphUtility=rwResult.computeSubGraphUtility();
       //printMaxSubgraphSummary();
	}

	public static Dashboard onlineRW(Integer k) {
		Lattice lattice = new Lattice();
		
        Node root = new Node("#");
        ArrayList<Double> root_measure_values = Hierarchia.compute_visualization(root,new ArrayList<String>(),new ArrayList<String>(),exp.uniqueAttributeKeyVals,exp.attribute_names,exp.xAxisName,exp.datasetName);
        long rootSize = root.getPopulation_size();
        System.out.println("Root size:"+rootSize);
        double  min_iceberg_support = exp.iceberg_ratio*rootSize;
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
        		ArrayList<Integer> currentFrontier = getFrontierOnline(lattice,dashboard);
        		System.out.println("nodeList.size:"+lattice.nodeList.size());
        		System.out.println("dashboard:"+dashboard);
        		System.out.println("currentFrontier:"+currentFrontier);
        		// Pick one from the given current frontier
        		System.out.println("currentFrontier.size:"+currentFrontier.size());
        		int randInt;
        		if (currentFrontier.size()==1) {
        			System.out.println("here");
        			randInt=  0;
        		}else {
        			randInt =  r.nextInt(currentFrontier.size()-1);
        		}
        		int pickedNodeID = currentFrontier.get(randInt);
        		System.out.println("pickedNodeID:"+pickedNodeID);
        		System.out.println("lattice.nodeList:"+lattice.nodeList);
        		Node pickedNode = lattice.nodeList.get(pickedNodeID);
        		ArrayList<Integer> parents = deriveParents(lattice, pickedNode);
        		System.out.println("derivedParents:"+parents);
        		ArrayList<Integer> informativeParentID = findInformativeParent(lattice,parents,pickedNode);
        		if (informativeParentID.size() > 0) {
        			// If there are more than one informative parent
        			dashboard.add(pickedNodeID);
        			//Compute Utility
        		}else {
        			// If no informative parent, then abort.
        			System.out.println("Can not find informative parent");
        			continue;
        		}
        }
        Dashboard resultDashboard = new Dashboard(lattice);
        resultDashboard.maxSubgraph=dashboard;
        return resultDashboard;
	}
	public static ArrayList<Integer> getFrontierOnline(Lattice lattice,ArrayList<Integer> dashboard) {
		ArrayList<Integer> currentFrontier = new ArrayList<Integer>();
        System.out.println("Dashboard Size: "+dashboard.size());
        int next = -1;
        for(int i = 0; i < dashboard.size(); i++)
        {
             //System.out.println("Children of: "+lattice.nodeList.get(dashboard.get(i)).get_id());
     	       // Looping through all children indexes 
             
             int flag = 0;
             Integer currentNodeID = dashboard.get(i);
             Node currentNode = lattice.nodeList.get(currentNodeID);
             
             if (currentNode.child_list.size()==0) {
            	  	// If child list is empty then populate child_list of the node with derived list of children; 
            	 	ArrayList<Integer> children  = deriveChildren(lattice,currentNode);
             }
            for(int j = 0; j < currentNode.child_list.size(); j++)
            { 
                //System.out.println(j+"th Child: "+ lattice.nodeList.get(currentNode.get_child_list().get(j)).id);
                for(int sp = 0; sp < dashboard.size(); sp++)
                {
                    // Check if the node to be added is already in the dashboard 
                    if(lattice.nodeList.get(dashboard.get(i)).child_list.get(j).equals(dashboard.get(sp)))
                    {
                        //System.out.println("Already in");
                        flag =1;
                        break;
                    }
                }
                if(flag == 0)
                {
                    next = lattice.nodeList.get(dashboard.get(i)).child_list.get(j);
                    currentFrontier.add(next);
                }
            }
        }
		return currentFrontier;
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
				double dist = exp.dist.computeDistance(current_visualization_measure_values, parent_visualization_measure_values);
				dist_list.add(dist);
				if(dist < min_distance)
                    min_distance = dist;
			}

			
	        ArrayList<Double> ip_dist_list = new ArrayList<Double>();
	        
	        for (int i=0;i<parents.size();i++) {
	        		double dist = dist_list.get(i);
	            if(dist*exp.informative_critera <= min_distance)
	            {
	            		ip_dist_list.add(dist);
	            		informative_parents.add(parents.get(i));
                		//System.out.println("Informative parent: "+lattice.nodeList.get(parents.get(i))+" -- "+dist);
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
			String[] items = node.id.split("#");
			System.out.println("items:"+items.toString());
			System.out.println("substring:"+ node.id.substring(1).split("#"));
		    ArrayList<String> split_filters = new ArrayList<String>(Arrays.asList(items));
		    System.out.println("split_filters:"+split_filters);
		    if (split_filters.get(0).equals("")) {
		    		split_filters.remove(0);
		    }
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
			    			lattice.add2Lattice(parent, null, lattice.nodeList.size()-1);
			        		parents.add(lattice.nodeList.size()-1);
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
		exp.uniqueAttributeKeyVals.remove(exp.xAxisName);// remove the xAxis item in the attribute list
		// Remove the existing attributes in the node
		for (String split_filter:node.id.split("#")) {
			if (split_filter.indexOf("$")!=-1) {
				String existing_attribute = split_filter.substring(0,split_filter.indexOf("$"));
				//System.out.println("Remove:"+existing_attribute);
				exp.uniqueAttributeKeyVals.remove(existing_attribute);
			}
		} 
		Iterator it = exp.uniqueAttributeKeyVals.entrySet().iterator();
        int n = exp.attribute_names.size();
	    	while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        for (String val: (ArrayList<String>) pair.getValue()) {
	        		// A Child is the existing node values plus one additional filter.
	        		//System.out.println(node.id+pair.getKey()+"$"+val+"#");
	        		Node child = new Node(node.id+pair.getKey()+"$"+val+"#");
	        		lattice.add2Lattice(child, null, lattice.nodeList.size()-1);
	        		children.add(lattice.nodeList.size()-1);
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
        tr.pickVisualizations(exp);
		*/
		Euclidean ed = new Euclidean();
		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
		Experiment exp = null;
		tr = new OnlineRandomWalk();
		try {
			exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", 15,tr, ed,0,0.8,true);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    tr.pickVisualizations(exp);
	    
    }
}
