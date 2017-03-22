package eu.crowdliterature;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.Cookies;
import de.deepamehta.core.service.ModelFactory;
import de.deepamehta.core.service.event.PostCreateTopicListener;
import de.deepamehta.core.service.event.PostUpdateTopicListener;
import de.deepamehta.core.util.ContextTracker;
import de.deepamehta.core.util.JavaUtils;
import de.deepamehta.facets.FacetsService;
import de.deepamehta.geomaps.model.GeoCoordinate;



public class GeoMapsHelper implements PostCreateTopicListener,
                                      PostUpdateTopicListener {

    private static final String GEOCODER_URL = "http://maps.googleapis.com/maps/api/geocode/json?" +
        "address=%s&sensor=false";

    private static final String COOKIE_NO_GEOCODING = "dm4_no_geocoding";

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private FacetsService facetsService;
    
    private ModelFactory mf;

    // used for geocoding suppression
    private ContextTracker contextTracker = new ContextTracker();

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods

    public GeoMapsHelper(ModelFactory modelFactory, FacetsService facetsService) {
    	mf = modelFactory;
    	this.facetsService = facetsService;
    }


    // *************************************
    // *** GeomapsService Implementation ***
    // *************************************

    public GeoCoordinate getGeoCoordinate(Topic geoTopic) {
        try {
            Topic geoCoordTopic = getGeoCoordinateTopic(geoTopic);
            if (geoCoordTopic != null) {
                return geoCoordinate(geoCoordTopic);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Getting the geo coordinate failed (geoTopic=" + geoTopic + ")", e);
        }
    }

    public GeoCoordinate geoCoordinate(Topic geoCoordTopic) {
        ChildTopics childTopics = geoCoordTopic.getChildTopics();
        return new GeoCoordinate(
            childTopics.getDouble("dm4.geomaps.longitude"),
            childTopics.getDouble("dm4.geomaps.latitude")
        );
    }

    // ---

    public <V> V runWithoutGeocoding(Callable<V> callable) throws Exception {
        return contextTracker.run(callable);
    }


    // ********************************
    // *** Listener Implementations ***
    // ********************************

    public void fixGeoCoordinate(Topic topic) {
        Address address = new Address(topic.getChildTopics().getModel());
        if (!address.isEmpty()) {
            logger.info("### GeoMapsHelper fixed address: " + address);
            geocodeAndStoreFacet(address, topic);
        } else {
            logger.info("### GeoMapsHelper - empty address");
        }
    	
    }

    @Override
    public void postCreateTopic(Topic topic) {
        if (topic.getTypeUri().equals("dm4.contacts.address") && DTOHelper.parents(topic).contains("dm4.contacts.person")) {
            if (!abortGeocoding(topic)) {
                //
                facetsService.addFacetTypeToTopic(topic.getId(), "eu.crowdliterature.geo_coordinate_facet");
                //
                Address address = new Address(topic.getChildTopics().getModel());
                if (!address.isEmpty()) {
                    logger.info("### New " + address);
                    geocodeAndStoreFacet(address, topic);
                } else {
                    logger.info("### New empty address");
                }
            }
        }
    }

    @Override
    public void postUpdateTopic(Topic topic, TopicModel updateModel, TopicModel oldTopic) {
    	if (topic.getTypeUri().equals("dm4.contacts.address") && DTOHelper.parents(topic).contains("dm4.contacts.person")) {
            if (!abortGeocoding(topic)) {
                Address address    = new Address(topic.getChildTopics().getModel());
                Address oldAddress = new Address(oldTopic.getChildTopicsModel());
                if (!address.equals(oldAddress)) {
                    logger.info("### Crowd Person Address changed:" + address.changeReport(oldAddress));
                    geocodeAndStoreFacet(address, topic);
                } else {
                    logger.info("### Address not changed");
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    /**
     * Returns the Geo Coordinate topic (including its child topics) of a geo-facetted topic (e.g. an Address),
     * or <code>null</code> if no geo coordinate is stored.
     */
    private Topic getGeoCoordinateTopic(Topic geoTopic) {
        Topic geoCoordTopic = facetsService.getFacet(geoTopic, "eu.crowdliterature.geo_coordinate_facet");
        return geoCoordTopic != null ? geoCoordTopic.loadChildTopics() : null;
    }

    // ---

    /**
     * Geocodes the given address and stores the resulting coordinate as a facet value of the given Address topic.
     * If geocoding (or storing the coordinate) fails a warning is logged; no exception is thrown.
     *
     * @param   topic   the Address topic to be facetted.
     */
    private void geocodeAndStoreFacet(Address address, Topic topic) {
        try {
            GeoCoordinate geoCoord = address.geocode();
            storeGeoCoordinate(topic, geoCoord);
        } catch (Exception e) {
            // ### TODO: show to the user?
            logger.log(Level.WARNING, "Adding geo coordinate to " + address + " failed", e);
        }
    }

    /**
     * Stores a geo coordinate for an address topic in the DB.
     */
    private void storeGeoCoordinate(Topic address, GeoCoordinate geoCoord) {
        try {
            logger.info("Storing geo coordinate (" + geoCoord + ") of address topic " + address.getId());
            facetsService.updateFacet(address, "eu.crowdliterature.geo_coordinate_facet",
                mf.newFacetValueModel("dm4.geomaps.geo_coordinate")
                .put(mf.newChildTopicsModel()
                    .put("dm4.geomaps.longitude", geoCoord.lon)
                    .put("dm4.geomaps.latitude",  geoCoord.lat)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException("Storing geo coordinate of address " + address.getId() + " failed", e);
        }
    }

    // ---

    private boolean abortGeocoding(Topic address) {
        return abortGeocodingByCookie(address) || abortGeocodingByExcecutionContext(address);
    }

    private boolean abortGeocodingByCookie(Topic address) {
        boolean abort = false;
        Cookies cookies = Cookies.get();
        if (cookies.has(COOKIE_NO_GEOCODING)) {
            String value = cookies.get(COOKIE_NO_GEOCODING);
            if (!value.equals("false") && !value.equals("true")) {
                throw new RuntimeException("\"" + value + "\" is an unexpected value for the \"" + COOKIE_NO_GEOCODING +
                    "\" cookie (expected are \"false\" or \"true\")");
            }
            abort = value.equals("true");
            if (abort) {
                logger.info("Geocoding for Address topic " + address.getId() + " SUPPRESSED -- \"" +
                    COOKIE_NO_GEOCODING + "\" cookie detected");
            }
        }
        return abort;
    }

    private boolean abortGeocodingByExcecutionContext(Topic address) {
        boolean abort = contextTracker.runsInTrackedContext();
        if (abort) {
            logger.info("Geocoding for Address topic " + address.getId() + " SUPPRESSED -- runWithoutGeocoding() " +
                "context detected");
        }
        return abort;
    }

    // ------------------------------------------------------------------------------------------------- Private Classes

    private class Address {

        String street, postalCode, city, country;

        // ---

        Address(ChildTopicsModel address) {
        	// The whole point of this implementation is to not have the street and postal code in the
        	// looked up address
            street     = "";
            postalCode = ""; //address.getString("dm4.contacts.postal_code", "");
            city       = address.getString("dm4.contacts.city", "");
            country    = address.getString("dm4.contacts.country", "");
        }

        // ---

        GeoCoordinate geocode() {
            URL url = null;
            try {
                // perform request
                String address = street + ", " + postalCode + " " + city + ", " + country;
                url = new URL(String.format(GEOCODER_URL, JavaUtils.encodeURIComponent(address)));
                logger.info("### Geocoding \"" + address + "\"\n    url=\"" + url + "\"");
                JSONObject response = new JSONObject(JavaUtils.readTextURL(url));
                // check response status
                String status = response.getString("status");
                if (!status.equals("OK")) {
                    throw new RuntimeException(status);
                }
                // parse response
                JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location");
                double lng = location.getDouble("lng");
                double lat = location.getDouble("lat");
                // create result
                GeoCoordinate geoCoord = new GeoCoordinate(lng, lat);
                logger.info("=> " + geoCoord);
                return geoCoord;
            } catch (Exception e) {
                throw new RuntimeException("Geocoding failed (url=\"" + url + "\")", e);
            }
        }

        boolean isEmpty() {
            return street.equals("") && postalCode.equals("") && city.equals("") && country.equals("");
        }

        String changeReport(Address oldAddr) {
            StringBuilder report = new StringBuilder();
            if (!street.equals(oldAddr.street)) {
                report.append("\n    Street: \"" + oldAddr.street + "\" -> \"" + street + "\"");
            }
            if (!postalCode.equals(oldAddr.postalCode)) {
                report.append("\n    Postal Code: \"" + oldAddr.postalCode + "\" -> \"" + postalCode + "\"");
            }
            if (!city.equals(oldAddr.city)) {
                report.append("\n    City: \"" + oldAddr.city + "\" -> \"" + city + "\"");
            }
            if (!country.equals(oldAddr.country)) {
                report.append("\n    Country: \"" + oldAddr.country + "\" -> \"" + country + "\"");
            }
            return report.toString();
        }

        // === Java API ===

        @Override
        public boolean equals(Object o) {
            if (o instanceof Address) {
                Address addr = (Address) o;
                return street.equals(addr.street) && postalCode.equals(addr.postalCode) &&
                    city.equals(addr.city) && country.equals(addr.country);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (street + postalCode + city + country).hashCode();
        }

        @Override
        public String toString() {
            return "address (street=\"" + street + "\", postalCode=\"" + postalCode +
                "\", city=\"" + city + "\", country=\"" + country + "\")";
        }
    }
}
