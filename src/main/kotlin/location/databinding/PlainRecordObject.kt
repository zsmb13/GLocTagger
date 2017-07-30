package location.databinding

/**
 * Model of the array item in the Google Location takeout JSON file
 * Properties not present here are thrown away at parsing time
 */
data class PlainRecordObject(
        var timestampMs: Long = 0,
        var latitudeE7: Int = 0,
        var longitudeE7: Int = 0,
        var accuracy: Int = 0
)
