package com.EveSrl.Indoornavigation.utils;

import android.os.Bundle;

import com.beyondar.android.world.GeoObject;

/**
 * Created by Cloud on 21/10/2016.
 */

// TODO
public class GeoObjectExt extends GeoObject {
    protected Bundle info;

    // Constructors.
    public GeoObjectExt(long id){
        super(id);
    }
    public GeoObjectExt(){
        super();
    }

    /** Method to record information associated to the GeoObject.
     *
     * @param info = Information associated to the GeoObject.
     */
    public void setInfo(Bundle info){
        this.info = info;
    }
    /** Method to retrieve information associated to the GeoObject.
     *
     *  @return = Information associated to the GeoObject.
     */
    public Bundle getInfo(){
        return info;
    }
}
