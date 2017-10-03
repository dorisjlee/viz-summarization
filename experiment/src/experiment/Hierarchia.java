/*
 * @author himeldev
 */
import java.util.*;

public class Hierarchia 
{
    public static void main(String[] args) 
    {
        ArrayList<String> attribute_names = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        HashMap<String, ArrayList<Double>> visualization_map = new HashMap<String, ArrayList<Double>>();
        
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
                    String visualization_key = "";
                    for(int sp = 0; sp < k; sp++)
                    {
                        visualization_key += current_combination.get(sp)+"$"+current_permutation.get(sp)+"#";  
                    }
                    //System.out.println(visualization_key);
                    visualization_map.put(visualization_key, compute_visualization(current_combination, current_permutation));
                    
                }
            }            
        }
        System.out.println("Number of visualizations: "+visualization_map.size());
        print_map(visualization_map);
        
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
        measure_values.add(80.0);
        measure_values.add(20.0);
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
