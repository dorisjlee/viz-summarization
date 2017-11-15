package algorithms;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import distance.Distance;
import lattice.Lattice;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class MultipleRandomWalk extends Traversal{
	int maxCount;
	public MultipleRandomWalk(int maxCount,Lattice lattice, Distance metric) {
		super(lattice, metric, "Multiple Random Walk in Lattice");
		this.maxCount=maxCount;
	}
	
	public void pickVisualizations(Integer k) {
	   lattice.maxSubgraphUtility=0; // reset maxSubgraphUtility when picking
	   System.out.println("---------------- Multiple Random Walk -----------------");
	   int count =0;
	   
	   while (count < maxCount) {
	       double total_utility =0;
	       ArrayList<Integer> dashboard = new ArrayList<Integer>();
	       dashboard.add(0); // Adding root
	       // Stop when dashboard exceeds desired size k 
	       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
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
	       total_utility=computeSubGraphUtility(dashboard);
	       if (total_utility>lattice.maxSubgraphUtility){
		       lattice.maxSubgraph= dashboard; 
		       lattice.maxSubgraphUtility=total_utility;
	       }
	       count+=1;
	   }
	   printMaxSubgraphSummary();
   }

}
