import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloQuadNumExpr;
import ilog.cplex.IloCplex;

public class example_4 {
    public static void main(String[] args){
        quadratic();
    }

    public static void quadratic(){
        try {
            IloCplex model = new IloCplex();
            IloNumVar x1 = model.numVar(0,40,"X1");
            IloNumVar x2 = model.numVar(0,Double.MAX_VALUE,"X2");
            IloNumVar x3 = model.numVar(0,Double.MAX_VALUE,"X3");

            model.addMaximize(
                    model.sum(
                            x1,
                            model.prod(2,x2),
                            model.prod(3,x3),
                            model.prod(-16.5,x1,x1),
                            model.prod(-11,x2,x2),
                            model.prod(-5.5,x3,x3),
                            model.prod(6,x1,x2),
                            model.prod(11.5,x2,x3)
                    )
            );

            model.addLe(model.sum(model.prod(-1,x1),x2,x3),20);
            model.addLe(model.sum(x1,model.prod(-3,x2),x3),30);
            model.solve();
            model.end();



        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
