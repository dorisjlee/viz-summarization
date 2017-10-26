import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class VizOutput {
	Lattice lattice; 
	ArrayList<Integer> selectedNodes;
	Hierarchia h;
	String yName;
	public VizOutput(Lattice lattice, ArrayList<Integer> selectedNodes,Hierarchia h, String yName) {
		this.lattice = lattice;
		this.selectedNodes = selectedNodes;
		this.h = h ;
		this.yName=yName;
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
		ArrayList<String> xAttr = h.uniqueAttributeKeyVals.get(h.xAxis);
		String nodeDic = "{";
		for (int i=0; i<selectedNodes.size();i++) {
			nodeDic+= "\\\""+(i+1)+"\\\": [";
			int selectedNodeID = selectedNodes.get(i);
			Node selectedNode = lattice.nodeList.get(selectedNodeID);
			ArrayList<Double> nodeVal = lattice.id2MetricMap.get(selectedNode.id);
			for (int ix=0; ix<xAttr.size();ix++) {
				nodeDic+="{ \\\"xAxis\\\": \\\""+xAttr.get(ix)+"\\\", \\\"yAxis\\\":"+ nodeVal.get(ix) +"},";
			}
			nodeDic+="{\\\"childrenIndex\\\":"+selectedNode.get_child_list()+
					", \\\"populationSize\\\":"+selectedNode.getPopulation_size()+
				    ", \\\"filter\\\":\\\""+selectedNode.get_id() +"\\\",\\\"yName\\\":\\\""+yName+"\\\"}]";
			if (i!=selectedNodes.size()-1) {
				nodeDic+=',';
			}
		}
		
		nodeDic+="}";
		return nodeDic;
	}
	public String generateLatticeDic() {
		// Dictionary containing node IDs and Lattice/Graph 
		return null;
	}
	public static void dumpString2File(String filename,String content) {
		try {
            Files.write(Paths.get(filename), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static void main(String[] args) throws SQLException 
    {
	   Euclidean ed = new Euclidean();
	   //Hierarchia h = new Hierarchia("titanic","survived");
	   //Hierarchia h = new Hierarchia("turn","has_list_fn");
	   //Hierarchia h = new Hierarchia("mushroom","type");
	   Hierarchia h = new Hierarchia("mushroom","cap_surface");
	   Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.1,0.8);
       Traversal tr = new FrontierGreedyPicking(lattice,new Euclidean());
       //tr.greedyPicking(20);
       //tr.greedyPicking(20);
       tr.pickVisualizations(20);
       VizOutput vo = new VizOutput(lattice, lattice.maxSubgraph, h,"COUNT(id)");
       String nodeDic = vo.generateNodeDic();
       dumpString2File("nodeDic.json", nodeDic);
       System.out.println("nodeDic:"+nodeDic);
       System.out.println("LatticeDic:"+vo.generateLatticeDic());
       System.out.println(h.uniqueAttributeKeyVals.keySet());
       System.out.println(h.uniqueAttributeKeyVals.values());
       System.out.println(h.uniqueAttributeKeyVals.size());
    }
}
