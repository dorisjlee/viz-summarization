import java.sql.SQLException;

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
	public Experiment(String datasetName, String xAxisName, String yAxisName, int k, String algoName, Distance dist,
			double iceberg_ratio, double informative_critera) throws SQLException {
		super();
		this.datasetName = datasetName;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		this.k = k;
		this.algoName = algoName;
		this.dist = dist;
		this.iceberg_ratio = iceberg_ratio;
		this.informative_critera = informative_critera;
		this.distName = dist.getDistName();
		this.h = new Hierarchia(datasetName,xAxisName);
		this.lattice = Hierarchia.generateFullyMaterializedLattice(dist,iceberg_ratio,informative_critera);
		this.nbars = lattice.id2MetricMap.get("#").size();
		if (this.algoName.equals("frontierGreedy")) {
			this.algo = new FrontierGreedyPicking(lattice,dist);   
		}else if (this.algoName.equals("greedy")) {
			this.algo = new GreedyPicking(lattice,dist);
		}else if (this.algoName.equals("naiveGreedy")) {
			this.algo = new NaiveGreedyPicking(lattice, dist);
		}
	    this.fname = datasetName+"_"+xAxisName.replace("_","-")+"_"+algoName+"_"+distName+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+"_nbar"+nbars+".json"; 
	}
	public void runOutput() {
		algo.pickVisualizations(k);
		VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph, h, yAxisName);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File("../ipynb/dashboards/json/"+fname, nodeDic);
	}
	public static void main(String[] args) throws SQLException 
	{
	   // Testing different algo on different datasets
	   int k = 20;
       String algo = "frontierGreedy"; 
	   //String algo = "naiveGreedy";
	   //String algo = "greedy";
	   Experiment exp = new Experiment("titanic", "survived", "COUNT(id)", k, algo, new Euclidean(),0.1,0.8);
	   exp.runOutput();
	   exp = new Experiment("turn", "has_list_fn", "SUM(slots_millis_reduces)", k, algo, new Euclidean(),0.1,0.8);
	   exp.runOutput();
	   exp = new Experiment("mushroom","type", "COUNT", k, algo, new Euclidean(),0.1,0.8);
	   exp.runOutput();
	   exp = new Experiment("mushroom","cap_surface", "COUNT", k, algo, new Euclidean(),0.1,0.8);
	   exp.runOutput();   
	   //}
	   
	   
	}
}