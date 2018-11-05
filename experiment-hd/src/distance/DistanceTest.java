package distance;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class DistanceTest {
	public static boolean almostEqual(double a, double b, double eps){
	    return Math.abs(a-b)<eps;
	}
	@Test
	public void testComputeDistance() {
		Euclidean ed = new Euclidean();
		ArrayList<Double> viz1 = new ArrayList<Double>(Arrays.asList(2.1,7.8,2.5)); //parent
		ArrayList<Double> viz2 = new ArrayList<Double>(Arrays.asList(2.5,3.2,3.6)); //child
		assertTrue("Euclidean Distance Test",ed.computeDistance(viz1, viz2)==4.746577714522327);
		KLDivergence kldiv = new KLDivergence();
		// Eval: 2.1*np.log2((2.1/2.5))+7.8*np.log2((7.8/3.2))+2.5*np.log2((2.5/3.6))
		assertTrue("KL Divergence",almostEqual(kldiv.computeDistance(viz1,viz2),8.182,0.2)); 
		MaxDiff mdiff = new MaxDiff();
		// Eval: max(0.4,4.6,1.1)
		assertTrue("Max Difference",mdiff.computeDistance(viz1,viz2)==4.6); 
		EarthMover emd = new EarthMover();
		// Eval: 2.5-2.1+3.2-7.8+3.6-2.5
		double emdDist = emd.computeDistance(viz1,viz2);
		assertTrue("Earth Mover's Distance",almostEqual(emdDist,-3.1,0.2)); 
	}

}
