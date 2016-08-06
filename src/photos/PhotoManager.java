package photos;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Reads photos from a directory, stores them, provides threadsafe access to them for the workers
 */
public class PhotoManager {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final String[] imageFormats = {"jpg", "jpeg"};
    private final List<Photo> photos;
    private int currentIndex = -1;

    /**
     * PhotoManager ctor
     *
     * @param inputDirectory  the directory containing the photos to be read
     * @param outputDirectory the directory to write the processed photos to
     * @param timeZone        the timezone that the photos were taken in
     */
    public PhotoManager(File inputDirectory, File outputDirectory, TimeZone timeZone) {
        File[] files = inputDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isFile()) {
                    return false;
                }

                String extension = getExtension(pathname);

                for (String imageFormat : imageFormats) {
                    if (imageFormat.equals(extension)) {
                        return true;
                    }
                }

                return false;
            }
        });

        photos = new ArrayList<>();

        for (File file : files) {
            photos.add(new Photo(file, outputDirectory));
        }

        dateFormat.setTimeZone(timeZone);
    }

    /**
     * Gets the extension of a file, lowercase
     *
     * @param file the given file
     * @return the extension in lowercase, without a '.'
     */
    private String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Fetches the next photo to process
     *
     * @return the next photo, or null, if there are none left.
     */
    public Photo getNext() {
        synchronized (photos) {
            currentIndex++;

            if (currentIndex < photos.size()) {
                return photos.get(currentIndex);
            }
        }

        return null;
    }

}
