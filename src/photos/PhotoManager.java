package photos;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zsmb on 2016-07-18.
 */
public class PhotoManager {

    private static final String[] imageFormats = {"jpg", "jpeg"};
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private List<Photo> photos;
    private int currentIndex = -1;

    public PhotoManager(File photoDir, File outputDirectory, TimeZone timeZone) {
        // TODO calculate offset by getting currentTimeMS and something else (?)

        File[] files = photoDir.listFiles(new FileFilter() {
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

        //photos = Arrays.asList(files);

        photos = new ArrayList<>();
        for(int i = 0; i < files.length; i++) {
            photos.add(new Photo(files[i], outputDirectory));
        }

        dateFormat.setTimeZone(timeZone);
    }

    private String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    public Photo getNext() {
        synchronized (photos) {
            currentIndex++;

            if(currentIndex < photos.size()) {
                return photos.get(currentIndex);
            }
        }

        return null;
    }

}
