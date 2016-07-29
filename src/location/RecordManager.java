package location;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import location.databinding.PlainRecordObject;
import location.databinding.PlainRoot;
import location.filters.RecordFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Reads location records from file, stores them, provides lookup access to get records
 */
public class RecordManager {

    private List<LocationRecord> records = new ArrayList<>();

    private RecordFilter filter;

    /**
     * Ctor without a filter parameter, adds a filter that accepts any record
     * @param locationFile the file to read records from
     */
    public RecordManager(File locationFile) {
        this.filter = new RecordFilter() {
            @Override
            public boolean test(LocationRecord record) {
                return true;
            }
        };
        loadRecords(locationFile);
    }

    /**
     * Ctor with a filter parameter
     * @param locationFile the file to read records from
     * @param filter the filter to use for the read records
     */
    public RecordManager(File locationFile, RecordFilter filter) {
        this.filter = filter;
        loadRecords(locationFile);
    }

    /**
     * Loads the records from the given JSON file, applying the stored filter to them
     * @param locationFile the file to read records from
     */
    private void loadRecords(File locationFile) {
        if (!locationFile.exists() || !locationFile.isFile()) {
            System.err.println("No records loaded, check your records file.");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Ignore properties that are not in PlainRecordObject
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Read records from JSON file
            PlainRoot pr = mapper.readValue(locationFile, PlainRoot.class);

            System.out.println("Read " + pr.locations.size() + " records from JSON file.");

            // Create the complex LocationRecord objects
            for (PlainRecordObject po : pr.locations) {
                LocationRecord lr = new LocationRecord(po);
                // Apply filter(s) to read records
                if (filter.accept(lr)) {
                    records.add(lr);
                }
            }

            System.out.println("Number of records after filtering: " + records.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Necessary for the binary search that's used later
        Collections.sort(records);
    }

    /**
     * Gets the (up to two) bounding records for a given timestamp
     * @param timeMS the timestamp to look up
     * @return a list of the found records (up to two)
     */
    public List<LocationRecord> getClosestRecords(long timeMS) {
        List<LocationRecord> closest = new ArrayList<>();

        // Find index where the timestamp would be inserted into the array of records
        int result = Arrays.binarySearch(records.toArray(), new LocationRecord(timeMS));

        // There was a single exact match
        if (result >= 0) {
            closest.add(records.get(result));
        }
        else {
            int index = -(result + 1);

            // The index returned was not the first one
            // This adds the record with the timestamp before the given one
            if (index > 0) {
                closest.add(records.get(index - 1));
            }

            // The index was still in the list
            // This adds the record with the timestamp after the given one
            if (index < records.size()) {
                closest.add(records.get(index));
            }
        }

        return closest;
    }

}
