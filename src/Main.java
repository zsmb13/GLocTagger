import location.RecordManager;
import location.finders.LocationFinder;
import location.finders.SimpleFinder;
import photos.PhotoManager;
import photos.PhotoWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the program, creates instances, runs photo workers
 */
public class Main {

    /**
     * Displays the contents of the help file on the error output
     */
    private static void displayHelp() {
        File help = new File("res/help.txt");

        try {
            Scanner scanner = new Scanner(help);
            while (scanner.hasNextLine()) {
                System.err.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates components, runs worker threads
     *
     * @param args arguments from the command line
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        boolean success = ParamProcessor.parse(args);
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

        System.out.println("---");
        System.out.println("Job complete, time taken: " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
