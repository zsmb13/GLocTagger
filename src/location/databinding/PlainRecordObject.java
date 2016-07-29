package location.databinding;

/**
 * POJO for parsing the location records from the Google-generated JSON
 * Aforementioned JSON file contains mainly an array of these types of records
 * Properties not used by this program are thrown away at parsing time
 */
public class PlainRecordObject {
    public long timestampMs;
    public int latitudeE7;
    public int longitudeE7;
    public int accuracy;
}
