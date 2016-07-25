package location;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import location.filters.RecordFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zsmb on 2016-07-23.
 */
public class RecordManager {

    private List<LocationRecord> records = new ArrayList<>();

    private RecordFilter filter;

    public RecordManager(File locationFile) {
        this.filter = new RecordFilter() {
            @Override
            public boolean test(LocationRecord record) {
                return true;
            }
        };
        loadRecords(locationFile);
    }

    public RecordManager(File locationFile, RecordFilter filter) {
        this.filter = filter;
        loadRecords(locationFile);
    }

    private void loadRecords(File locationFile) {
        if (!locationFile.exists() || !locationFile.isFile()) {
            //TODO error handling
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Ignore properties that are not in PlainRecordObject
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            PlainRoot pr = mapper.readValue(locationFile, PlainRoot.class);

            System.out.println("Read " + pr.locations.size() + " records from JSON file.");

            for (PlainRecordObject po : pr.locations) {
                LocationRecord lr = new LocationRecord(po);
                // Apply filter(s) to read records
                if (filter.accept(lr)) {
                    records.add(lr);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Necessary for the binary search used later
        Collections.sort(records);
    }

    public List<LocationRecord> getClosestRecords(long timeMS) {
        List<LocationRecord> closest = new ArrayList<>();

        int result = Arrays.binarySearch(records.toArray(), new LocationRecord(timeMS));

        // There was a single exact match
        if (result >= 0) {
            closest.add(records.get(result));
        }
        else {
            int index = -(result + 1);

            if (index > 0) {
                closest.add(records.get(index - 1));
            }
            if (index < records.size()) {
                closest.add(records.get(index));
            }
        }

        return closest;
    }

}
