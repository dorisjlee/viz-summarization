package algorithms;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import distance.Distance;
import distance.Euclidean;
import lattice.Hierarchia;
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
		   ArrayList<Integer> rwResult = RandomWalk.randomWalk(lattice,k);
	       double total_utility=computeSubGraphUtility(rwResult);
	       if (total_utility>lattice.maxSubgraphUtility){
		       lattice.maxSubgraph= rwResult; 
		       lattice.maxSubgraphUtility=total_utility;
	       }
	       count+=1;
	   }
	   printMaxSubgraphSummary();
   }
	public static void main (String[] args) throws SQLException {
    		Euclidean ed = new Euclidean();
    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
    		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
        Traversal tr; 
        tr = new MultipleRandomWalk(1000000,lattice,new Euclidean());
        tr.pickVisualizations(8);
        
        tr = new GreedyPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
        
        tr = new BreadthFirstPicking(lattice,new Euclidean());
        tr.pickVisualizations(8);
    }
}
