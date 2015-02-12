package kz.kamadi.passenger.model;

/**
 * Created by Madiyar on 11.02.2015.
 */
public class LocationHolder {
    private static LocationHolder instance;
    public static TaxiLocation start;
    public static TaxiLocation end;

    private LocationHolder(){

    }

    public static LocationHolder getInstance(){
        if(instance==null){
            instance = new LocationHolder();
        }
        return  instance;
    }

}
