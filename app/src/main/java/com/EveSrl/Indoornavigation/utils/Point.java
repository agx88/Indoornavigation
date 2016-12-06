package com.EveSrl.Indoornavigation.utils;

/**
 * Created by peppe on 29/10/2016.
 */

public class Point {

    private double x;
    private double y;

    private static double pixelMeterRatioX;
    private static double pixelMeterRatioY;

    public Point(){
        this.x = 0.0;
        this.y = 0.0;
    }

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double pMRX, double pMRY){
        this.x = x;
        this.y = y;
        pixelMeterRatioX = pMRX;
        pixelMeterRatioY = pMRY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static void setPixelMeterRatioX(double pixelMeterRatioX) {
        Point.pixelMeterRatioX = pixelMeterRatioX;
    }

    public static double getPixelMeterRatioX() {
        return pixelMeterRatioX;
    }

    public static void setPixelMeterRatioY(double pixelMeterRatioY) {
        Point.pixelMeterRatioY = pixelMeterRatioY;
    }

    public static double getPixelMeterRatioY() {
        return pixelMeterRatioY;
    }


    public static Point fromMeterToPixel(Point m){
        Point p = new Point();

        p.setX(m.getX() * pixelMeterRatioX);
        p.setY(m.getY() * pixelMeterRatioY);

        return p;
    }

    public static Point fromPixelToMeter(Point p){
        Point m = new Point();

        m.setX(p.getX() / pixelMeterRatioX);
        m.setY(p.getY() / pixelMeterRatioY);

        return m;
    }
}
