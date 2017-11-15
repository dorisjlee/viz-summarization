package distance;
public interface Distance {
	public String getDistName();
	public void setDistName(String distName);
	public double computeDistance(double [] viz1, double [] viz2);
	//	Let viz1 be the parent and viz2 be the child 
	// (Order matters for certain metrics such as KL divergence and EMD)
}
