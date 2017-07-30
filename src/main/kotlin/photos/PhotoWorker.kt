package photos


import location.finders.LocationFinder

/**
 * Gets photos from the given PhotoManager, processes them, and writes
 * the results to the output directory
 */
class PhotoWorker(private val pm: PhotoManager, private val lf: LocationFinder) : Runnable {

    /**
     * Gets and processes pictures until there are none left
     */
    override fun run() {
        var p: Photo? = pm.next()
        while (p != null) {
            val (lat, lon) = lf.getLocation(p.getTimeStampMS())
                    ?: throw RuntimeException("No timestamp found for a photo") // TODO handle gracefully (skip?)

            p.writeExifLocation(lat, lon)
            p = pm.next()
        }
    }

}
