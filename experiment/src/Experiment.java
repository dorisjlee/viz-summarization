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
		if (this.algoName.equals("frontierGreedy")) {
		   this.algo = new FrontierGreedyPicking(lattice,dist);   
		}
	    this.fname = datasetName+"_"+xAxisName+"_"+algoName+"_"+distName+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+".json"; 
	}
	public void runOutput() {
		algo.pickVisualizations(k);
		VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph, h, yAxisName);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File("../ipynb/dashboards/json/"+fname, nodeDic);
	}
	public static void main(String[] args) throws SQLException 
	{
	   Experiment exp = new Experiment("titanic", "survived", "COUNT (id)", 10, "frontierGreedy", new Euclidean(),0.1,0.8);
       exp.runOutput();
	}
}