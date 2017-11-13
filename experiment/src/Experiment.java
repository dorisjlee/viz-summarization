import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Experiment {
	String datasetName;
    String xAxisName;
	String yAxisName;
	int k;
	String algoName;
	Distance dist ;
	String distName;
	double iceberg_ratio;// [ic] % of root population size to keep as a node
	double informative_critera; //[ip] % closeness to minDist to be regarded as informative parent
	Lattice lattice;
	Hierarchia h;
	Traversal algo;
	String fname;
	int nbars;
	private ArrayList<String> groupby;
	private String aggFunc;
	public static String experiment_name="../ipynb/dashboards/json/"+"vary_dataset_ip";
	public Experiment(String datasetName, String xAxisName, String yAxisName, ArrayList<String> groupby, String aggFunc, int k, String algoName, Distance dist,
			double iceberg_ratio, double informative_critera) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
		super();
		this.datasetName = datasetName;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		this.groupby = groupby;
		this.aggFunc = aggFunc.toUpperCase();
		this.k = k;
		this.algoName = algoName;
		this.dist = dist;
		this.iceberg_ratio = iceberg_ratio;
		this.informative_critera = informative_critera;
		this.distName = dist.getDistName();
		this.h = new Hierarchia(datasetName,xAxisName);
		this.h.setAttribute_names(this.groupby);
		// Generate base table via group-by
		ResultSet rs = Database.viz_query(this.datasetName, this.groupby, this.yAxisName, this.aggFunc, new ArrayList<String>(Arrays.asList()));
		Database.resultSet2csv(rs,this.datasetName,this.groupby,this.aggFunc+"("+this.yAxisName+")");
		this.lattice = Hierarchia.generateFullyMaterializedLattice(dist,iceberg_ratio,informative_critera);
		this.nbars = lattice.id2MetricMap.get("#").size();
		if (this.algoName.equals("frontierGreedy")) {
			this.algo = new FrontierGreedyPicking(lattice,dist);   
		}else if (this.algoName.equals("greedy")) {
			this.algo = new GreedyPicking(lattice,dist);
		}else if (this.algoName.equals("naiveGreedy")) {
			this.algo = new NaiveGreedyPicking(lattice, dist);
		}else if (this.algoName.equals("exhaustive")) {
			this.algo = new ExhaustivePicking(lattice, dist);
		}
		if (experiment_name!="") {
			File directory = new File(experiment_name);
		    if (! directory.exists()){
		        directory.mkdir();
		    }
			this.fname = experiment_name+"/"+datasetName+"_"+xAxisName.replace("_","-")+"_"+algoName+"_"+distName+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+".json";
		}else {
			this.fname = datasetName+"_"+xAxisName.replace("_","-")+"_"+algoName+"_"+distName+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+".json";
		}
	}
	public void runOutput() throws SQLException {
		h.db.c.close();
		algo.pickVisualizations(k);
		VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph, h, yAxisName);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File(fname, nodeDic);
	}
	
	public long timedRunOutput() throws SQLException {
		h.db.c.close();
		long startTime = System.nanoTime();
		algo.pickVisualizations(k);
		long endTime = System.nanoTime();
		VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph, h, yAxisName);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File(fname, nodeDic);
        long duration = (endTime - startTime);
        return duration;
	}
	public static ArrayList<String> pickNRandom(ArrayList<String> lst, int n) {
		LinkedList<String> copy = new LinkedList<String>(lst);
	    Collections.shuffle(copy);
	    return new ArrayList<String>(copy.subList(0, n));
	}
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
//	   Experiment exp;
//	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
//	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
//	   String [] algoList = {"frontierGreedy","naiveGreedy","greedy","exhaustive"};
//	   int numIterations = 10;
//	   int k =10;
//	   String aggFunc="SUM";
//	   experiment_name="../ipynb/dashboards/json/"+"baseline";
//	   //Debugging Exhaustive
//	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("has_list_sum_range_fn","has_corr_list_fn","has_prof_clicks_tbl","has_est_distinct","has_list_sum_fn","has_impressions_tbl","is_profile_query","has_prof_engagement_events_tbl"));
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "frontierGreedy", new Euclidean(),0,0.8);
//	   long duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "greedy", new Euclidean(),0,0.8);
//	   duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
//	   
//	   exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "exhaustive", new Euclidean(),0,0.8);
//	   long duration = exp.timedRunOutput();
//	   System.out.println("Duration:"+duration);
//	   exp.algo.printMaxSubgraphSummary();
	   
