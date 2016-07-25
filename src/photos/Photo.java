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
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import java.io.*;
import java.text.ParseException;

/**
 * Created by zsmb on 2016-07-25.
 */
public class Photo {

    private final File photoFile;
    private final File outputDir;

    private static final Object syncObject = new Object();

    public Photo(File photoFile, File outputDir) {
        this.photoFile = photoFile;
        this.outputDir = outputDir;
    }


    public long getTimestampMS() {
        // Because Sanselan is grumpy when you read metadata from multiple threads
        // at the same time
        synchronized (syncObject) {
            try {
                // Read data from image
                IImageMetadata metadata = Sanselan.getMetadata(photoFile);
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                TiffField dateField = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE);

                String dateString = dateField.getStringValue();
                //System.out.println("Read photo w/ date " + dateString);
                //System.out.println("Time is " + dateFormat.parse(dateString).getTime());

                return PhotoManager.dateFormat.parse(dateString).getTime();

            } catch (ImageReadException | IOException | ParseException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public void writeExifLocation(final double latitude, final double longitude) {
        OutputStream os = null;
        TiffOutputSet outputSet = null;

        try {
            // Read data from image
            IImageMetadata metadata = Sanselan.getMetadata(photoFile);
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
            final File outputFile = new File(outputDir + File.separator + photoFile.getName());
            os = new FileOutputStream(outputFile);
            os = new BufferedOutputStream(os);

            ExifRewriter exifRewriter = new ExifRewriter();
            exifRewriter.updateExifMetadataLossless(photoFile, os, outputSet);

            os.flush();

        } catch (ImageReadException | IOException | ImageWriteException e) {
            e.printStackTrace();
        }
    }
}
