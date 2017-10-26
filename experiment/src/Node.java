/*
 * @author Himel
 */
import java.util.*;

public class Node 
{
    String id;
    ArrayList<Integer> child_list; // List of node IDs of the children
    ArrayList<Double> dist_list; // edge weights (distance) of this node to all the children
    ArrayList<String> merged_nodes_keys;
    long population_size;
    
    public long getPopulation_size() {
		return population_size;
	}
	public void setPopulation_size(long population_size) {
		this.population_size = population_size;
	}
	
    public ArrayList<String> getMerged_nodes_keys() {
		return merged_nodes_keys;
	}
	public void setMerged_nodes_keys(ArrayList<String> merged_nodes_keys) {
		this.merged_nodes_keys = merged_nodes_keys;
	}
	
	Node(String id)
    {
        this.id = id; 
        child_list = new ArrayList<>();
        dist_list = new ArrayList<>();
    }
    void set_child_list(ArrayList<Integer> child_list)
    {
        this.child_list = new ArrayList<>(child_list);
    }
    void set_dist_list(ArrayList<Double> dist_list)
    {
        this.dist_list = new ArrayList<>(dist_list);
    }
    String get_id()
    {
        return id;
    }
    ArrayList<Integer> get_child_list()
    {
        return child_list;
    }  
    ArrayList<Double> get_dist_list()
    {
        return dist_list;
    } 
}
