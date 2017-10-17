import java.sql.SQLException;
import java.util.ArrayList;

public class VizOutput {
	Lattice lattice; 
	ArrayList<Integer> selectedNodes;
	public VizOutput(Lattice lattice, ArrayList<Integer> selectedNodes) {
		this.lattice = lattice;
		this.selectedNodes = selectedNodes;
	}
	public String generateNodeDic() {
		// Node dictionary contains all the data required for generating the visualizations
		/*{1: [{'xAxis': 'Clinton', 'yAxis': 48},
			  {'xAxis': 'Trump', 'yAxis': 46},
			  {'xAxis': 'Others', 'yAxis': 6},
			  {'childrenIndex': [2, 3], 'filter': 'All', 'yName': '% of vote'}],
			 2: [{'xAxis': 'Clinton', 'yAxis': 31},
			  {'xAxis': 'Trump', 'yAxis': 62},
			  {'xAxis': 'Others', 'yAxis': 7},
			  {'childrenIndex': [], 'filter': 'Race = White', 'yName': '% of vote'}],
			 3: [{'xAxis': 'Clinton', 'yAxis': 21},
			  {'xAxis': 'Trump', 'yAxis': 70},
			  {'xAxis': 'Others', 'yAxis': 9},
			  {'childrenIndex': [], 'filter': 'Gender = F', 'yName': '% of vote'}]}
		*/
		return null;
	}
	public String generateLatticeDic() {
		// Dictionary containing node IDs and Lattice/Graph 
		return null;
	}
	public static void main(String[] args) throws SQLException 
    {
	   Euclidean ed = new Euclidean();
	   Hierarchia h = new Hierarchia("titanic","survived");
       Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed);
       Traversal tr = new Traversal(lattice,new Euclidean());
       tr.greedyPicking(10);
       VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph);
       System.out.println("nodeDic:"+vo.generateNodeDic());
       System.out.println("LatticeDic:"+vo.generateLatticeDic());
    }
}
