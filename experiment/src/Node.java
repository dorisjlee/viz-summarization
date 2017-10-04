/*
 * @author Himel
 */
import java.util.*;

public class Node 
{
    String id;
    ArrayList<Integer> child_list;
    ArrayList<Double> dist_list;
    
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
