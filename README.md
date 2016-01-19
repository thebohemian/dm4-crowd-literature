
DeepaMehta 4 CROWD Literature - Omnibus Tour 2016
=================================================


Running
-------

After installing open this page:

    http://localhost:8080/eu.crowd-literature/


Version History
---------------

**0.4** -- Jan 19, 2016

* First production version, updatable
* Work Involvement associations are typed automatically
* Pages:
    * Work page: show institutions (publishers)
    * Event Series page: show events with dates
* Own public "CROWD" workspace
* Technical: uses DTOs for less requests and smaller transfer sizes
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.3** -- Dec 30, 2015

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

* Responsive web design: individual layouts for portrait/landscape orientation
* GeoJSON visualization of the bustour
* Clickable Event markers
* Event details page, Person details page, linked to each other
* Deep links
* Functional browser back/forward buttons
* Technical: Makes use of Angular-Leaflet directive
* Compatible with DeepaMehta 4.8-SNAPSHOT

**0.1** -- Dec 15, 2015

* Data model: Work, Translation, Work Involvement
* Leaflet based map rendering with Mapbox imagery
* Compatible with DeepaMehta 4.8-SNAPSHOT


------------
Jörg Richter  
Jan 19, 2016
