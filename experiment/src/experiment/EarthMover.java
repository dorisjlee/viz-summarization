package experiment;

import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Feature2D;
import experiment.JFastEMD.src.com.telmomenezes.jfastemd.JFastEMD;
import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Signature;
//import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Test;
public class EarthMover implements Distance{
	private double dist;
	public EarthMover() {
		dist = 0;
	}
	static double getValue(double[] map, int x, int y, int bins) {
        return map[(y * bins) + x];
    }

    static Signature getSignature(double[] map, int bins)
    {
        // find number of entries in the sparse matrix
        int n = 0;
        for (int x = 0; x < bins; x++) {
            for (int y = 0; y < bins; y++) {
                if (getValue(map, x, y, bins) > 0) {
                    n++;
                }
            }
        }
        

        // compute features and weights
        Feature2D[] features = new Feature2D[n];
        double[] weights = new double[n];
        int i = 0;
        for (int x = 0; x < bins; x++) {
            for (int y = 0; y < bins; y++) {
                double val = getValue(map, x, y, bins);
                if (val > 0) {
                    Feature2D f = new Feature2D(x, y);
                    features[i] = f;
                    weights[i] = val;
                    i++;
                }
            }
        }

        Signature signature = new Signature();
        signature.setNumberOfFeatures(n);
        signature.setFeatures(features);
        signature.setWeights(weights);

        return signature;
    }
	@Override
	public double computeDistance(double [] viz1, double [] viz2) 
//	static double emdDist(double[] map1, double[] map2, int bins)
    {
        Signature sig1 = getSignature(viz1, viz1.length);
        Signature sig2 = getSignature(viz2, viz2.length);
        double emd = JFastEMD.distance(sig1, sig2, -1);
        dist = emd;
        return emd;
    }
    
}
