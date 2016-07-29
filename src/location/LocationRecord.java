package location;

import location.databinding.PlainRecordObject;

/**
 * Created by zsmb on 2016-07-18.
 */
public class LocationRecord implements Comparable<LocationRecord> {

    private long timeStampMS;
    private double latitude;
    private double longitude;
    private int accuracy;

    /**
     * Ctor only for temporary records, does not store actual data!
     *
     * @param timeMS
     */
    public LocationRecord(long timeMS) {
        this.timeStampMS = timeMS;
        this.latitude = 0;
        this.longitude = 0;
        this.accuracy = 0;
    }

    public LocationRecord(PlainRecordObject po) {
        this.timeStampMS = po.timestampMs;
        this.latitude = convertIntLatLong(po.latitudeE7);
        this.longitude = convertIntLatLong(po.longitudeE7);
        this.accuracy = po.accuracy;
    }

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
        LocationRecord rec = (LocationRecord) obj;
        return timeStampMS == rec.timeStampMS;
    }
}
