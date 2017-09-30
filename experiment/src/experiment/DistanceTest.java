package experiment;
import static org.junit.Assert.*;
import org.junit.Test;
import experiment.Euclidean;
import experiment.LpNorm;
import experiment.EarthMover;

public class DistanceTest {

	@Test
	public void testComputeDistance() {
		Euclidean ed = new Euclidean();
		double [] viz1 = {2.1,3.2,2.5};
		double [] viz2 = {2.5,7.8,3.6};
		assertTrue("Euclidean Distance Test",ed.computeDistance(viz1, viz2)==4.746577714522327);
		LpNorm lpobj = new LpNorm(1);
		assertTrue("L1-norm Test (Sum of absolute differences)", lpobj.computeDistance(viz1, viz2)==6.1); //0.4+(7.8-3.2)+(3.6-2.5)
		lpobj = new LpNorm(2);
		assertTrue("L2-norm Test", lpobj.computeDistance(viz1, viz2)==ed.computeDistance(viz1, viz2));
		KLDivergence kldiv = new KLDivergence();
		assertTrue("KL Divergence",kldiv.computeDistance(viz1,viz2)==-5.9566905402196895); // 2.1*np.log2((2.1/2.5))+3.2*np.log2((3.2/7.8))+2.5*np.log2((2.5/3.6))
		MaxDiff mdiff = new MaxDiff();
		assertTrue("Max Difference",mdiff.computeDistance(viz1,viz2)==4.6); // max(0.4,4.6,1.1)
//		EarthMover emd = new EarthMover();
//		System.out.println(emd.computeDistance(viz1,viz2));
		
	}

}
