import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class example_2 {
    public static void main(String[] args){
        solve_me();
    }
    public static void solve_me(){
        int n = 4;
        int m = 3;

        double [] p = {310.0,380.0,350.0,285.0};
        double [] v = {480.0,650.0,580.0,390.0};
        double [] a = {18.0,15,23,12};
        double [] c = {10,16,8};
        double [] V = {6800,8700,5300};

        try{
            IloCplex model = new IloCplex();

            IloNumVar[][] x = new IloNumVar[n][];
            for(int i = 0;i<n;i++){
                x[i] = model.numVarArray(m,0,Double.MAX_VALUE);
            }
            IloNumVar y = model.numVar(0,Double.MIN_VALUE);

            IloLinearNumExpr[] usedWeightCapacity = new IloLinearNumExpr[m];
            IloLinearNumExpr[] usedVolumeCapacity = new IloLinearNumExpr[m];
            for(int j = 0;j<m;j++){
                usedWeightCapacity[j] = model.linearNumExpr();
                usedVolumeCapacity[j] = model.linearNumExpr();
                for (int i = 0;i<n;i++){
                    usedWeightCapacity[j].addTerm(1.0,x[i][j]);
                    usedVolumeCapacity[j].addTerm(v[i],x[i][j]);
                }
            }

            IloLinearNumExpr objective = model.linearNumExpr();
            for(int i = 0;i<n;i++){
                for(int j = 0;j<m;j++) {
                    objective.addTerm(p[i], x[i][j]);
                }
            }

            model.addMaximize(objective);
            for(int i = 0;i<n;i++){
                model.addLe(model.sum(x[i]),a[i]);
            }
            for(int i = 0;i<m;i++){
                model.addLe(usedWeightCapacity[i],c[i]);
                model.addLe(usedVolumeCapacity[i],v[i]);
                model.addEq(model.prod(1/c[i],usedWeightCapacity[i]),y);
            }


            if (model.solve()){
                System.out.print("Obejctive ="+model.getObjValue());
            }
            else{
                System.out.print("Model not solved");
            }
            model.end();
        }

        catch(IloException exc){
            exc.printStackTrace();
        }
    }
}
