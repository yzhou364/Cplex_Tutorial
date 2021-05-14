import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

public class Color{
    public static String[] colors = {"blue","white","yellow","green"};
    public static void main(String[] args){
        try {
            IloCP cp = new IloCP();

            IloIntVar x1 = cp.intVar(0,3,"Belgium");
            IloIntVar x2 = cp.intVar(0,3,"Denmark");
            IloIntVar x3 = cp.intVar(0,3,"France");
            IloIntVar x4 = cp.intVar(0,3,"Germany");
            IloIntVar x5 = cp.intVar(0,3,"Luxembourg");
            IloIntVar x6 = cp.intVar(0,3,"Netherlands");

            cp.add(cp.neq(x1,x3));
            cp.add(cp.neq(x1,x4));
            cp.add(cp.neq(x1,x5));
            cp.add(cp.neq(x1,x6));
            cp.add(cp.neq(x2,x4));
            cp.add(cp.neq(x3,x4));
            cp.add(cp.neq(x3,x5));
            cp.add(cp.neq(x4,x5));
            cp.add(cp.neq(x4,x6));

            if(cp.solve()){
                System.out.println();
                System.out.println("Belgium:  "+colors[(int)cp.getValue(x1)]);
                System.out.println("Denmark:  "+colors[(int)cp.getValue(x2)]);
                System.out.println("France:  "+colors[(int)cp.getValue(x3)]);
                System.out.println("Germany:  "+colors[(int)cp.getValue(x4)]);
                System.out.println("Luxembourg:  "+colors[(int)cp.getValue(x5)]);
                System.out.println("Netherlands:  "+colors[(int)cp.getValue(x6)]);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}