ÖffiWatch [![Build Status](https://travis-ci.org/Timmeey/OeffiWatch.svg?branch=master)](https://travis-ci.org/Timmeey/OeffiWatch)
=====
A Library that can grab the estimated/scheduled departure times for public transport, from websites. Currently only Berlin(Germany) is supported.

Download
--------

Until this gets moved to a more appropriate place, just clone the Repository and build the Library yorself
by running
```
git clone github.com:Timmeey/OeffiWatch.git
cd OeffiWatch
mvn3 package
```

Usage
--------
There are two examples provided in the "sample" folder.

###ConsoleClient
In samples/consoleClient is an example on how to use this library as a standalone program
that is controlled via commandline ``sample/consoleClient/``:

    $ java -jar consoleClient.jar
    usage: ÖffiGrabber
    
     -h,   --humanReadable      Specifies the output to be in human readable
                                form
     -j,   --json               Specifies the output to be in compact json
     -J,   --prettyJson         Specifies the output to be in prettyPrinted
                                json
     -s,   --station <arg>      Specifies the Station to query
     -v,   --verbose            Set verbose

If you want to query the departures for "S-Bahnhof Tiergarten":

    $ java -jar consoleClient-0.0.1a.jar -J -s "S Tiergarten (Berlin)"
    {
      "lines": [
        {
          "departureTime": "2016-04-16T00:48",
          "lineName": "S75",
          "destination": "S Wartenberg (Berlin)",
          "type": "SBahn",
          "platform": 3,
          "departureIn": 3
        },
        {
          "departureTime": "2016-04-16T01:41",
          "lineName": "S7",
          "destination": "S Griebnitzsee Bhf",
          "type": "SBahn",
          "platform": 4,
          "departureIn": 56
        }
      ],
      "name": "S Tiergarten (Berlin)",
      "alternativeNamesForStation": [
        "S Tiergarten (Berlin)"
      ],
      "lastUpdated": "2016-04-16T00:44:23.440"
    }

### ApiExample
Since ÖffiWatch was designed to be used as a library by other software projects, there is also an example on
how to use it that way.
``sample/apiExample/src/main/java/ApiExample.java``:

    $ java -jar apiExample.jar
    There are 10 departures scheduled, at S Tiergarten (Berlin)
    The next departures will be:
    S5 which is a S-Bahn going to S Spandau Bhf (Berlin) in 4 minutes
    S5 which is a S-Bahn going to S Hoppegarten in 5 minutes
    S7 which is a S-Bahn going to S Griebnitzsee Bhf in 12 minutes
    S7 which is a S-Bahn going to S Ahrensfelde Bhf (Berlin) in 20 minutes
    Station is autoupdateing: true
    Last update happened at: 2016-04-16T00:58:29.666
    Observer added, waiting for update to happen

The Grabber is the Parent object, where you will select which city/region you want to work on (currently only Berlin/Brandenbug (VBB) is supported)

```java
public static void main(String[] args){

// Getting the appropriate Grabber for Berlin (VBB)
Grabber vbbGrabber = Grabber.getVBBInstance();
}
```

Now it is time to query a Station

```java
// Getting the Station data from the website
Station station = vbbGrabber.getStation(stationName);

System.out.println(String.format("There are %s departures scheduled, at %s",
    station.lines().size(), station.name()));
```
will give

    There are 10 departures scheduled, at S Tiergarten (Berlin)
    The next departures will be:
    S5 which is a S-Bahn going to S Spandau Bhf (Berlin) in 4 minutes
    S5 which is a S-Bahn going to S Hoppegarten in 5 minutes
    S7 which is a S-Bahn going to S Griebnitzsee Bhf in 12 minutes
    S7 which is a S-Bahn going to S Ahrensfelde Bhf (Berlin) in 20 minutes

This data will not update itself.
If you want to get updated departure Times
you need to call the ``update()`` method:

```java
String stationName = "S Tiergarten (Berlin);
Station station = vbbGrabber.getStation(stationName);
station.update();
```


A Station can also be set to update itself in regular intervals.
```java
// Refresh the Data every 30 Seconds
private final static int        refreshPeriod       = 30;
private final static TimeUnit   refreshPeriodUnit   = TimeUnit.SECONDS;
    
Station station = vbbGrabber.getStation(stationName);
station.enableAutoUpdate(refreshPeriod, refreshPeriodUnit);
```

But here is the thing. Since the current VBBGrabber parses a mobile-oriented Website to get it's data,
a Station that is set to autoUpdate only updates itself if there is at least ONE(1) observer registered,
in order to cut down the number of requests.

So just register an Observer
```java
Station station = vbbGrabber.getStation(stationName);
station.enableAutoUpdate(refreshPeriod, refreshPeriodUnit);
station.addObserver((obs,obj)->System.out.println(station.lines().size()));
```
from now on, this Station will update itself every 30 seconds, and will notify the Observer if the timetable has changed.


Notes
-------
There are some internal mechanics you should be aware of.
####Duplicate Station handling
If you query the same station serveral times from the same Grabber, you will get the same object every time,
without any additional qeuery to the website
```java
String stationName = "S Tiergarten (Berlin);
Station station1 = vbbGrabber.getStation(stationName);
Station station2 = vbbGrabber.getStation(stationName);
station1==station2 //true
```
This is done via an internal List in the Grapper, where als the previously queried stations are stored


####Unambigiuous StationName handling
Every physical Station (in Berlin) has exactly one name. But often queries with slightly different Names
can refer to the same physical station
Example: "Bornholmer Str." and "BornholmerStr." refer to the same station.
Since we don't want several Objects referring to the same Station, a Station holds a List of ``alternativeNames``
for itself. 

Example: A Station was already queried using it's correct name "Bornholmerstr.", and then there is a query for "Bornholmer Str.". Since the Grabber does not yet know to which Station "Bornholmer Str." refers, it sends out a query to the website.
The Website now returns the Station "Bornholmerstr." with it's real name.The Grabber now checks whether there already is a station with that "official" name. If such a station is found, the newly queried Station gets merged onto the already known Station. During that merge, if the newly queried station contains more recent data, than the already known station, the data will get merged, and the already known station will notify it's observers. After that, the now updated already known station gets returned to the caller. After the merge, the already known Station now has a List of ``alternativeNames`` containing "Bornholmer Str.". ANy subsequent queries for either "Bornholmerstr." or "Bornholmer Str." will now return the same Stationobject without querying the website.

```java
Station station1 = vbbGrabber.getStation("S Bornholmer Str. (Berlin)"); //Unkown sstation, queries the website
Station station2 = vbbGrabber.getStation("S Bornholmerstr. (Berlin)"); //Since the name is unkown, the website is queried
//An alternative name for station1 is discovered and stored
station1==station2 //true

Station station3 = vbbGrabber.getStation("S Bornholmerstr. (Berlin)"); //Since the name is already known as an alternativeName for station1, there will be no query to the website
```

####Ambigiuous StationName handling
If the queried StationName can not be exactly identifierd by the Website, an AmbigiuousStationNameException is thrown.

This Exceptions contains a List of recommended Station ("Did you mean....")

```java
try{
    Grabber vbbGrabber = Grabber.getVBBInstance();
}catch (AmbigiuousStationNameException e) {
    // the entered station name was not recognized, there are some "Did you mean" recommendations
    for (String station : e.getAlternativeNames()) {
        System.out.println("    " + station);
    }
}
```


####AutoUpdatingStations
A Station implements the Observable Interface (IObservable, which is a Interface-Wrapper for the abstract Observable Class)
If a Station is set to autoUpdate itself, it will NOT do so, unless there is at least ONE(1) observer registered.
If you want to force-trigger an update, jsut call the ``update()`` Method, it will query the Website, and replace
the data contained in the Station

####Immutable Lines
The List of soon departing Lines, contained in a Station, is Immutable.
If you hold any reference to a list returned by ``station.lines()``, your referenced List will not contain any updates
After the Station recognized changed data for it's lines (by updating), it will replace the contained List with this new List. SO make sure you replace your reference to the Lines List after every update.






Credits
-------
This project is heavily influenced by:
https://github.com/MarkusH/bvg-grabber/
The HTML-Parsing was basically copy-pasted from there.

Thanks to Berliner Verkehrs Betriebe (BVG)https://www.bvg.de/en/, to make your data available via a minimalistic HTML-only
page. https://www.bvg.de/en/

Minimalsitic HTML-only site: http://mobil.bvg.de/Fahrinfo/bin/stboard.bin/dox?ld=0.1&


License
-------
The MIT License (MIT)

Copyright (c) 2016 Tim Hinkes <timmeey@timmeey.de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
