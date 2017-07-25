package location

import location.databinding.PlainRecordObject

class LocationRecord @JvmOverloads constructor(
        val timeStampMS: Long,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val accuracy: Int = 0)
    : Comparable<LocationRecord> {

    companion object {

        /**
         * Converts integer latlong coordinates into doubles
         * e.g. 135590300 to 13.5590300
         */
        private fun Int.asCoordinate() = this * 0.0000001

        @JvmStatic
        fun from(plainRecordObject: PlainRecordObject) =
                LocationRecord(
                        plainRecordObject.timestampMs,
                        plainRecordObject.latitudeE7.asCoordinate(),
                        plainRecordObject.longitudeE7.asCoordinate(),
                        plainRecordObject.accuracy
                )
    }

    override fun compareTo(other: LocationRecord) = timeStampMS.compareTo(other.timeStampMS)

}
