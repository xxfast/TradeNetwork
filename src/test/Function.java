package test;

public class Function {
	public static double custscoreFunction(double nextVal,double minVal,double maxVal)
	{
		//simple decreasing linear score function for customer
		double val=0;
		double m=-1/(maxVal-minVal);
		val=m*nextVal-m*(minVal)+1;
		return val;
	}
	public static double retScoreFunction(double nextVal,double minVal,double maxVal)
	{
		//simple increasing linear score function for supplier
		double val=0;
		double m=1/(maxVal-minVal);
		val=m*nextVal-m*(maxVal)+1;
		return val;
	}
}
