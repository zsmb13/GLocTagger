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
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zsmb on 2016-07-18.
 */
public class PhotoManager {

    private static final String[] imageFormats = {"jpg", "jpeg"};
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private final File outputDir;

    private List<File> photos;
    private int currentIndex = -1;

    private int hourOffset;

    public PhotoManager(File photoDir, File outputDirectory, int hourOffset) {
        // TODO calculate offset by getting currentTimeMS and something else (?)

        this.outputDir = outputDirectory;

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

        photos = Arrays.asList(files);

        this.hourOffset = hourOffset;
        String timeZoneString = "GMT" + (hourOffset < 0 ? "" : "+") + hourOffset;
        System.out.println(timeZoneString);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneString));
    }

    private String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Checks if there are still photos to process
     *
     * @return true if there's at least one photo left
     */
    public boolean next() {
        currentIndex++;
        return currentIndex < photos.size();
    }

    /**
     * Returns the time the photo at the current index was taken
     *
     * @return the unix timestamp in milliseconds, in the time zone that the pictures was taken in!
     */
    public long getCurrentTimestampMS() {
        final File jpegImage = photos.get(currentIndex);

        try {
            // Read data from image
            IImageMetadata metadata = Sanselan.getMetadata(jpegImage);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            TiffField dateField = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE);

            String dateString = dateField.getStringValue();
            //System.out.println("Read photo w/ date " + dateString);
            //System.out.println("Time is " + dateFormat.parse(dateString).getTime());

            return dateFormat.parse(dateString).getTime();

        } catch (ImageReadException | IOException | ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void testData(JpegImageMetadata data, TagInfo tagInfo) {
        System.out.println(tagInfo.name);
        System.out.println(data.findEXIFValue(tagInfo));
    }

    public void writeCurrentExifData(final double latitude, final double longitude) {
        OutputStream os = null;
        TiffOutputSet outputSet = null;

        final File jpegImage = photos.get(currentIndex);

        try {
            // Read data from image
            IImageMetadata metadata = Sanselan.getMetadata(jpegImage);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            if (jpegMetadata != null) {
                TiffImageMetadata exif = jpegMetadata.getExif();

                if (exif != null) {
                    outputSet = exif.getOutputSet();
                }
            }

            // Change data
            outputSet.setGPSInDegrees(longitude, latitude);

            // Write data to new file
            final File outputFile = new File(outputDir + File.separator + jpegImage.getName());
            os = new FileOutputStream(outputFile);
            os = new BufferedOutputStream(os);

            ExifRewriter exifRewriter = new ExifRewriter();
            exifRewriter.updateExifMetadataLossless(jpegImage, os, outputSet);

            os.flush();

        } catch (ImageReadException | IOException | ImageWriteException e) {
            e.printStackTrace();
        }
    }
}
