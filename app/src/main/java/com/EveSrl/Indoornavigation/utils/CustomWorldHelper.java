/*
 * Copyright (C) 2014 BeyondAR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.EveSrl.Indoornavigation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.EveSrl.Indoornavigation.R;
import com.beyondar.android.world.World;

import java.util.ArrayList;


/**
 * Class managing the AR World.
 */

@SuppressLint("SdCardPath")
public class CustomWorldHelper {
	public static final int LIST_TYPE_EXAMPLE_1 = 1;

	public static World sharedWorld;

    // Indice che tiene conto del numero di oggetti nel mondo.
    protected static long index = 0l;

    // Questi valori iniziali sono quelli usati per l'esempio di "BeyondAR Example"
    protected static double latitude = 0.0d; //= 41.90533734214473d;
    protected static double longitude = 0.0d; //= 2.565848038959814d;

    // Example World.
    public static World sampleWorld(Context context){
        Bundle bndl = new Bundle();

        sharedWorld = newWorld(context);

        //latitude = userLat;
        //longitude = userLon;

        addObject(R.drawable.beacon_gray,
                43.5873893d,
                13.5168969d,
                "Little Green Ball",
                null);

        addObject(LIST_TYPE_EXAMPLE_1,              // TYPE
                // URI
                "http://wikizilla.org/wiki/images/thumb/1/14/Download-Godzilla-Strike-Zone-v1-0-0-APK-gratis.png/330px-Download-Godzilla-Strike-Zone-v1-0-0-APK-gratis.png",
                43.5873893d + 0.00014767853754d,    // Latitude
                13.5168969d + 0.000023794277594d,   // Longitude
                "Godzilla!!",                       // Name
                null);                              // Information

        addObject(R.drawable.googlemaps_icono,
                43.5873893d - 0.0017225426972d,
                13.5168969d - 0.000025349127805d,
                "Little Blue Ball",
                null);

        // Example of passing a Bundle of information.
        ArrayList<String> images = new ArrayList<>();
        images.add("/storage/extSdCard/DCIM/Camera/20150810_194407.jpg");
        images.add("/storage/extSdCard/DCIM/Camera/20160408_172124.jpg");

        bndl.putStringArrayList("Imgs", images);
        //bndl.putString("Img", "/storage/extSdCard/DCIM/Camera/20150810_194407.jpg");
        addObject(R.drawable.map_marker_outside_chartreuse,    // Drawable image
                latitude + 0.00014767853754d,       // Latitude
                longitude + 0.000023794277594d,     // Longitude
                "Default Image",                    // Name
                bndl);                              // Information

        return sharedWorld;
    }

    /*----------------------------------------------------------------------------
      Tutti i metodi sopra questa linea verranno eliminati perché si utilizzeranno
        i metodi seguenti.
     */


    /** Create a new AR World
     *
     * @param context = Application context.
     * @return = Return a whole new World, without any objects inside.
     */
    public static World newWorld(Context context){
        // Comando che dovrebbe liberare le risorse prima occupate dal vecchio mondo.
        if (sharedWorld != null) {
            sharedWorld.clearWorld();
        }

        sharedWorld = new World(context);

        // The user can set the default bitmap. This is useful if you are
        // loading images form Internet and the connection get lost
        sharedWorld.setDefaultImage(R.drawable.map_marker_outside_pink_icon);

        // User position (you can change it using the GPS listeners form Android
        // API)
        //GPSReader gpsReader = new GPSReader(context);
        latitude = 0.0d;
        longitude = 0.0d;


        sharedWorld.setGeoPosition(latitude, longitude);

        // Il numero di elementi viene reiniziallizzato a zero.
        index = 0l;

        return sharedWorld;
    }

