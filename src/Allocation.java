import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

public class Allocation {
    static final int nbCell = 25;
    static final int nbAvailFreq = 256;
    static final int[] nbChannel = {
            8,6,6,1,4,4,8,8,8,8,4,9,8,4,4,10,8,9,8,4,5,4,8,1,1
    };
    static final int[][] dist = {
            { 16,1,1,0,0,0,0,0,1,1,1,1,1,2,2,1,1,0,0,0,2,2,1,1,1 },
            { 1,16,2,0,0,0,0,0,2,2,1,1,1,2,2,1,1,0,0,0,0,0,0,0,0 },
            { 1,2,16,0,0,0,0,0,2,2,1,1,1,2,2,1,1,0,0,0,0,0,0,0,0 },
            { 0,0,0,16,2,2,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1 },
            { 0,0,0,2,16,2,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1 },
            { 0,0,0,2,2,16,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1 },
            { 0,0,0,0,0,0,16,2,0,0,1,1,1,0,0,1,1,1,1,2,0,0,0,1,1 },
            { 0,0,0,0,0,0,2,16,0,0,1,1,1,0,0,1,1,1,1,2,0,0,0,1,1 },
            { 1,2,2,0,0,0,0,0,16,2,2,2,2,2,2,1,1,1,1,1,1,1,0,1,1 },
            { 1,2,2,0,0,0,0,0,2,16,2,2,2,2,2,1,1,1,1,1,1,1,0,1,1 },
            { 1,1,1,0,0,0,1,1,2,2,16,2,2,2,2,2,2,1,1,2,1,1,0,1,1 },
            { 1,1,1,0,0,0,1,1,2,2,2,16,2,2,2,2,2,1,1,2,1,1,0,1,1 },
            { 1,1,1,0,0,0,1,1,2,2,2,2,16,2,2,2,2,1,1,2,1,1,0,1,1 },
            { 2,2,2,0,0,0,0,0,2,2,2,2,2,16,2,1,1,1,1,1,1,1,1,1,1 },
            { 2,2,2,0,0,0,0,0,2,2,2,2,2,2,16,1,1,1,1,1,1,1,1,1,1 },
            { 1,1,1,0,0,0,1,1,1,1,2,2,2,1,1,16,2,2,2,1,2,2,1,2,2 },
            { 1,1,1,0,0,0,1,1,1,1,2,2,2,1,1,2,16,2,2,1,2,2,1,2,2 },
            { 0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,2,2,16,2,2,1,1,0,2,2 },
            { 0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,16,2,1,1,0,2,2 },
            { 0,0,0,1,1,1,2,2,1,1,2,2,2,1,1,1,1,2,2,16,1,1,0,1,1 },
            { 2,0,0,0,0,0,0,0,1,1,1,1,1,1,1,2,2,1,1,1,16,2,1,2,2 },
            { 2,0,0,0,0,0,0,0,1,1,1,1,1,1,1,2,2,1,1,1,2,16,1,2,2 },
            { 1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,16,1,1 },
            { 1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,1,2,2,1,16,2 },
            { 1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,1,2,2,1,2,16 }
    };

    public static int getTransmitterIndex(int cell, int channel){
        int idx = 0;
        int c = 0;
        while (c<cell){
            idx += nbChannel[c++];
        }
        return (idx+channel);
    }

    public static void main(String[] args){
        try {
            IloCP cp = new IloCP();
            int nbTransmitters = getTransmitterIndex(nbCell,0);
            IloIntVar[] freq = cp.intVarArray(nbTransmitters,0,nbAvailFreq-1,"Freq");

            for(int cell=0;cell<nbCell;cell++){
                for(int channel_1 = 0; channel_1< nbChannel[cell]; channel_1++){
                    for(int channel_2 = channel_1+1;channel_2<nbChannel[cell];channel_2++){
                        cp.add(cp.ge(cp.abs(cp.diff(freq[getTransmitterIndex(cell,channel_1)],freq[getTransmitterIndex(cell,channel_2)])),16));
                    }
                }
            }

            for (int cell1 = 0; cell1 < nbCell; cell1++){
                for (int cell2 = cell1+1; cell2 < nbCell; cell2++)
                    if (dist[cell1][cell2] > 0)
                        for (int channel1 = 0; channel1 < nbChannel[cell1]; channel1++)
                            for (int channel2 = 0; channel2 < nbChannel[cell2]; channel2++)
                                cp.add(cp.ge(cp.abs(cp.diff(freq[getTransmitterIndex(cell1, channel1)],
                                        freq[getTransmitterIndex(cell2, channel2)])),
                                        dist[cell1][cell2]));
            }

            IloIntExpr nbFreq = cp.countDifferent(freq);
            cp.add(cp.minimize(nbFreq));

            cp.setParameter(IloCP.IntParam.CountDifferentInferenceLevel,IloCP.ParameterValues.Extended);
            cp.setParameter(IloCP.IntParam.FailLimit,400000);
            cp.setParameter(IloCP.IntParam.LogPeriod,100000);



            if (cp.solve()) {
                for (int cell = 0; cell < nbCell; cell++) {
                    for (int channel = 0; channel < nbChannel[cell]; channel++)
                        System.out.print((int)cp.getValue(freq[getTransmitterIndex(cell, channel)])
                                + "  " );
                    System.out.println();
                }
                System.out.println("Total # of sites       " + nbTransmitters);
                System.out.println("Total # of frequencies " + (int)cp.getValue(nbFreq));

            } else
                System.out.println("No solution");
            cp.end();






        }catch(Exception e){
            System.out.println("Problem has no solution");
        }
    }
}
