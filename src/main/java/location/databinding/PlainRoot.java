package location.databinding;

import java.util.List;

/**
 * POJO for parsing the location records from the Google-generated JSON
 * This is the root element of said JSON file
 */
public class PlainRoot {
    public List<PlainRecordObject> locations;
}
