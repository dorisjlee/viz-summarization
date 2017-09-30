package experiment;

import static org.junit.Assert.*;
import org.junit.Test;
import experiment.Euclidean;
import experiment.LpNorm;

public class DistanceTest {

	@Test
	public void testComputeDistance() {
		Euclidean ed = new Euclidean();
		double [] viz1 = {2.1,3.2,2.5};
		double [] viz2 = {2.5,7.8,3.6};
		assertTrue("Euclidean Distance Test",ed.computeDistance(viz1, viz2)==4.746577714522327);
		LpNorm lpobj = new LpNorm(1);
		System.out.println(lpobj.computeDistance(viz1, viz2));
		assertTrue("L1-norm Test", lpobj.computeDistance(viz1, viz2)==6.1); //0.4+(7.8-3.2)+(3.6-2.5)
		lpobj = new LpNorm(2);
		assertTrue("L2-norm Test", lpobj.computeDistance(viz1, viz2)==ed.computeDistance(viz1, viz2));
	}

}
