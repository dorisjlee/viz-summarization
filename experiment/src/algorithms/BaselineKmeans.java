package algorithms;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Random;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
import lattice.Lattice;
import lattice.Node;

import net.sf.javaml.core.*; 
import net.sf.javaml.clustering.*;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class BaselineKmeans extends Traversal{
	Experiment exp;
	public BaselineKmeans() {
		super("KMeans");
	}
	
	public void pickVisualizations(Experiment exp) {
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   System.out.println("---------------- Shape Clustering -----------------");
	   ArrayList<Integer> result = kmeans(lattice,exp.k);
	   exp.dashboard.maxSubgraph= result; 
	   exp.dashboard.maxSubgraphUtility=exp.dashboard.computeSubGraphUtility(result);
	   exp.dashboard.printMaxSubgraphSummary();
   }
	
	public static ArrayList<Integer> kmeans(Lattice lattice,Integer k) 
	{
	       ArrayList<Integer> dashboard = new ArrayList<Integer>();
	       dashboard.add(0); // Adding root
	       int it =1;
	       Dataset data = new DefaultDataset();
	       while(it < lattice.nodeList.size())
	       {
	    	   //System.out.println(lattice.ID2idMap.get(it));
	    	   ArrayList<Double> value_list = lattice.id2MetricMap.get(lattice.id2IDMap.get(it));
	    	   //System.out.println(value_list);
	    	   double[] values = new double[value_list.size()];
	    	  
	    	   for (int sd = 0; sd < values.length; sd++) 
	    	   {
	    	      values[sd] = value_list.get(sd);
	    	   }
	    	   Instance instance = new DenseInstance(values);
	    	   data.add(instance);
	    	   it++;
	       }
	       
	       Clusterer km = new KMeans(k);
		    /*
		     * Cluster the data, it will be returned as an array of data sets, with
		     * each dataset representing a cluster
		     */
		    Dataset[] clusters = km.cluster(data);
		    System.out.println("Cluster count: " + clusters.length);
		    System.out.println(clusters[0]);
		    System.out.println(clusters[1]);
	       
	       /*
	       for(int i=1; i<k; i++)
	       {
	    	   dashboard.add(i);
	       }
	       for(int i =0; i < dashboard.size(); i++)
	       {
	    	   System.out.println(dashboard.get(i));
	       }
	       */
	       
	       return dashboard;
	}
}
