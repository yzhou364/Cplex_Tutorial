import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloSolution;
import ilog.cp.IloCP;

import java.io.*;

public class PlantLocation {
    public static class DataReader{
        private StreamTokenizer st;

        public DataReader(String fileName) throws IOException{
            FileInputStream fstream = new FileInputStream(fileName);
            Reader r = new BufferedReader(new InputStreamReader(fstream));
            st = new StreamTokenizer(r);
        }

        public int next() throws IOException{
            st.nextToken();
            return (int) st.nval;
        }
    }

    public static void main(String[] args) throws IOException, IloException{
        IloCP cp = new IloCP();

        DataReader data = new DataReader("data/facility.data");
        int nbCustomer = data.next();
        int nbLocation = data.next();

        int[][] cost = new int[nbCustomer][];
        for(int c=0;c<nbCustomer;c++){
            cost[c] = new int[nbLocation];
            for(int w=0;w<nbLocation;w++){
                cost[c][w] = data.next();
            }
        }

        int[] demand = new int[nbCustomer];
        int totalDemand = 0;
        for(int c=0;c<nbCustomer;c++){
            demand[c] = data.next();
            totalDemand += demand[c];
        }
        int[] fixedCost = new int[nbLocation];
        for(int w=0;w<nbLocation;w++){
            fixedCost[w] = data.next();
        }
        int[] capacity = new int[nbLocation];
        for(int w=0;w<nbLocation;w++){
            capacity[w] = data.next();
        }


        IloIntVar cust[] = new IloIntVar[nbCustomer];
        for(int c=0;c<nbCustomer;c++){
            cust[c] = cp.intVar(0,nbLocation-1);
        }
        IloIntVar[] open = new IloIntVar[nbLocation];
        IloIntVar[] load = new IloIntVar[nbLocation];
        for(int w=0;w<nbLocation;w++){
            open[w] = cp.intVar(0,1);
            load[w] = cp.intVar(0,capacity[w]);
            cp.add(cp.eq(open[w],cp.gt(load[w],0))); /// 1>=0 or1
        }
        cp.add(cp.pack(load,cust,demand));

        IloNumExpr obj = cp.scalProd(fixedCost,open);
        for(int c=0;c<nbCustomer;c++){
            obj = cp.sum(obj,cp.element(cost[c],cust[c]));
        }
        cp.add(cp.minimize(obj));

        cp.addKPI(cp.quot(totalDemand,cp.scalProd(open,capacity)),"Mean occupancy");
        IloNumExpr[] usage = new IloNumExpr[nbLocation];
        for(int w=0;w<nbLocation;w++){
            usage[w] = cp.sum(cp.quot(load[w],capacity[w]),cp.diff(1,open[w]));
        }
        cp.addKPI(cp.min(usage),"Min capacity");
        int[] custValues = {
                19, 0, 11, 8, 29, 9, 29, 28, 17, 15, 7, 9, 18, 15, 1, 17, 25, 18, 17, 27,
                22, 1, 26, 3, 22, 2, 20, 27, 2, 16, 1, 16, 12, 28, 19, 2, 20, 14, 13, 27,
                3, 9, 18, 0, 13, 19, 27, 14, 12, 1, 15, 14, 17, 0, 7, 12, 11, 0, 25, 16,
                22, 13, 16, 8, 18, 27, 19, 23, 26, 13, 11, 11, 19, 22, 28, 26, 23, 3, 18, 23,
                26, 14, 29, 18, 9, 7, 12, 27, 8, 20 };

        IloSolution sol = cp.solution();
        for (int c = 0; c < nbCustomer; c++) {
            sol.setValue(cust[c], custValues[c]);
        }

        cp.setStartingPoint(sol);
        cp.setParameter(IloCP.DoubleParam.TimeLimit, 10);
        cp.setParameter(IloCP.IntParam.LogPeriod, 10000);
        cp.solve();




    }

}