    /** To load Object with an image from the app resources.
     *
     * @param image     = ID of the image.
     * @param latitude  = Latitude value of the object.
     * @param longitude = Longitude value of the object.
     * @param name      = Name of the object.
     * @param info      = Information related to the object.
     * @return          = Return the World updated with the new object.
     */
    public static World addObject(int image, double latitude, double longitude, String name, Bundle info){
        GeoObjectExt goe;
        index += 1l;
        goe = new GeoObjectExt(index);
        goe.setGeoPosition(latitude, longitude);
        goe.setImageResource(image);
        goe.setName(name);
        goe.setInfo(info);

        addObject(goe);

        return sharedWorld;
    }
    /** To load Object with an image from the app resources.
     *
     * @param worldListType = ?
     * @param image     = ID of the image.
     * @param latitude  = Latitude value of the object.
     * @param longitude = Longitude value of the object.
     * @param name      = Name of the object.
     * @param info      = Information related to the object.
     * @return          = Return the World updated with the new object.
     */
	public static World addObject(int worldListType, int image, double latitude, double longitude, String name, Bundle info){
        GeoObjectExt goe;
        index += 1l;
        goe = new GeoObjectExt(index);
        goe.setGeoPosition(latitude, longitude);
        goe.setImageResource(image);
        goe.setName(name);
        goe.setInfo(info);

        addObject(worldListType, goe);

        return sharedWorld;
    }
    /** To load Object with an image loaded asynchronously form internet or saved in the sdcard.
     *
     * @param image_URI = URI of the image.
     * @param latitude  = Latitude value of the object.
     * @param longitude = Longitude value of the object.
     * @param name      = Name of the object.
     * @param info      = Information related to the object.
     * @return          = Return the World updated with the new object.
     */
    public static World addObject(String image_URI, double latitude, double longitude, String name, Bundle info){
        GeoObjectExt goe;
        index += 1l;
        goe = new GeoObjectExt(index);
        goe.setGeoPosition(latitude, longitude);
        goe.setImageUri(image_URI);
        goe.setName(name);
        goe.setInfo(info);

        addObject(goe);

        return sharedWorld;
    }
    /** To load Object with an image loaded asynchronously form internet or saved in the sdcard.
     *
     * @param worldListType = ?
     * @param image_URI = URI of the image.
     * @param latitude  = Latitude value of the object.
     * @param longitude = Longitude value of the object.
     * @param name      = Name of the object.
     * @param info      = Information related to the object.
     * @return          = Return the World updated with the new object.
     */
    public static World addObject(int worldListType, String image_URI, double latitude, double longitude, String name, Bundle info){
        GeoObjectExt goe;
        index += 1l;
        goe = new GeoObjectExt(index);
        goe.setGeoPosition(latitude, longitude);
        goe.setImageUri(image_URI);
        goe.setName(name);
        goe.setInfo(info);

        addObject(worldListType, goe);

        return sharedWorld;
    }

    /** Add the GeoObject parameter to the ARWorld.
     *
     * @param goe = GeoObject Extended to load in the ARWorld.
     * @return   = Return the World updated with the new object.
     */
    public static World addObject(GeoObjectExt goe){
        // Check if the AR World were correctly initialized.
        if (sharedWorld != null) {
            sharedWorld.addBeyondarObject(goe);
        }

        return sharedWorld;
    }
    /** Add the GeoObject parameter to the ARWorld.
     *
     * @param worldListType = ?
     * @param goe = GeoObject Extended to load in the ARWorld.
     * @return   = Return the World updated with the new object.
     */
    public static World addObject(int worldListType, GeoObjectExt goe){
        // Check if the AR World were correctly initialized.
        if (sharedWorld != null) {
            sharedWorld.addBeyondarObject(goe, worldListType);
        }

        return sharedWorld;
    }


    public static long getIndex(){ return index; }
    public static void incIndex(){ index += 1l; }
    public static World getARWorld(){ return sharedWorld; }

    // Set user's location.
    public static void setLocation(double lat, double lon){
        if (sharedWorld != null) {
            latitude = lat;
            longitude = lon;
            sharedWorld.setGeoPosition(latitude, longitude);
        }
    }
}
