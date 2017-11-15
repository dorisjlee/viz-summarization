import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * In each iteration we pick a node with the highest utility from a set of "frontier" nodes.
 * Then, the children of the picked node are added to the frontier.
 * 
 * @param k
 * @author saarkuzi
 */
public class ProbablisticPicking extends Traversal{

	public ProbablisticPicking(Lattice lattice, Distance metric) {
		super(lattice, metric, "Probabalistic Greedy Picking");
	}

	/**
	 * 
	 * Implementation of the traversal algorithm for generating a subgraph
	 * with maximal utility of k nodes
	 * 
	 * @param k
	 */
	public void pickVisualizations(Integer k)
	{
		super.printAlgoName();
		
	    lattice.maxSubgraph.clear();
	    lattice.maxSubgraphUtility = 0;
	    
		//a map in which keys are node IDs, and values are utilities (interestingness)
		HashMap<Integer,Float> localMaxSubgraph = new HashMap<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.put(rootId, 0f);
		HashMap<Integer,Float> frontierNodesUtility = expandFrontier(new HashMap<>(), rootId);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(frontierNodesUtility.size() == 0) break;
//			Integer selectedNodeID = Collections.max(frontierNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			Integer selectedNodeID = probablisticPickFromFrontier(frontierNodesUtility);
			localMaxSubgraph.put(selectedNodeID, frontierNodesUtility.get(selectedNodeID));
			localMaxSubgraph = updateUtilities(localMaxSubgraph, selectedNodeID);
			frontierNodesUtility = expandFrontier(frontierNodesUtility, selectedNodeID);
		}
		
//		for(Map.Entry<Integer, Float> entry : localMaxSubgraph.entrySet())
//		{
//			System.out.println(lattice.nodeList.get(entry.getKey()).get_id() + ":" + entry.getValue());
//		}
//		System.out.println("=================================================");
//		for(Map.Entry<Integer, Float> entry : frontierNodesUtility.entrySet())
//		{
//			System.out.println(lattice.nodeList.get(entry.getKey()).get_id() + ":" + entry.getValue());
//		}
		
		//System.out.println(frontierNodesUtility);
		//System.out.println(localMaxSubgraph);
		localMaxSubgraph = permuteLattice(localMaxSubgraph, frontierNodesUtility);
		
		for(int nodeId : localMaxSubgraph.keySet())
		{
			lattice.maxSubgraph.add(nodeId);
			lattice.maxSubgraphUtility += localMaxSubgraph.get(nodeId);
		}
		printMaxSubgraphSummary();
	}
	
	public Integer probablisticPickFromFrontier(HashMap <Integer,Float> frontier) {
		Iterator it = frontier.entrySet().iterator();
		ArrayList<Tuple> frontierList = new ArrayList<Tuple>();
		Float sum =0f;
		int i=0;
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        Float x = logistic(0.02,(Float) pair.getValue());
	        sum += x;
	        frontierList.add(new Tuple((Integer) pair.getKey(),x));
	        System.out.println(frontierList.get(i).getX()+","+frontierList.get(i).getY());
	        i++;
	    }
	    for (int j=0;j<frontierList.size();j++) {
	    		frontierList.get(j).setY(frontierList.get(j).getY()/sum);
	    }
	    Float rollSum =0f;
	    for (int k=0; k<frontierList.size();k++) {
	    		rollSum+=frontierList.get(k).getY();
	    		frontierList.get(k).setY(rollSum);
	    		System.out.println(rollSum);
	    }
