# GLocTagger

This program uses your Google Location data that you can download from Google Takeout to add GPS data to your JPG photos. This is useful if you're taking pictures with a camera that doesn't have GPS built in, but your location is recorded by your phone.

You can download your location history from Takeout [here](https://takeout.google.com/settings/takeout/custom/location_history).

### Usage
`java -jar GLocTagger.jar <location data json> <input directory> <output directory> <time offset from utc> [optional args]`
### Optional arguments

##### Accuracy filter `-a, --accuracy`

Defines a maximum amount of uncertainty for location records to allow.

`-a <max value in meters>`

Example, only use records that are accurate within 40 meters:

`-a 40`

#### Location filters

##### Restrict filter `-r, --restrict`

Restricts the used records to those that are within a given radius of a given pair of coordinates.

`-r <latitude> <longitude> <radius in kilometers>`

Example, only use records within 2 kilometers of The White House:

`-r 38.897440 -77.036584 2`

Note that you can not use multiple restrict filters together to mark multiple circles on a map that are allowed.

##### Exclude filter `-e, --exclude`

Ignores location records within a given radius of a given pair of coordinates.

`-e <latitude> <longitude> <radius in kilometers>`

Example, ignore location records within 10 kilometers of The White House:

`-e 38.897440 -77.036584 10`

#### Time filters

Both filters use ISO-8601, the YYYY-MM-DD format.

##### From filter `-f, --from`

Defines a start date for the records to consider (inclusive).

`-f <start date>`

##### Until filter `-u, --until`

Defines an end date for the records to consider (inclusive).

`-u <end date>`

Combined example, only look for records that happened from Christmas until the end of the year (again, inclusive of the bounds):

`-f 2015-12-24 -u 2015-12-31`
