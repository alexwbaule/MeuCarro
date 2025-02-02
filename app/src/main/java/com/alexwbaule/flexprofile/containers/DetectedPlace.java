package com.alexwbaule.flexprofile.containers;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * Created by alex on 10/10/16.
 */

public class DetectedPlace {
    private List<Place.Type> places_type;
    private LatLng coord;
    private String placeId;
    private String placeName;
    private String placeAddress;
    private double placeRating;
    private int placePriceLevel;
    private boolean elected;
    private boolean valid;

    public DetectedPlace() {
        valid = false;
    }

    public DetectedPlace(List<Place.Type> pt, double latd, double longt, String pid, String pname, String paddr, double rr, int pl) {
        places_type = pt;
        coord = new LatLng(latd, longt);
        placeId = pid;
        placeName = pname;
        placeAddress = paddr;
        placeRating = rr;
        placePriceLevel = pl;
        elected = false;
        valid = true;
    }

    public DetectedPlace(List<Place.Type> pt, double latd, double longt, String pid, String pname, String paddr, double rr, int pl, boolean elec) {
        places_type = pt;
        coord = new LatLng(latd, longt);
        placeId = pid;
        placeName = pname;
        placeAddress = paddr;
        placeRating = rr;
        placePriceLevel = pl;
        elected = elec;
        valid = true;
    }

    public DetectedPlace(Place place) {
        add(place);
    }

    public DetectedPlace(Place place, boolean ad) {
        add(place, ad);
    }


    public boolean isValid() {
        return valid;
    }

    public void add(Place place) {
        placeId = place.getId();
        coord = place.getLatLng();
        places_type = place.getTypes();
        placeName = place.getName().replace('"', ' ');
        placeAddress = place.getAddress().replace('"', ' ');
        placeRating = place.getRating() == null ? 0 : place.getRating();
        placePriceLevel = place.getPriceLevel() == null ? 0 : place.getPriceLevel();
        valid = true;
    }

    public void add(Place place, boolean elec) {
        placeId = place.getId();
        coord = place.getLatLng();
        places_type = place.getTypes();
        placeName = place.getName().replace('"', ' ');
        placeAddress = place.getAddress().replace('"', ' ');
        placeRating = place.getRating() == null ? 0 : place.getRating();
        placePriceLevel = place.getPriceLevel() == null ? 0 : place.getPriceLevel();
        elected = elec;
        valid = true;
    }

    public List<Place.Type> getPlaces_type() {
        return places_type;
    }

    public void setPlaces_type(List<Place.Type> places_type) {
        this.places_type = places_type;
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public double getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }

    public int getPlacePriceLevel() {
        return placePriceLevel;
    }

    public void setPlacePriceLevel(int placePriceLevel) {
        this.placePriceLevel = placePriceLevel;
    }

    public boolean getElectedPlace() {
        return elected;
    }

    public void setElectedPlace(boolean elected) {
        this.elected = elected;
    }


    @Override
    public String toString() {
        return "DetectedPlace{" +
                "places_type=" + places_type +
                ", coord=" + coord +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeAddress='" + placeAddress + '\'' +
                ", placeRating=" + placeRating +
                ", placePriceLevel=" + placePriceLevel +
                '}';
    }
}
