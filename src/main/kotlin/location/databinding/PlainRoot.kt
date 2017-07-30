package location.databinding

/**
 * The root element of the Google Location takeout JSON file
 */
data class PlainRoot(
        var locations: List<PlainRecordObject>? = null
)
