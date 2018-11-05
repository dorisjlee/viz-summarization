package algorithms;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;

/**
 * XYZ
 * ABC
 */
public class BaselineBFS extends Traversal
{
	public BaselineBFS() 
	{
		super("Breadth First Search");
	}
	
	public void pickVisualizations(Experiment exp) 
	{
		this.exp = exp;
		this.lattice = exp.lattice;
		System.out.println("---------------- Breadth First Search -----------------");
		ArrayList<Integer> rwResult = bfs(lattice, exp.k);
		exp.dashboard.maxSubgraph= rwResult;
		exp.dashboard.maxSubgraphUtility=exp.dashboard.computeSubGraphUtility(rwResult);
		exp.dashboard.printMaxSubgraphSummary();
	}
	
	public static ArrayList<Integer> bfs(Lattice lattice, Integer k) 
	{
	       ArrayList<Integer> dashboard = new ArrayList<Integer>();
	       dashboard.add(0); // Adding root
	       // Stop when dashboard exceeds desired size k 
	       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
	       {
	    	   ArrayList<Integer> currentFrontier = RandomWalk.getFrontier(lattice, dashboard);
	          
	           if (currentFrontier.size() >= k) 
	           {
	        	   for(int it = 0; it < k; it++)
	        	   {
	        		   dashboard.add(currentFrontier.get(it));
	        	   }
	           }
	           else
	           {
	        	   for(int it = 0; it < currentFrontier.size(); it++)
	        	   {
	        		   dashboard.add(currentFrontier.get(it));
	        	   }
	           }
	       }
	       for(int i =0; i < dashboard.size(); i++)
	       {
	    	   System.out.println(dashboard.get(i));
	       }
	       
	       return dashboard;
	}
	public static ArrayList<Integer> getFrontier(Lattice lattice,ArrayList<Integer> dashboard) 
	{
		ArrayList<Integer> currentFrontier = new ArrayList<Integer>();
     //System.out.println("Dashboard Size: "+dashboard.size());
     int next = -1;
     for(int i = 0; i < dashboard.size(); i++)
     {
          //System.out.println("Children of: "+lattice.nodeList.get(dashboard.get(i)).get_id());
  	       // Looping through all children indexes 
         int flag = 0;
         Integer currentNodeID = dashboard.get(i);
         Node currentNode = lattice.nodeList.get(currentNodeID);
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

}
