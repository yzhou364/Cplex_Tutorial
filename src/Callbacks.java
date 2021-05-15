import ilog.concert.IloException;
import ilog.cp.IloCP;

public class Callbacks {
    static public class BoundsCallback implements IloCP.Callback {
        private double _lb;
        private double _ub;
        private double _gap;
        public BoundsCallback() { init();}
        public void init(){
            _lb = Double.NEGATIVE_INFINITY;
            _ub = Double.POSITIVE_INFINITY;
            _gap = Double.POSITIVE_INFINITY;
        }

        public void invoke(IloCP cp, IloCP.Callback.Reason reason){
            try {
                System.out.println("Using callbacks now!!!!");
                if (reason == IloCP.Callback.Reason.StartSolve){
                    init();
                    System.out.println("Time\tLB\tUB\tGAP");
                    System.out.println("==============================");
                }else if (reason == IloCP.Callback.Reason.EndSolve){
                    System.out.println("End Callback");
                }
                else{
                    boolean soln = (reason == IloCP.Callback.Reason.Solution);
                    boolean bnd = (reason == IloCP.Callback.Reason.ObjBound);
                    if (soln || bnd){
                        if (_lb > Double.NEGATIVE_INFINITY && _ub <Double.POSITIVE_INFINITY)
                            System.out.println();
                        if (soln) {
                            _ub = cp.getObjValue();
                            if (_lb > Double.NEGATIVE_INFINITY){
                                _gap = cp.getObjGap();
                            }
                        }
                        else if(bnd){
                            _lb = cp.getObjBound();
                            if (_ub < Double.POSITIVE_INFINITY)
                                _gap = cp.getObjGap();
                        }
                    }
                    if (_lb > Double.NEGATIVE_INFINITY && _ub < Double.POSITIVE_INFINITY){
                        System.out.format("\r                                                  \r%.1f\t%.0f\t%.0f\t%.1f%%",
                                cp.getInfo(IloCP.DoubleInfo.SolveTime), _lb, _ub, 100 * _gap);
                        System.out.flush();
                    }
                }
            } catch(IloException ex) {
                System.err.println("FATAL:CallBack Wrong"+ex);
                System.exit(-1);
            }
        }
    };

    static void SolveWithCallBack(IloCP cp) throws IloException{
        BoundsCallback cb = new Callbacks.BoundsCallback();
        cp.addCallback(cb);
        cp.solve();
        cp.removeCallback(cb);
    }

    public static void main(String[] args){
        try{
            IloCP cp = new IloCP();
            cp.importModel("data/linebal-BARTHOL2-142-30.cpo");
            SolveWithCallBack(cp);
            cp.end();
        }catch(IloException e){
            System.out.println("Error:");
            e.printStackTrace();
        }
    }
}
