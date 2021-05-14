import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class example_3 {
    public static void main(String[] args){
        solve_Me(100);
    }


    public static void solve_Me(int n){
        double[] xPos = new double[n];
        double[] yPos = new double[n];
        for(int i =0;i<n;i++){
            xPos[i] = Math.random();
            yPos[i] = Math.random();
        }
        double[][] c = new double[n][n];
        for(int i =0;i<n;i++){
            for(int j = 0;j<n;j++){
                c[i][j] = Math.sqrt(Math.pow(xPos[i]-xPos[j],2)+Math.pow(yPos[i]-yPos[j],2));
            }
        }

        try {
            IloCplex model = new IloCplex();

            IloNumVar[][] x = new IloNumVar[n][];
            for (int i =0;i<n;i++){
                x[i] = model.boolVarArray(n);
            }
            IloNumVar[] u = model.numVarArray(n,0,Double.MAX_VALUE);

            IloLinearNumExpr obj = model.linearNumExpr();
            for (int i=0;i<n;i++){
                for (int j = 0;j<n;j++){
                    if (j!=i){
                        obj.addTerm(c[i][j],x[i][j]);
                    }
                }
            }

            model.addMinimize(obj);

            for(int j=0;j<n;j++){
                IloLinearNumExpr expr = model.linearNumExpr();
                for (int i=0;i<n;i++){
                    if(i!=j){
                        expr.addTerm(1,x[i][j]);
                    }
                }
                model.addEq(expr,1.0);
            }

            for(int i=0;i<n;i++){
                IloLinearNumExpr expr = model.linearNumExpr();
                for (int j=0;j<n;j++){
                    if(i!=j){
                        expr.addTerm(1,x[i][j]);
                    }
                }
                model.addEq(expr,1.0);
            }


            for (int i=1;i<n;i++){
                for(int j=1;j<n;j++){
                    IloLinearNumExpr expr = model.linearNumExpr();
                    expr.addTerm(1,u[i]);
                    expr.addTerm(-1,u[j]);
                    expr.addTerm(n-1,x[i][j]);
                    model.addLe(expr,n-2);
                }
            }

            model.setParam(IloCplex.Param.MIP.Tolerances.MIPGap,0.1);
            model.setParam(IloCplex.Param.TimeLimit,15);


            model.solve();
            model.end();

        } catch (IloException e) {
            e.printStackTrace();
        }

    }
}
