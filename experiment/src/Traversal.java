import java.util.ArrayList;
import java.util.HashMap;

public class Traversal {
	public static void main(String[] args) {
		System.out.print("Hello world");
		HashMap<String, ArrayList<Double>>  hmap = Hierarchia.computeVisualizationMap();
		Hierarchia.print_map(hmap);
				
	}
}
