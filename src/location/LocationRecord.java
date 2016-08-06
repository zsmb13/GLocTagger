package location;

import location.databinding.PlainRecordObject;

/**
 * A comparable, and therefore sortable location record class
 * Sorting is based purely on the timestamps of the records
 */
public class LocationRecord implements Comparable<LocationRecord> {

    private long timeStampMS;
    private double latitude;
    private double longitude;
    private int accuracy;

    /**
     * Ctor only for temporary records, does not store actual data!
     *
     * @param timeMS the timestamp to store, in milliseconds
     */
    LocationRecord(long timeMS) {
        this.timeStampMS = timeMS;
        this.latitude = 0;
        this.longitude = 0;
        this.accuracy = 0;
    }

    /**
     * Ctor from a POJO location record
     *
     * @param po the record to get the data from
     */
    LocationRecord(PlainRecordObject po) {
        this.timeStampMS = po.timestampMs;
        this.latitude = convertIntLatLong(po.latitudeE7);
        this.longitude = convertIntLatLong(po.longitudeE7);
        this.accuracy = po.accuracy;
    }

    /**
     * Helper function to turn integer formatted latlong coordinates into doubles
     *
     * @param latlong the coordinate to convert
     * @return the coordinate as a double value
     */
    private double convertIntLatLong(int latlong) {
        return (double) latlong * 0.0000001;
    }

    public long getTimeStampMS() {
        return timeStampMS;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public int compareTo(LocationRecord o) {
        return Long.compare(timeStampMS, o.timeStampMS);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != LocationRecord.class) {
            return false;
        }

        LocationRecord rec = (LocationRecord) obj;
        return timeStampMS == rec.timeStampMS;
    }
}
