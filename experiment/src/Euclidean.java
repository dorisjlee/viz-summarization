import org.apache.commons.math3.ml.distance.EuclideanDistance;
//http://java-ml.sourceforge.net/api/0.1.0/net/sf/javaml/distance/EuclideanDistance.html
public class Euclidean implements Distance{
	private EuclideanDistance dobj;
	public Euclidean() {
		dobj = new EuclideanDistance();
	}
	@Override
	public double computeDistance(double [] viz1, double [] viz2) {
		return dobj.compute(viz1,viz2);
	}
}