//	    System.out.println(sum);

	    Random rand = new Random();
	    Float floatThres = rand.nextFloat();
	    if (floatThres>frontierList.get(frontierList.size()-1).getY()) {
	    		floatThres=frontierList.get(frontierList.size()-1).getY()-0.000001f;
	    }
	    
	    int picked =-1;
	    if(floatThres >= 0 && floatThres < frontierList.get(0).getY())
	    {
	    		picked = 0;
	    	
	    }
	    for(int j = 1; j<frontierList.size(); j++)
	    {
	    		if(floatThres >= frontierList.get(j-1).getY() &&
	    				floatThres < frontierList.get(j).getY())
	    		{
	    			picked = j;
	    			break;
	    		}
	    }
	    System.out.println(floatThres);
	    System.out.println(picked);
	    return picked;
	}
	public Float logistic (double a, Float x) {
//		return (float) (1./(1+Math.exp(-a*x)));
		return (float) Math.exp(a*x);
	}
	/**
	 * 
	 * When adding a new node to the subgraph, we should check if it already has children
	 * there (possible because node can have several informative parents).
	 * If it is the case, then the utility of the node needs to be updated by using the most 
	 * informative parent.
	 * 
	 * @param currentMaxSubgraph, nodeId
	 */
	private HashMap<Integer,Float> updateUtilities(HashMap<Integer,Float> currentMaxSubgraph, int nodeId)
	{
		Node currentNode = lattice.nodeList.get(nodeId);
		for(int childId : currentNode.child_list)
		{
			if(!currentMaxSubgraph.containsKey(childId)) continue;
			Double newUtility = super.calculateDistance(nodeId, childId);
			Float currentUtility = currentMaxSubgraph.get(childId);
			currentMaxSubgraph.put(childId, (float) Math.max(currentUtility, newUtility));
		}	
		return currentMaxSubgraph;
	}
	
	/**
	 * Adding nodes to a frontier; the added nodes are the children of 
	 * a given parent node.
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	private HashMap<Integer, Float> expandFrontier(HashMap<Integer, Float> currentFrontier, Integer parentNodeId)
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(lattice.maxSubgraph.contains(childId)) continue;
			double utility = super.calculateDistance(parentNodeId, childId);
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}

	/**
	 * Replacing one node in the subgraph with one node from the frontier
	 * 
	 * @param currentSubgraph, frontierNodesUtility
	 */
	private HashMap<Integer,Float> permuteLattice(HashMap<Integer,Float> currentSubgraph, HashMap<Integer,Float> frontierNodes)
	{
		Float maximalUtility = super.sumMapByValue(currentSubgraph);
		int chosenFrontierNodeId = -1;
		for(int frontierNodeId : frontierNodes.keySet())
		{
			
			HashMap<Integer,Float> tempMaxSubgraph = swapSingleNode(frontierNodeId, frontierNodes.get(frontierNodeId), currentSubgraph);
			Float tempMaxUtility = super.sumMapByValue(tempMaxSubgraph);
			if( tempMaxUtility > maximalUtility)
			{
				maximalUtility = tempMaxUtility;
				currentSubgraph = tempMaxSubgraph;
				chosenFrontierNodeId = frontierNodeId;
			}
		}
		
		if(chosenFrontierNodeId > 0)
			currentSubgraph = updateUtilities(currentSubgraph, chosenFrontierNodeId);
		
		return currentSubgraph;
	}
	
	/**
	 * Adding a candidate node to a subgraph at the expense of removing another
	 * 
	 * @param candidateId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer, Float> swapSingleNode(int candidateId, Float candidateUtility, HashMap<Integer,Float> maxSubgraph)
	{
		Float maxUtility = super.sumMapByValue(maxSubgraph);
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(maxSubgraph);

		for(int nodeId : maxSubgraph.keySet())
		{
			HashMap<Integer,Float> tempSubgraph = replace2Nodes(nodeId, candidateId, candidateUtility, maxSubgraph);
			Float newUtility = super.sumMapByValue(tempSubgraph);
			if(newUtility > maxUtility)
			{
				maxUtility = newUtility;
				newMaxSubgraph = tempSubgraph;
			}
		}
		return newMaxSubgraph;
	}
	
	/**
	 * Adding one node, and deleting another. If it is not possible then the original 
	 * subgraph is returned.
	 * 
	 * @param outNodeId, inNodeId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer,Float> replace2Nodes(int outNodeId, int inNodeId, Float candidateUtility, HashMap<Integer,Float> originalMaxSubgraph)
	{
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(originalMaxSubgraph);
		newMaxSubgraph.put(inNodeId, candidateUtility);
		newMaxSubgraph.remove(outNodeId);
		
		// first, if the node has no children we can safely remove it
		ArrayList<Integer> childrenInSubgraph = new ArrayList<>();
		for(int childId : lattice.nodeList.get(outNodeId).child_list)
		{
			if(newMaxSubgraph.containsKey(childId))
				childrenInSubgraph.add(childId);
		}
		if(childrenInSubgraph.size() == 0) 
			return newMaxSubgraph;
		
		// if the node has children, then maybe these children have several parents;
		// so it will still be okay to remove the node
		for(int childId : childrenInSubgraph)
		{
			HashMap<Integer,Float> otherParents = new HashMap<>();
			for(int possibleParentId : newMaxSubgraph.keySet())
			{
				Node possibleParentNode = lattice.nodeList.get(possibleParentId);
				if(possibleParentNode.child_list.contains(childId))
				{
					double dist = super.calculateDistance(possibleParentId, childId);
					otherParents.put(possibleParentId, (float) dist);
				}
			}
			if(otherParents.isEmpty())
				return originalMaxSubgraph;
			
			Float newUtility = Collections.max(otherParents.entrySet(), Map.Entry.comparingByValue()).getValue();
			newMaxSubgraph.remove(childId);
			newMaxSubgraph.put(childId, newUtility);
		}
		return newMaxSubgraph;
	}
	 public static void main (String[] args) throws SQLException {
		    /*    
		    	ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
			int r = 3;
	        combination(pivot_children, r);
	        */
	    		Euclidean ed = new Euclidean();
	    		Hierarchia h = new Hierarchia("mushroom","cap_surface");
	    		//Hierarchia h = new Hierarchia("turn","has_list_fn");
	    		//Hierarchia h = new Hierarchia("titanic","survived");
	    		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
	        Traversal tr; 
	        tr = new ProbablisticPicking(lattice,new Euclidean());
	        tr.pickVisualizations(5);
	        tr = new RandomWalk(lattice,new Euclidean());
	        tr.pickVisualizations(5);
	        
	        tr = new GreedyPicking(lattice,new Euclidean());
	        tr.pickVisualizations(5);
	        
	        tr = new BreadthFirstPicking(lattice,new Euclidean());
	        tr.pickVisualizations(5);
	 }
}