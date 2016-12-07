package com.binitshah.hb141;

import static com.google.android.gms.location.places.Place.*;

/**
 * Created by bshah on 12/7/2016.
 */

public class PlaceType {
    public static String convertConstantToString(int GENERIC_CONSTANT) {
        switch (GENERIC_CONSTANT) {
            case TYPE_ACCOUNTING:
                return "Accounting";
            case TYPE_ADMINISTRATIVE_AREA_LEVEL_1:
                return "Administration";
            case TYPE_ADMINISTRATIVE_AREA_LEVEL_2:
                return "Administration";
            case TYPE_ADMINISTRATIVE_AREA_LEVEL_3:
                return "Administration";
            case TYPE_AIRPORT:
                return "Airport";
            case TYPE_AMUSEMENT_PARK:
                return "Amusement Park";
            case TYPE_AQUARIUM:
                return "Aquarium";
            case TYPE_ART_GALLERY:
                return "Art Gallery";
            case TYPE_ATM:
                return "ATM";
            case TYPE_BAKERY:
                return "Bakery";
            case TYPE_BANK:
                return "Bank";
            case TYPE_BAR:
                return "Bar";
            case TYPE_BEAUTY_SALON:
                return "Beauty Salon";
            case TYPE_BICYCLE_STORE:
                return "Bicycle Store";
            case TYPE_BOOK_STORE:
                return "Book Store";
            case TYPE_BOWLING_ALLEY:
                return "Bowling Alley";
            case TYPE_BUS_STATION:
                return "Bus Station";
            case TYPE_CAFE:
                return "Cafe";
            case TYPE_CAMPGROUND:
                return "Campground";
            case TYPE_CAR_DEALER:
                return "Car Dealer";
            case TYPE_CAR_RENTAL:
                return "Car Rental";
            case TYPE_CAR_REPAIR:
                return "Car Repair";
            case TYPE_CAR_WASH:
                return "Car Wash";
            case TYPE_CASINO:
                return "Casino";
            case TYPE_CEMETERY:
                return "Cemetery";
            case TYPE_CHURCH:
                return "Church";
            case TYPE_CITY_HALL:
                return "City Hall";
            case TYPE_CLOTHING_STORE:
                return "Clothing Store";
            case TYPE_COLLOQUIAL_AREA:
                return "Colloquial Area";
            case TYPE_CONVENIENCE_STORE:
                return "Convenience Store";
            case TYPE_COUNTRY:
                return "Country";
            case TYPE_COURTHOUSE:
                return "Courthouse";
            case TYPE_DENTIST:
                return "Dentist";
            case TYPE_DEPARTMENT_STORE:
                return "Department Store";
            case TYPE_DOCTOR:
                return "Doctor";
            case TYPE_ELECTRICIAN:
                return "Electrician";
            case TYPE_ELECTRONICS_STORE:
                return "Electronics Store";
            case TYPE_EMBASSY:
                return "Embassy";
            case TYPE_ESTABLISHMENT:
                return "";
            case TYPE_FINANCE:
                return "Finance";
            case TYPE_FIRE_STATION:
                return "Fire Station";
            case TYPE_FLOOR:
                return "Floor";
            case TYPE_FLORIST:
                return "Florist";
            case TYPE_FOOD:
                return "Food";
            case TYPE_FUNERAL_HOME:
                return "Funeral Home";
            case TYPE_FURNITURE_STORE:
                return "Furniture Store";
            case TYPE_GAS_STATION:
                return "Gas Station";
            case TYPE_GENERAL_CONTRACTOR:
                return "General Contractors";
            case TYPE_GEOCODE:
                return "Geocode";
            case TYPE_GROCERY_OR_SUPERMARKET:
                return "Grocery | Supermarket";
            case TYPE_GYM:
                return "Gym";
            case TYPE_HAIR_CARE:
                return "Hair Care";
            case TYPE_HARDWARE_STORE:
                return "Hardware Store";
            case TYPE_HEALTH:
                return "Health";
            case TYPE_HINDU_TEMPLE:
                return "Hindu Temple";
            case TYPE_HOME_GOODS_STORE:
                return "Home Goods Store";
            case TYPE_HOSPITAL:
                return "Hospital";
            case TYPE_INSURANCE_AGENCY:
                return "Insurance Agency";
            case TYPE_INTERSECTION:
                return "Intersection";
            case TYPE_JEWELRY_STORE:
                return "Jewelry Store";
            case TYPE_LAUNDRY:
                return "Laundry";
            case TYPE_LAWYER:
                return "Lawyer";
            case TYPE_LIBRARY:
                return "Library";
            case TYPE_LIQUOR_STORE:
                return "Liquor Store";
            case TYPE_LOCALITY:
                return "Locality";
            case TYPE_LOCAL_GOVERNMENT_OFFICE:
                return "Local Government Office";
            case TYPE_LOCKSMITH:
                return "Locksmith";
            case TYPE_LODGING:
                return "Lodging";
            case TYPE_MEAL_DELIVERY:
                return "Meal Delivery";
            case TYPE_MEAL_TAKEAWAY:
                return "Meal Takeaway";
            case TYPE_MOSQUE:
                return "Mosque";
            case TYPE_MOVIE_RENTAL:
                return "Movie Rental";
            case TYPE_MOVIE_THEATER:
                return "Movie Theater";
            case TYPE_MOVING_COMPANY:
                return "Moving Company";
            case TYPE_MUSEUM:
                return "Museum";
            case TYPE_NATURAL_FEATURE:
                return "Natural Feature";
            case TYPE_NEIGHBORHOOD:
                return "Neighborhood";
            case TYPE_NIGHT_CLUB:
                return "Night Club";
            case TYPE_OTHER:
                return "";
            case TYPE_PAINTER:
                return "Painter";
            case TYPE_PARK:
                return "Park";
            case TYPE_PARKING:
                return "Parking";
            case TYPE_PET_STORE:
                return "Pet Store";
            case TYPE_PHARMACY:
                return "Pharmacy";
            case TYPE_PHYSIOTHERAPIST:
                return "Physiotherapist";
            case TYPE_PLACE_OF_WORSHIP:
                return "Place of Worship";
            case TYPE_PLUMBER:
                return "Plumber";
            case TYPE_POINT_OF_INTEREST:
                return "";
            case TYPE_POLICE:
                return "Police";
            case TYPE_POLITICAL:
                return "Political";
            case TYPE_POSTAL_CODE:
                return "Postal Code";
            case TYPE_POSTAL_CODE_PREFIX:
                return "Postal Code";
            case TYPE_POSTAL_TOWN:
                return "Postal Town";
            case TYPE_POST_BOX:
                return "Post Box";
            case TYPE_POST_OFFICE:
                return "Post Office";
            case TYPE_PREMISE:
                return "Premise";
            case TYPE_REAL_ESTATE_AGENCY:
                return "Real Estate Agency";
            case TYPE_RESTAURANT:
                return "Restaurant";
            case TYPE_ROOFING_CONTRACTOR:
                return "Roofing Contractor";
            case TYPE_ROOM:
                return "Room";
            case TYPE_ROUTE:
                return "Route";
            case TYPE_RV_PARK:
                return "RV Park";
            case TYPE_SCHOOL:
                return "School";
            case TYPE_SHOE_STORE:
                return "Shoe Store";
            case TYPE_SHOPPING_MALL:
                return "Shopping Mall";
            case TYPE_SPA:
                return "Spa";
            case TYPE_STADIUM:
                return "Stadium";
            case TYPE_STORAGE:
                return "Storage";
            case TYPE_STORE:
                return "Store";
            case TYPE_STREET_ADDRESS:
                return "Street Address";
            case TYPE_SUBLOCALITY:
                return "Sublocality";
            case TYPE_SUBLOCALITY_LEVEL_1:
                return "Sublocality";
            case TYPE_SUBLOCALITY_LEVEL_2:
                return "Sublocality";
            case TYPE_SUBLOCALITY_LEVEL_3:
                return "Sublocality";
            case TYPE_SUBLOCALITY_LEVEL_4:
                return "Sublocality";
            case TYPE_SUBLOCALITY_LEVEL_5:
                return "Sublocality";
            case TYPE_SUBPREMISE:
                return "Subpremise";
            case TYPE_SUBWAY_STATION:
                return "Subway Station";
            case TYPE_SYNAGOGUE:
                return "Synagogue";
            case TYPE_SYNTHETIC_GEOCODE:
                return "Synthetic Geocode";
            case TYPE_TAXI_STAND:
                return "Taxi Stand";
            case TYPE_TRAIN_STATION:
                return "Train Station";
            case TYPE_TRANSIT_STATION:
                return "Transit Station";
            case TYPE_TRAVEL_AGENCY:
                return "Travel Agency";
            case TYPE_UNIVERSITY:
                return "University";
            case TYPE_VETERINARY_CARE:
                return "Veterinary Care";
            case TYPE_ZOO:
                return "Zoo";
            default:
                return "";
        }
    }
}
