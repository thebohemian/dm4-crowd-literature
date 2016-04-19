
CROWD Literature - Omnibus Reading Tour 2016
============================================

A geographical event calendar with author profiles for the Omnibus Reading Tour.  
A DeepaMehta 4 plugin.

Public installation:  
<http://crowdlitbus.eu/>

About the Omnibus Reading Tour:  
<http://crowd-literature.eu/omnibus-2/>

DeepaMehta 4 is a platform for collaboration and knowledge management:  
<https://github.com/jri/deepamehta>


Running locally
---------------

After installation open this page:  
<http://localhost:8080/eu.crowd-literature/>


Version History
---------------

**0.5** -- Mar 21, 2016

Webapp usability

* Web design by Zlata Pasalic
* Marker cluster rendering (by the help of Leaflet MarkerCluster plugin)
* Auto-scale images and videos
* Info pages:
    * Institution page: show events
    * Event page: show address along with institution link
    * Proper date/time formatting on all pages
    * New start page with CROWD logo and introduction text
* Mobile devices: on hi-res displays markers and clusters are slightly enlarged
* Spinning load indicator
* Fixes:
    * External links are working also if `http://` was not entered
    * No map "flickering through" on page change when in portrait mode
* Technical: the webapp talks solely to its own `/crowd` endpoint; does not rely on `/core` anymore
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.4** -- Jan 19, 2016

First production version, updatable

* Work Involvement associations are typed automatically
* Pages:
    * Work page: show institutions (publishers)
    * Event Series page: show events with dates
* Own public "CROWD" workspace
* Technical: uses DTOs for less requests and smaller transfer sizes
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.3** -- Dec 30, 2015

Model consolidation

* Views: Work details page, Institution details page, Event Series details page
* Model:
    * Person has "Gender", "Place of birth", "Nationality" (multi), and "Language" (multi)
    * Work has "Year of publication" and "Place of publication"
    * Work "Genre" is a multi field
    * Event has "Entrance Fee"
    * "Event Series" is new topic type
    * "Curator" is new work involvement role
* Own REST service
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.2** -- Dec 21, 2015

Technical Basis

* Responsive web design: individual layouts for portrait/landscape orientation
* GeoJSON visualization of the bustour
* Clickable Event markers
* Event details page, Person details page, linked to each other
* Deep links
* Functional browser back/forward buttons
* Technical: Makes use of Angular-Leaflet directive
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.1** -- Dec 15, 2015

Project setup

* Data model: Work, Translation, Work Involvement
* Leaflet based map rendering with Mapbox imagery
* Compatible with DeepaMehta 4.8-SNAPSHOT


------------
Jörg Richter  
Mar 21, 2016
