package location.filters


import location.LocationRecord

/**
 * Filters records that are inside or outside a given radius around a given point
 */
class LocationFilter(private val latitude: Double,
                     private val longitude: Double,
                     private val radius: Double,
                     private val acceptInside: Boolean) : RecordFilter() {

    companion object {
        private const val earthRad = 6371.0088 // Earth's mean radius in kms
    }

    override fun test(record: LocationRecord): Boolean {
        val dist = dist(record.latitude, record.longitude)

        return when {
            acceptInside ->
                dist <= radius
            else ->
                dist >= radius
        }
    }

    private fun dist(lat: Double, lon: Double): Double {
        val rLat1 = degToRad(lat)
        val rLon1 = degToRad(lon)
        val rLat2 = degToRad(latitude)
        val rLon2 = degToRad(longitude)

        // Half of delta latitude
        val hdLat = (rLat1 - rLat2) / 2
        // Half of delta longitude
        val hdLon = (rLon1 - rLon2) / 2

        val a = Math.sin(hdLat) * Math.sin(hdLat) + Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(hdLon) * Math.sin(hdLon)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a))

        return c * earthRad
    }

    private fun degToRad(deg: Double) = deg / 180.0 * Math.PI

}