//	   groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//	   exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", 30, "frontierGreedy", new Euclidean(),0,0.1);
//	   exp.runOutput();
////	   int[] array = new int[exp.lattice.nodeList.size()];
//	   ArrayList<Integer> list = new ArrayList<Integer>();
//	   for (int i =0;i<50;i=i+2)
//	   {
//		   list.add(i);
//	   }
//	   VizOutput.dumpGenerateNodeDicFromNoHierarchia(99, exp.lattice,list); 
////	   VizOutput.dumpGenerateNodeDicFromNoHierarchia(99, exp.lattice,exp.lattice.maxSubgraph);
//	   System.out.println(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#"));
//	   System.out.println(exp.lattice.nodeList.get(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#")).get_child_list());
//	   for (int i: exp.lattice.nodeList.get(exp.lattice.id2IDMap.get("#has_clicks_tbl$1#")).get_child_list()) {
//		   System.out.println(exp.lattice.nodeList.get(i).get_id());
//	   }
	   /*
	   // Baseline experiment
	   PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
	   writer.println("xAxis,yAxis,algo,groupby,total_time,total_utility");
	   for (int i=0;i<numIterations;i++) {
		   System.out.println("---------------- Iteration #"+i+"----------------");
		   // We are picking 8 random dimensions in the groupby and one random measure value, since 35C5 combinations is too much, we are just picking random samples of potential dashboards in our experiments.
		   ArrayList<String> groupby = pickNRandom(all_dimensions, 8);
		   String yAxis = all_measures.get(new Random().nextInt(all_measures.size()));
		   String xAxis = groupby.get(new Random().nextInt(groupby.size()));
		   //System.out.println(groupby);
		   //System.out.println(xAxis+","+yAxis);
		   for (String algo : algoList) {
			   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, new Euclidean(),0,0.8);
			   long duration = exp.timedRunOutput();
			   writer.println(xAxis+","+yAxis+","+algo+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+duration+","+exp.lattice.maxSubgraphUtility);
		   }
	   }
	   writer.close();
	   */
	   
	   // Generating all possible outputs for frontend to use
	   Experiment exp;
	   Distance [] distList = {new KLDivergence(),new MaxDiff(),new EarthMover(),new Euclidean()};
       String [] algoList = {"frontierGreedy","naiveGreedy","greedy"};
       experiment_name="../ipynb/dashboards/json/"+"vary_all";
       double [] ip_vals = {0.1,0.3,0.5,0.7,0.9,1};
       double [] ic_vals = {0,0.05,0.1,0.15,0.2};
       int [] k_vals = {15,20,25,30};
       for (Distance dist:distList) {
	    	   for (String algo:algoList) {
	    	   	   for (double ip: ip_vals) {
	    	   		   for (double ic: ic_vals) {
	    	   			   for (int k : k_vals) {
//	    	   				   try {
		    	   				   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
		    	   				   exp = new Experiment("titanic", "survived", "id",groupby, "COUNT", k, algo, dist,ic,ip);
						    	   exp.runOutput();
						    	   groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
						    			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
						    	   exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", k, algo, dist,ic,ip);
						    	   exp.runOutput();
						    	   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
						    	   exp = new Experiment("mushroom","type","type",groupby, "COUNT", k, algo, dist,ic,ip);
						    	   exp.runOutput();
						    	   exp = new Experiment("mushroom","cap_surface","cap_surface",groupby, "COUNT", k, algo, dist,ic,ip);
						    	   exp.runOutput();
//	    	   				   }
//	    	   				   catch (Exception e){
//	    	   					   System.out.println("Failed at:"+k+","+ic+","+ip+","+algo);
//	    	   				   }
	    	   			   }
	    	   		   }
	    	   	   }
	       }
       }
       
       
       /*
		// Testing different algo on different datasets
       String algo = "frontierGreedy";
       double [] ip_vals = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
       experiment_name="../ipynb/dashboards/json/"+"vary_dataset_ip";
       for (double ip: ip_vals) {
    	   	   System.out.println("ip:"+ip);
	    	   exp = new Experiment("titanic", "survived", "COUNT(id)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("turn", "has_list_fn", "SUM(slots_millis_reduces)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","type", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","cap_surface", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp.h.db.c.close();
       }
	   */	   
	   
	}
}