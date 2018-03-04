package algorithms;

import net.sf.javaml.core.*; 
import net.sf.javaml.clustering.*;

public class KmeansTest {
	
	public static void main(String[] args) throws Exception 
	{

	    /* Load a dataset */
		double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		double[] values2 = new double[] { 1, 2, 3, 4, 5, 12, 7, 8, 9, 10 };
		double[] values3 = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 3, 11 };
		double[] values4 = new double[] { 1, 4, 3, 4, 2, 6, 7, 2, 3, 11 };
	    Instance instance = new DenseInstance(values);
	    Instance instance2 = new DenseInstance(values2);
	    Instance instance3 = new DenseInstance(values3);
	    Instance instance4 = new DenseInstance(values4);
	    Dataset data = new DefaultDataset();
	    data.add(instance);
	    data.add(instance2);
	    data.add(instance3);
	    data.add(instance4);
	    /*
	     * Create a new instance of the KMeans algorithm, with no options
	     * specified. By default this will generate 4 clusters.
	     */
	    Clusterer km = new KMeans(2);
	    /*
	     * Cluster the data, it will be returned as an array of data sets, with
	     * each dataset representing a cluster
	     */
	    Dataset[] clusters = km.cluster(data);
	    System.out.println("Cluster count: " + clusters.length);
	    System.out.println(clusters[0]);
	    System.out.println(clusters[1]);
	    
	    
	    
	    
	}

}
