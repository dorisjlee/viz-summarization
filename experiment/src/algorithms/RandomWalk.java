package algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class RandomWalk extends Traversal{
	
	public RandomWalk() {
		super("Random Walk in Lattice");
	}
	
	public void pickVisualizations(Experiment exp,Integer k) {
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   System.out.println("---------------- Random Walk -----------------");
	   ArrayList<Integer> rwResult = randomWalk(lattice,k);
       lattice.maxSubgraph= rwResult; 
       lattice.maxSubgraphUtility=computeSubGraphUtility(rwResult);
       printMaxSubgraphSummary();
   }
	
	public static ArrayList<Integer> randomWalk(Lattice lattice,Integer k) {
	       ArrayList<Integer> dashboard = new ArrayList<Integer>();
	       dashboard.add(0); // Adding root
	       // Stop when dashboard exceeds desired size k 
	       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
	       {	
	    	   	   ArrayList<Integer> currentFrontier = RandomWalk.getFrontier(lattice,dashboard);
	           Random r = new Random(System.currentTimeMillis());
	           int myRandomNumber = 0;
	           if (currentFrontier.size()>0) {
	        	   	   myRandomNumber = r.nextInt(currentFrontier.size());
		           dashboard.add(currentFrontier.get(myRandomNumber));
	           }else {
	        	   	   break;
	           }
	       }
	       return dashboard;
	}
	public static ArrayList<Integer> getFrontier(Lattice lattice,ArrayList<Integer> dashboard) {
		ArrayList<Integer> currentFrontier = new ArrayList<Integer>();
        //System.out.println("Dashboard Size: "+dashboard.size());
        int next = -1;
        for(int i = 0; i < dashboard.size(); i++)
        {
            //System.out.println("Children of: "+node_list.get(dashboard.get(i)).get_id());
     	       // Looping through all children indexes 
            for(int j = 0; j < lattice.nodeList.get(dashboard.get(i)).child_list.size(); j++)
            { 
                int flag = 0;
                //System.out.println("Current Node: "+node_list.get(dashboard.get(i)).get_child_list().get(j));
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

	public static void main (String[] args) throws SQLException {
		    /*    
		    	ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
			int r = 3;
	        combination(pivot_children, r);
	        */
			/*
	    		Euclidean ed = new Euclidean();
	    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
	    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
	    		//Hierarchia h = new Hierarchia("titanic","survived");
	    		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
	        Traversal tr; 
	        tr = new RandomWalk(lattice,new Euclidean());
	        tr.pickVisualizations(8);
	        
	        tr = new GreedyPicking(lattice,new Euclidean());
	        tr.pickVisualizations(8);
	        
	        tr = new BreadthFirstPicking(lattice,new Euclidean());
	        tr.pickVisualizations(8);
	        */
	    }
}
