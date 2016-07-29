import location.RecordManager;
import location.finders.LocationFinder;
import location.finders.SimpleFinder;
import photos.PhotoManager;
import photos.PhotoWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsmb on 2016-07-17.
 */
public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        boolean success = ParamProcessor.check(args);
        if (!success) {
            return;
        }

        RecordManager rm = ParamProcessor.getRecordManager();
        PhotoManager pm = ParamProcessor.getPhotomanager();
        LocationFinder lf = new SimpleFinder(rm);

        //int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        int threadCount = 1;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(new PhotoWorker(pm, lf)));
        }

        threads.forEach(Thread::start);

        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lf.printStats();

        System.out.println();
        System.out.println("TIME TAKEN");
        System.out.println(System.currentTimeMillis() - time);
    }

}
