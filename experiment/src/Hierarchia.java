/*
 * @author himeldev
 */
import java.util.*;

public class Hierarchia 
{
    public static void main(String[] args)
    {
        ArrayList<String> attribute_names = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        HashMap<String, ArrayList<Double>> map_id_to_metric_values = new HashMap<String, ArrayList<Double>>();
        ArrayList<Node> node_list = new ArrayList<Node>();
        HashMap<String, Integer> map_id_to_index = new HashMap<String, Integer>();
        
        Node root = new Node("#");
        node_list.add(root);
        map_id_to_index.put("#", 0);
        
        
        int n = attribute_names.size();
        for(int k = 1; k <= n; k++)
        {
            System.out.println("k: "+k);
            ArrayList<ArrayList<String>> k_attribute_combinations = new ArrayList<ArrayList<String>>();
            ArrayList<String> current_combination = new ArrayList<String>();
            for(int i = 0; i < k; i++)
            {
                current_combination.add("#");
            }
            generate_k_combinations(attribute_names, k, 0, current_combination, k_attribute_combinations);
            System.out.println("Number of combinations: "+k_attribute_combinations.size());
            
            System.out.println("Attribute Combinations: "+k_attribute_combinations);
            
            for(int i = 0; i < k_attribute_combinations.size(); i++)
            {
                current_combination = k_attribute_combinations.get(i);
                
                ArrayList<ArrayList<String>> attribute_values = new ArrayList<ArrayList<String>>();
                for(int j = 0; j < k; j++)
                {
                    ArrayList<String> binary_values = new ArrayList<String>();
                    binary_values.add("0");
                    binary_values.add("1");
                    attribute_values.add(binary_values);       
                }
                ArrayList<ArrayList<String>> value_permutations = new ArrayList<ArrayList<String>>();
                ArrayList<String> current_permutation = new ArrayList<String>();
                for(int j = 0; j < k; j++)
                {
                    current_permutation.add("#");
                }
                generate_value_permutations(attribute_values, 0, current_permutation, value_permutations);
                //System.out.println("Value Permutations: "+value_permutations);
                
                for(int j=0; j < value_permutations.size(); j++)
                {
                    current_permutation = value_permutations.get(j);
                    String visualization_key = "#";
                    for(int sp = 0; sp < k; sp++)
                    {
                        visualization_key += current_combination.get(sp)+"$"+current_permutation.get(sp)+"#";  
                    }
                    // id: list of metric values
                    // node_list: list of child indexes

                    // After generating viz put in map
                    map_id_to_metric_values.put(visualization_key, compute_visualization(current_combination, current_permutation));
                    Node node = new Node(visualization_key);
                    node_list.add(node); // add parent first, then children
                    map_id_to_index.put(visualization_key, node_list.size()-1);
                    
                    for(int dr = 0; dr < k; dr++)
                    {
                        visualization_key = "#";
                        for(int sp = 0; sp < k; sp++)
                        {
                            if(sp!=dr) // which filter do I drop to get the children
                                visualization_key += current_combination.get(sp)+"$"+current_permutation.get(sp)+"#";
                        }
                        int parent_index = map_id_to_index.get(visualization_key);
                        ArrayList<Integer> child_list = node_list.get(parent_index).get_child_list(); //update list of children
                        child_list.add(node_list.size()-1);
                        node_list.get(parent_index).set_child_list(child_list);
                        
                    }
                    /*
                    System.out.print("Node List: ");
                    for(int x = 0; x < node_list.size(); x++)
                    {
                        System.out.print(node_list.get(x).get_id()+" ");
                    }
                    System.out.println();
                    for(int x = 0; x < node_list.size(); x++)
                    {
                        System.out.println("Node: "+node_list.get(x).get_id());
                        for(int y=0; y < node_list.get(x).get_child_list().size(); y++)
                        {
                            System.out.print(node_list.get(x).get_child_list().get(y)+" ");
                        }
                        System.out.println();
                    }
                    */
                    
                }
            }            
        }
        // traverse to check if this is corectly generate the lattice , node list stores viz along with reference to children
        /*
        for(int x = 0; x < node_list.size(); x++)
        {
            System.out.println("Node index: "+x+", Node id: "+node_list.get(x).get_id());
            System.out.print("Parents of following nodes: ");
            for(int y=0; y < node_list.get(x).get_child_list().size(); y++)
            {
                System.out.print(node_list.get(node_list.get(x).get_child_list().get(y)).get_id()+" ");
            }
            System.out.println();
        */
        //System.out.println("Number of visualizations: "+map_id_to_metric_values.size());
        //print_map(map_id_to_metric_values);
        
		Euclidean ed = new Euclidean();
		Traversal.greedyPicking( map_id_to_metric_values, node_list, map_id_to_index, ed);
		
    }
    
    static void generate_k_combinations(ArrayList<String> attribute_names, int len, int start, ArrayList<String> current_combination, 
            ArrayList<ArrayList<String>> k_combination_list)
    {
        if (len == 0)
        {
            ArrayList<String> current_combination_copy = new ArrayList<String>(current_combination);
            k_combination_list.add(current_combination_copy);
            return;
        }
        for (int i = start; i <= attribute_names.size()-len; i++)
        {
            current_combination.set(current_combination.size() - len, attribute_names.get(i));
            generate_k_combinations(attribute_names, len-1, i+1, current_combination, k_combination_list);
        }
    }
    
    static void generate_value_permutations(ArrayList<ArrayList<String>> attribute_values, int depth,
            ArrayList<String> current_permutation, ArrayList<ArrayList<String>> value_permutations)
    {
        if(depth == attribute_values.size())
        {
            ArrayList<String> current_permutation_copy = new ArrayList<String>(current_permutation);
            value_permutations.add(current_permutation_copy);
            return;
        }
        
        for(int i = 0; i < attribute_values.get(depth).size(); ++i)
        { 
            current_permutation.set(depth, attribute_values.get(depth).get(i));
            generate_value_permutations(attribute_values, depth + 1, current_permutation, value_permutations);
        }
    }
    static ArrayList<Double> compute_visualization(ArrayList<String> current_combination, ArrayList<String> current_permutation)
    {
        ArrayList<Double> measure_values = new ArrayList<Double>();
        double Min = 1.0;
        double Max = 99.0;
        double x = Min + (Math.random() * ((Max - Min) + 1));
        measure_values.add(x);
        measure_values.add(100-x);
        return measure_values;
    }
    
    static void print_map(Map mp) 
    {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) 
        {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
    
}
