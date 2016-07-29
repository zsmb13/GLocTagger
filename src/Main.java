import location.RecordManager;
import location.finders.LocationFinder;
import location.finders.SimpleFinder;
import photos.PhotoManager;
import photos.PhotoWorker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zsmb on 2016-07-17.
 */
public class Main {

    private static void displayHelp() {
        File help = new File("res/help.txt");

        try {
            Scanner scanner = new Scanner(help);
            while(scanner.hasNextLine()) {
                System.err.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        boolean success = ParamProcessor.check(args);
        if (!success) {
            displayHelp();
            return;
        }

        RecordManager rm = ParamProcessor.getRecordManager();
        PhotoManager pm = ParamProcessor.getPhotoManager();
        LocationFinder lf = new SimpleFinder(rm);

        /*
          Multithreaded use is implemented and working, but is not advised, since
           the program is very heavily IO bound, it does much better on a single
           thread, where the storage device can perform sequential writes
         */
        //int threadCount = Runtime.getRuntime().availableProcessors();
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
        System.out.println("Job complete, time taken : " + (System.currentTimeMillis() - time) + "ms.");
    }

}
