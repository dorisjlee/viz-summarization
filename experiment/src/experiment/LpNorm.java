package experiment;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure; 
import net.sf.javaml.distance.EuclideanDistance; 
import net.sf.javaml.distance.NormDistance; 

public class LpNorm implements Distance{
	private NormDistance lpObj;
	public LpNorm(double power) {
		lpObj = new NormDistance(power);
	}
	@Override
	public double computeDistance(double [] viz1, double [] viz2) {
		Instance i1 = new DenseInstance(viz1,"viz1");
		Instance i2 = new DenseInstance(viz2,"viz2");
		return lpObj.measure(i1,i2);
	}
}
