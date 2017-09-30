package experiment;

//import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Feature2D;
//import experiment.JFastEMD.src.com.telmomenezes.jfastemd.JFastEMD;
//import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Signature;
//import experiment.JFastEMD.src.com.telmomenezes.jfastemd.Test;
public class EarthMover implements Distance{
	private double emd;
	public EarthMover() {
		emd = 0;
	}
	@Override
	public double computeDistance(double [] viz1, double [] viz2) 
    {
		// The 1-D implementation of EMD is simply the cumulative sum of how much dirt needs to be moved over in total
		assert viz1.length==viz2.length;
		for (int i=0; i<viz1.length;i++) {
			emd+=Math.abs(viz1[i]-viz2[i]);
		}
        return emd;
    }
    
}
