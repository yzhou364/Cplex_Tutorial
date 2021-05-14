import ilog.cp.*;
import ilog.concert.*;

public class Color {
    public static String[] Names = {"blue", "white", "yellow", "green"}; 
    public static void main(String[] args) {
        try {
            IloCP cp = new IloCP();
            IloIntVar Belgium = cp.intVar(0, 3);
            IloIntVar Denmark = cp.intVar(0, 3);
            IloIntVar France = cp.intVar(0, 3);
            IloIntVar Germany = cp.intVar(0, 3);
            IloIntVar Luxembourg = cp.intVar(0, 3);
            IloIntVar Netherlands = cp.intVar(0, 3);
            
            cp.add(cp.neq(Belgium , France)); 
            cp.add(cp.neq(Belgium , Germany)); 
            cp.add(cp.neq(Belgium , Netherlands));
            cp.add(cp.neq(Belgium , Luxembourg));
            cp.add(cp.neq(Denmark , Germany)); 
            cp.add(cp.neq(France , Germany)); 
            cp.add(cp.neq(France , Luxembourg)); 
            cp.add(cp.neq(Germany , Luxembourg));
            cp.add(cp.neq(Germany , Netherlands)); 
            
            if (cp.solve())
                {    
                   System.out.println();
                   System.out.println( "Belgium:     " + Names[(int)cp.getValue(Belgium)]);
                   System.out.println( "Denmark:     " + Names[(int)cp.getValue(Denmark)]);
                   System.out.println( "France:      " + Names[(int)cp.getValue(France)]);
                   System.out.println( "Germany:     " + Names[(int)cp.getValue(Germany)]);
                   System.out.println( "Luxembourg:  " + Names[(int)cp.getValue(Luxembourg)]);
                   System.out.println( "Netherlands: " + Names[(int)cp.getValue(Netherlands)]);
                }
        } catch (IloException e) {
            System.err.println("Error " + e);
        }
    }
}

