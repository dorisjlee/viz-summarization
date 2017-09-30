package experiment;

public class KLDivergence implements Distance {
	private double kldiv;
	public KLDivergence() {
		kldiv = 0;
	}
	@Override
	public double computeDistance(double [] viz1, double [] viz2) {
		// Computing the KL divergence from Q to reference P
		assert viz1.length==viz2.length;
		for (int i=0; i<viz1.length;i++) {
			if (viz1[i]!=0 & viz2[i]!=0) {
				kldiv+=viz1[i]*(Math.log(viz1[i]/viz2[i])/Math.log(2));
			}
		}
		return kldiv;
	}
}