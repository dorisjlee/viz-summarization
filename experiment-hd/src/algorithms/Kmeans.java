package algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Kmeans {


	double data[][];
	int acount = 2;
	int ecount = 600;
        int kreal=12;
        int kval=2;
        int nitr =10;

        public ArrayList<Integer>[] clusterList = (ArrayList<Integer>[]) new ArrayList[kreal];
        int numOfClusters=0;
        //public int[] clusterListCentroid = new int[kreal];
        public double[] clusterListSSE = new double[kreal];

        
        

        public static void main(String[] args) {
            Kmeans ab = new Kmeans();
            ab.solve();
        }

    public void solve()
    {
            //File in = new File("Data2.csv");
            File in = new File("bisecting.txt");
            Scanner input = new Scanner(System.in);
            try
            {
		input = new Scanner(in);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            data = new double[ecount][acount];
            //Reading input
            StringTokenizer st;
            String dataLine;
            for(int i=0;i<ecount;i++)
            {
		dataLine = input.nextLine();
                //System.out.println(dataLine);
		st = new StringTokenizer(dataLine," \t");
		//st = new StringTokenizer(dataLine,",");
		for(int j=0;j<acount;j++)
		{
                    data[i][j] = Double.parseDouble( st.nextToken() );
		}
                //st.nextToken();
            }

            //Initial cluster contains all points
            for(int i=0;i<kreal;i++)
            {
                clusterList[i]=new ArrayList<Integer>();
            }
            for(int i=0;i<ecount;i++)
            {
                clusterList[0].add(i);
            }
            clusterListSSE[0]=clusterListSSEcalc(clusterList[0]);
            numOfClusters=1;

            System.out.println("First bisection:");
            //Bisecting a cluster loop
            while(numOfClusters<kreal)
            {
                double max=0;
                int index=0;
                for(int i=0; i<numOfClusters;i++)
                {
                    if(clusterListSSE[i]>max)
                    {
                        max=clusterListSSE[i];
                        index=i;
                    }
                }

                System.out.println("Cluster to bisect: "+index);
                double mina = 99999;
                ArrayList<Integer>[] calcul = (ArrayList<Integer>[]) new ArrayList[kval];
                for(int sp=0;sp<nitr;sp++)
                {

                    ArrayList<Integer> inits = new ArrayList<Integer>();
                    double[][] centroids = new double[kval][acount];
                    double[][] newCentroids = new double[kval][acount];
                    ArrayList<Integer>[] clusters = (ArrayList<Integer>[]) new ArrayList[kval];
                    

                    //Initial k centroid selection
                    Random random = new Random();
                    System.out.println("Initial Centroids:");
                    for(int i=0;i<kval;i++)
                    {
                        int rnum = random.nextInt(ecount);

                        if(inits.contains(rnum)==false && clusterList[index].contains(rnum)==true)
                        {
                            inits.add(rnum);
                            //System.out.println(rnum);
                            for(int j=0;j<acount; j++)
                            {
                                System.out.print(data[rnum][j]+" ");
                                centroids[i][j] = data[rnum][j];
                            }
                            System.out.println();
                        }
                        else
                        {
                            i--;
                        }

                    }

                    int flag=1;
                    while(flag!=0)
                    {
                        //Cluster list initialization
                        for(int j=0;j<kval;j++)
                        {
                            clusters[j]=new ArrayList<Integer>();
                        }
                        System.out.println("Cluster Membership");
                        for(int i=0;i<clusterList[index].size();i++)
                        {
                            double distmin = 99999;
                            int clusterNo = 0;
                            for(int j=0;j<kval;j++)
                            {
                               double dist =  euclidean(data[clusterList[index].get(i)],centroids[j]);
                               if(dist<distmin)
                               {
                                   distmin = dist;
                                   clusterNo = j;

                               }
                            }
                            clusters[clusterNo].add(clusterList[index].get(i));
                            System.out.print(clusterNo+" ");
                        }
                        System.out.println("\nNew Centroid:");
                        for(int i=0;i<kval;i++)
                        {
                            for(int j=0;j<acount;j++)
                            {
                                double val=0;
                                for(int k=0;k<clusters[i].size();k++)
                                {
                                    val+=data[clusters[i].get(k)][j];
                                }
                                newCentroids[i][j] = val/clusters[i].size();
                                System.out.print(newCentroids[i][j]+" ");
                            }
                            System.out.println();
                        }
                        flag = 0;
                        for(int i=0;i<kval;i++)
                        {
                            for(int j=0;j<acount;j++)
                            {
                                if(Math.abs(centroids[i][j]-newCentroids[i][j])>.01)
                                    flag=1;
                            }
                        }
                        if(flag==1)
                        {
                            for(int i=0;i<kval;i++)
                        {
                            for(int j=0;j<acount;j++)
                            {
                                centroids[i][j]=newCentroids[i][j];
                            }
                        }
                        }
                    }
                    double sum2 = clusterListSSEcalc(clusters[0])+clusterListSSEcalc(clusters[1]);
                    System.out.println("SSE "+sum2);
                    if(sum2<mina)
                    {
                        calcul[0] = new ArrayList<Integer>();
                        calcul[1] = new ArrayList<Integer>();
                        calcul[0] = clusters[0];
                        calcul[1] = clusters[1];
                        mina=sum2;
                    }
                }
                clusterList[index] = new ArrayList<Integer>();
                clusterList[index] = calcul[0];
                clusterList[numOfClusters] = calcul[1];
                clusterListSSE[index]=clusterListSSEcalc(clusterList[index]);
                clusterListSSE[numOfClusters]=clusterListSSEcalc(clusterList[numOfClusters]);

                
                for(int i=0;i<clusterList[index].size();i++)
                {
                    System.out.print(clusterList[index].get(i)+" ");
                }
                System.out.println();
                numOfClusters++;
                /*
                // Cluster Printing
                System.out.println("Cluster Info:");
                for(int j=0;j<kreal;j++)
                {
                    for(int k=0;k<clusterList[j].size();k++)
                        System.out.print(clusterList[j].get(k)+" ");
                    System.out.println();
                }
                 * */
            }

            // Cluster Printing
            System.out.println("*************");
                System.out.println("Cluster Info:");
                for(int j=0;j<kreal;j++)
                {
                    for(int k=0;k<clusterList[j].size();k++)
                        System.out.print(clusterList[j].get(k)+" ");
                    System.out.println();
                }
                 System.out.println("*************");
                double[][] centroidsent = new double[kreal][acount];
                for(int k=0;k<clusterList.length;k++)
                {
                    for(int j=0;j<acount;j++)
                    {
                        double val=0;
                        for(int i=0;i<clusterList[k].size();i++)
                        {
                            val+=data[clusterList[k].get(i)][j];
                        }
                        centroidsent[k][j] = val/clusterList[k].size();
                    }
                }

                //RKmeans op= new RKmeans(acount,ecount,kreal);
                //op.solve(centroidsent);

                

    }

    public double clusterListSSEcalc(ArrayList<Integer> clusters)
    {
            double[] centroid = new double[acount];
            for(int j=0;j<acount;j++)
            {
                double val=0;
                for(int i=0;i<clusters.size();i++)
                {
                    val+=data[clusters.get(i)][j];
                }
                centroid[j] = val/clusters.size();
            }

           double sse=0;
           for(int i=0;i<clusters.size();i++)
           {
               sse += euclidean(centroid, data[clusters.get(i)]);
           }
           return sse;

    }

    public double clusterListCentroid(ArrayList<Integer> clusters)
    {
            double[] centroid = new double[acount];
            for(int j=0;j<acount;j++)
            {
                double val=0;
                for(int i=0;i<clusters.size();i++)
                {
                    val+=data[clusters.get(i)][j];
                }
                centroid[j] = val/clusters.size();
            }

           double sse=0;
           for(int i=0;i<clusters.size();i++)
           {
               sse += euclidean(centroid, data[clusters.get(i)]);
           }
           return sse;

    }

    public double euclidean(double A[],double B[])
    {
        double dist =0;
        for(int i=0;i<acount;i++)
        {
            dist += (A[i]-B[i])*(A[i]-B[i]);
        }
        dist = Math.sqrt(dist);
        return dist;

    }

}
