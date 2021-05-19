import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
import ilog.cp.IloCP;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.*;

public class FacilityExplanations {

    public static String[] Colors = {"blue","white","yellow","green"};

    public static class DataReader{
        private StreamTokenizer st;

        public DataReader(String filename) throws IOException{
            FileInputStream fstream = new FileInputStream(filename);
            Reader r = new BufferedReader(new InputStreamReader(fstream));
            st = new StreamTokenizer(r);
        }

        public int next() throws IOException{
            st.nextToken();
            return (int) st.nval;
        }
    }

    public static void main(String[] args) throws IOException{
        String filename;
        if (args.length >0)
            filename = args[0];
        else
            filename = "data/facility.data";
        try{
            IloCP cp = new IloCP();
            int p,q;

            DataReader data = new DataReader(filename);

            int nbLocations = data.next();
            int nbStores = data.next();
            int[] capacity = new int[nbLocations];
            int[] fixedCost = new int[nbLocations];
            int[][] cost = new int[nbStores][];

            for(int i=0;i<nbStores;i++){
                cost[i] = new int[nbLocations];
            }
            for(int j=0;j<nbLocations;j++){
                capacity[j] = data.next();
            }
            for(int j=0;j<nbLocations;j++){
                fixedCost[j] = data.next();
            }
            for(int i=0;i<nbStores;i++){
                for(int j=0;j<nbLocations;j++){
                    cost[i][j] = data.next();
                }
            }

            IloIntVar[] supplier = cp.intVarArray(nbStores,0,nbLocations-1);
            IloIntVar[] open = cp.intVarArray(nbLocations,0,1);

            for(p=0;p<nbStores;p++){
                cp.add(cp.eq(cp.element(open,supplier[p]),1));
            }
            for(q=0;q<nbLocations;q++){
                cp.add(cp.le(cp.count(supplier,q),capacity[q]));
            }

            IloIntExpr obj = cp.scalProd(open,fixedCost);
            for(p=0;p<nbStores;p++){
                obj = cp.sum(obj,cp.element(cost[p],supplier[p]));
            }

            cp.add(cp.minimize(obj));

            cp.setParameter(IloCP.IntParam.Workers, 1);
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogPeriod, 1);
            cp.setParameter(IloCP.IntParam.LogSearchTags, IloCP.ParameterValues.On);
            cp.solve();

            cp.clearExplanations();
            cp.explainFailure(15);
            cp.explainFailure(20);
            int failureArray[] = {3, 10, 11, 12};
            cp.explainFailure(failureArray);
            cp.solve();

            cp.clearExplanations();
            cp.explainFailure(1);
            cp.solve();

            System.out.println();
            System.out.println("Optimal value: " + (int) cp.getValue(obj));
            for (q = 0; q < nbLocations; q++) {
                if (cp.getValue(open[q]) == 1) {
                    System.out.print("Facility " + q
                            + " is open, it serves stores ");
                    for (p = 0; p < nbStores; p++) {
                        if (cp.getValue(supplier[p]) == q)
                            System.out.print(p + " ");
                    }
                    System.out.println();
                }
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
