package location;

/**
 * Created by zsmb on 2016-07-23.
 */
public class LinearApproximator implements LocationFinder {

    private RecordManager rm;

    public LinearApproximator(RecordManager rm) {
        this.rm = rm;
    }


    @Override
    public double[] getLocation(long timeMS) {


        //TODO make this not an empty array
        return new double[]{};
    }

    @Override
    public void printStats() {

    }
}
