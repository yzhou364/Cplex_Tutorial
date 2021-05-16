import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

public class CPOFileFormat {

    public static String[] colors = {"blue","white","yellow","green"};
    public static void createModel(String filename){
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

            cp.dumpModel(filename);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void solveModel(String filename){
        try {
            IloCP cp = new IloCP();
            cp.importModel(filename);
            cp.getIloIntVar("x3").setUB(0);
            if (cp.solve()){
                System.out.println("Solution:");
                IloIntVar[] vars = cp.getAllIloIntVars();
                for(int i=0; i<vars.length;i++){
                    System.out.println(vars[i]);
                }
            }
        } catch (Exception e){
            System.err.println("Error"+e);
        }
    }


    public static void main(String[] args){
        String filename = (args.length > 0 ? args[0]: "CPOFileFormat.cpo");
        createModel(filename);
        solveModel(filename);
    }
}
