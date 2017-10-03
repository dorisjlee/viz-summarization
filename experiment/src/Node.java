/*
 * @author Himel
 */
import java.util.*;

public class Node 
{
    String id;
    ArrayList<Integer> child_list;
    
    Node(String id)
    {
        this.id = id; 
        child_list = new ArrayList<>();
    }
    void set_child_list(ArrayList<Integer> child_list)
    {
        this.child_list = new ArrayList<>(child_list);
    }
    String get_id()
    {
        return id;
    }
    ArrayList<Integer> get_child_list()
    {
        return child_list;
    }  
    
}
