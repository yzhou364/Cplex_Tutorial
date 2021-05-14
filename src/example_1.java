import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

public class example_1 {

    public static void main(String[] args){
        model_1();
    }

    public static void model_1(){
        try {
            IloCplex model = new IloCplex();

            IloNumVar x = model.numVar(0,Double.MAX_VALUE,"x");
            IloNumVar y = model.numVar(0,Double.MAX_VALUE,"y");

            IloLinearNumExpr objective = model.linearNumExpr();
            objective.addTerm(0.12,x);
            objective.addTerm(0.15,y);
            // define objective
            model.addMinimize(objective);
            // define constraint

            List<IloRange> constraints = new ArrayList<IloRange>();
            constraints.add(model.addGe(model.sum(model.prod(60,x),model.prod(60,y)),300));
            constraints.add(model.addGe(model.sum(model.prod(12,x),model.prod(6,y)),36));
            model.addGe(model.sum(model.prod(10,x),model.prod(30,x)),90);

            IloLinearNumExpr num_expr = model.linearNumExpr();
            num_expr.addTerm(2,x);
            num_expr.addTerm(-1,y);
            model.addEq(num_expr,0);

            num_expr = model.linearNumExpr();
            num_expr.addTerm(1,y);
            num_expr.addTerm(-1,x);
            model.addLe(num_expr,8);

            model.setParam(IloCplex.IntParam.Simplex.Display,0);

            if (model.solve()){
                System.out.println("Objective ="+model.getObjValue());
                System.out.println("X = "+model.getValue(x));
                System.out.println("Y = "+model.getValue(y));
                for(int i=0;i<constraints.size();i++){
                    System.out.println("Dual constaint"+(i+1)+"="+model.getDual(constraints.get(i)));
                    System.out.println("Slack constrant"+(i+1)+"="+model.getSlack(constraints.get(i)));
                }
            }
            else{
                System.out.println("Model not solved");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }

    }
}
