/*
 * @author himeldev
 */
import java.io.*;
import java.util.*;

import org.w3c.dom.NodeList;

public class Hierarchia 
{
    public static Lattice generateFullyMaterializedLattice(Distance distance){
    	    System.out.println("---------------- Generate Fully Materialized Lattice -----------------");
    		ArrayList<String> attribute_names = get_attribute_names();
        //System.out.println(attribute_names);
        
        HashMap<String, ArrayList<Double>> map_id_to_metric_values = new HashMap<String, ArrayList<Double>>();
        ArrayList<Node> node_list = new ArrayList<Node>();// node_list: list of child indexes
        HashMap<String, Integer> map_id_to_index = new HashMap<String, Integer>();
        ArrayList<Double> root_measure_values = compute_visualization(attribute_names,new ArrayList<String>(),new ArrayList<String>());
        
        map_id_to_metric_values.put("#", root_measure_values);
        Node root = new Node("#");
        node_list.add(root);
        map_id_to_index.put("#", 0);
        
        int n = attribute_names.size();
        for(int k = 1; k <= n; k++)
        {
        		// System.out.println("k: "+k);
            ArrayList<ArrayList<String>> k_attribute_combinations = new ArrayList<ArrayList<String>>();
            ArrayList<String> current_combination = new ArrayList<String>();
            for(int i = 0; i < k; i++)
            {
                current_combination.add("#");
            }
            /*
	            System.out.println("current_combination:"+current_combination);
	            current_combination:[#]
				current_combination:[#, #]
				current_combination:[#, #, #]
				current_combination:[#, #, #, #]
				current_combination:[#, #, #, #, #]
				current_combination:[#, #, #, #, #, #]
				current_combination:[#, #, #, #, #, #, #]
            */
            generate_k_combinations(attribute_names, k, 0, current_combination, k_attribute_combinations);
            //System.out.println("Number of combinations: "+k_attribute_combinations.size());
            
            //System.out.println("Attribute Combinations: "+k_attribute_combinations);
            
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
                    
                    ArrayList<Double> measure_values = compute_visualization(attribute_names, current_combination, current_permutation);
                    
                    if(measure_values.get(0) > 0.0 || measure_values.get(1)> 0.0 )
                    {
                        //System.out.println("Current Visualization: "+visualization_key+" -- "+measure_values);
                        //System.out.print("C");
                        ArrayList<Double> current_visualization_measure_values = 
                                compute_visualization(attribute_names, current_combination, current_permutation);
                        map_id_to_metric_values.put(visualization_key, current_visualization_measure_values);
                        Node node = new Node(visualization_key);
                        node_list.add(node);
                        map_id_to_index.put(visualization_key, node_list.size()-1);

                        double min_distance = 1000000;
                        for(int dr = 0; dr < k; dr++)
                        {
                            visualization_key = "#";
                            for(int sp = 0; sp < k; sp++)
                            {
                                if(sp!=dr)
                                    visualization_key += current_combination.get(sp)+"$"+current_permutation.get(sp)+"#";
                            }
                            //System.out.println("Potential parent: "+visualization_key+" -- "+map_id_to_metric_values.get(visualization_key));
                            if(map_id_to_metric_values.get(visualization_key) != null)
                            {
                                ArrayList<Double> parent_visualization_measure_values = map_id_to_metric_values.get(visualization_key);
                                double [] cviz = Traversal.ArrayList2Array(current_visualization_measure_values);
                            		double [] pviz =  Traversal.ArrayList2Array(parent_visualization_measure_values);
                                double dist = distance.computeDistance(cviz,pviz);
                                if(dist < min_distance)
                                    min_distance = dist;
                            }
                        }
                        
                        for(int dr = 0; dr < k; dr++)
                        {
                            visualization_key = "#";
                            for(int sp = 0; sp < k; sp++)
                            {
                                if(sp!=dr)
                                    visualization_key += current_combination.get(sp)+"$"+current_permutation.get(sp)+"#";
                            }
                            
                            if(map_id_to_metric_values.get(visualization_key) != null)
                            {
                                ArrayList<Double> parent_visualization_measure_values = map_id_to_metric_values.get(visualization_key);
                                double dist = compute_distance(current_visualization_measure_values, parent_visualization_measure_values);
                                if(dist*0.8 <= min_distance)
                                {
                                    int parent_index = map_id_to_index.get(visualization_key);
                                    
                                    ArrayList<Integer> child_list = node_list.get(parent_index).get_child_list();
                                    child_list.add(node_list.size()-1);
                                    node_list.get(parent_index).set_child_list(child_list);
                                    
                                    ArrayList<Double> dist_list = node_list.get(parent_index).get_dist_list();
                                    dist_list.add(dist);
                                    node_list.get(parent_index).set_child_list(child_list);
                                    //System.out.print("I");
                                    //System.out.println("Informative parent: "+visualization_key+" -- "+map_id_to_metric_values.get(visualization_key));
                                }
                            }
                        }
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
        /*
        // Printing out materialized outputs 
        for(int x = 0; x < node_list.size(); x++)
        {
            System.out.println("Node index: "+x+", Node id: "+node_list.get(x).get_id());
            System.out.println("Parents of "+node_list.get(x).get_child_list().size()+" nodes:");
            for(int y=0; y < node_list.get(x).get_child_list().size(); y++)
            {
                System.out.println(node_list.get(node_list.get(x).get_child_list().get(y)).get_id()+" "
                        +node_list.get(x).get_dist_list().get(y));
            }
            System.out.println();
        }*/
        Lattice materialized_lattice = new Lattice(map_id_to_metric_values,node_list,map_id_to_index);
        return materialized_lattice;
    }
    static ArrayList<String> get_attribute_names()
    {
        ArrayList<String> attribute_names = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("materialized_view.csv"));
            String line = null;
            if((line = reader.readLine()) != null) 
            {
                String [] names = line.split(", ");
                for(int i = 0; i < names.length-2; i++)
                {
                    attribute_names.add(names[i]);
                }
            }
            
        }
        catch(IOException e)
        {
            System.out.println("Error");
        }
        return attribute_names;
        
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
    static ArrayList<Double> compute_visualization(ArrayList<String> attribute_names, ArrayList<String> current_combination, ArrayList<String> current_permutation)
    {
        //System.out.println("Attribute-Value Combination:"+current_combination+" -- "+current_permutation);
        ArrayList<Double> measure_values = new ArrayList<Double>();
        double sum_0 = 0;
        double sum_1 = 0;  
        
        ArrayList<Integer> attribute_positions = new ArrayList<Integer>();
        for(int i = 0; i < current_combination.size(); i++)
        {
            for(int j = 0; j < attribute_names.size(); j++)
            {
                if(current_combination.get(i).equals(attribute_names.get(j)))
                {
                    attribute_positions.add(j);
                }
            }
        }
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("materialized_view.csv"));
            String line = reader.readLine();
                 
            
            while((line = reader.readLine()) != null) 
            {
                String [] values = line.split(",");
                int flag = 1;
                for(int i = 0; i < attribute_positions.size(); i++)
                {
                    if(!values[attribute_positions.get(i)].equals(current_permutation.get(i)))
                    {
                        flag = 0;
                        break;
                    }
                }
                if(flag == 1 && values[values.length-2].equals("0"))
                    sum_0 += Double.parseDouble(values[values.length-1]);
                else if(flag == 1 && values[values.length-2].equals("1"))
                    sum_1 += Double.parseDouble(values[values.length-1]);
                    
            }
            
        }
        catch(IOException e)
        {
            System.out.println("Error");
        }
        /*
        double Min = 1.0;
        double Max = 99.0;
        double x = Min + (Math.random() * ((Max - Min) + 1));
        measure_values.add(x);
        measure_values.add(100-x);
        */
        if(Math.abs(sum_0-0.0) <0.000001 &&  Math.abs(sum_1-0.0) <0.000001)
        {
            measure_values.add(-1.0);
            measure_values.add(-1.0);
        }
        else
        {
            measure_values.add(sum_0/(sum_0+sum_1)*100);
            measure_values.add(sum_1/(sum_0+sum_1)*100);
        }
        //System.out.println(measure_values);
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

    
    static double compute_distance(ArrayList<Double> l1, ArrayList<Double> l2)
    {
        double distance = 0;
        for(int i=0; i < l1.size() && i < l2.size(); i++)
        {
            distance += (l1.get(i)-l2.get(i))*(l1.get(i)-l2.get(i));
            //distance += Math.abs(l1.get(i)-l2.get(i));
        }
        return Math.sqrt(distance);
//        return distance;
    }
    
}
