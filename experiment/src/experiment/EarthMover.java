package experiment;
public class EarthMover implements Distance{
	private double emd;
	public EarthMover() {
		emd = 0;
	}
	@Override
	public double computeDistance(double [] viz1, double [] viz2) 
    {
		// The 1-D implementation of EMD is simply the cumulative sum of how much dirt 
		// needs to be moved from viz2's pile (child) to viz1 (parent).
		assert viz1.length==viz2.length;
		for (int i=0; i<viz1.length;i++) {
			emd+=viz2[i]-viz1[i];
		}
        return emd;
    }
    
}
