package algorithms;
import java.util.ArrayList;

import distance.Distance;
import lattice.Lattice;

/**
 * Looping through all children of current node (over all node in nodeList)
 * Greedily adding in nodes with larger utility than current max 
 * Stop until reach k nodes in dashboard
 */
public class GreedyPicking extends Traversal{
	
	public GreedyPicking() {
		super("Greedy Picking");
	}
	
	public void pickVisualizations(Experiment exp, Integer k) {
	   this.exp = exp;
	   this.lattice=exp.lattice;
	   System.out.println("---------------- Greedy Picking -----------------");
       double total_utility = 0;
       ArrayList<Integer> dashboard = new ArrayList<Integer>();
       dashboard.add(0); // Adding root
       // Stop when dashboard exceeds desired size k 
       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
       {	
       	   double max_utility = 0;
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
                   if(flag == 0 && lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j) > max_utility)
                   {
                       max_utility = lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j);
                       next = lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j);
                   }
               }
           }
           dashboard.add(next);
           total_utility+=max_utility;
       }
       lattice.maxSubgraph= dashboard; 
       lattice.maxSubgraphUtility=total_utility;
       printMaxSubgraphSummary();
   }
}
