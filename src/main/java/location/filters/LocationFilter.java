package location.filters;


import location.LocationRecord;

/**
 * Filters records that are inside or outside a given radius around a given point
 */
public class LocationFilter extends RecordFilter {

    // Earth's mean radius in kms
    private static final double earthRad = 6371.0088;

    private double latitude;
    private double longitude;
    private double radius;
    private boolean acceptInside;

    public LocationFilter(double latitude, double longitude, double radius, boolean acceptInside) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.acceptInside = acceptInside;
    }

    @Override
    protected boolean test(LocationRecord record) {
        double dist = dist(record.getLatitude(), record.getLongitude());
        return acceptInside ? (dist <= radius) : (dist >= radius);
    }

    private double dist(double lat, double lon) {
        double rLat1 = degToRad(lat);
        double rLon1 = degToRad(lon);
        double rLat2 = degToRad(latitude);
        double rLon2 = degToRad(longitude);

        // Half of delta latitude
        double hdLat = (rLat1 - rLat2) / 2;
        // Half of delta longitude
        double hdLon = (rLon1 - rLon2) / 2;

        double a = Math.sin(hdLat) * Math.sin(hdLat) +
                Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(hdLon) * Math.sin(hdLon);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return c * earthRad;
    }

    private double degToRad(double deg) {
        return deg / 180.0 * Math.PI;
    }

}
