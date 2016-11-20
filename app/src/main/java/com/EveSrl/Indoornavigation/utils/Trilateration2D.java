package com.EveSrl.Indoornavigation.utils;

/**
 * Created by peppe on 29/10/2016.
 */

public class Trilateration2D {
    //Point coordinates for A,B,C,U
    private Point a, b, c, u;
    //Distances of A,B,C from U
    private double r1, r2, r3;

    public void initialize(){
        this.a = new Point();
        this.b = new Point();
        this.c = new Point();
        this.u = new Point();

    }

    public void setR1(double r1) {
        this.r1 = r1;
    }

    public void setR2(double r2) {
        this.r2 = r2;
    }

    public void setR3(double r3) {
        this.r3 = r3;
    }

    public void setA (double x, double y){
        this.a.setX(x);
        this.a.setY(y);
    }

    public void setB (double x, double y){
        this.b.setX(x);
        this.b.setY(y);
    }

    public void setC (double x, double y){
        this.c.setX(x);
        this.c.setY(y);
    }
    
    public void calculateCoordinates(){
        double i1 = a.getX();
        double i2 = b.getX();
        double i3 = c.getX();

        double j1 = a.getY();
        double j2 = b.getY();
        double j3 = c.getY();

        double i1Sq = Math.pow(i1,2);
        double i2Sq = Math.pow(i2,2);
        double i3Sq = Math.pow(i3,2);

        double j1Sq = Math.pow(j1,2);
        double j2Sq = Math.pow(j2,2);
        double j3Sq = Math.pow(j3,2);

        double r1Sq = Math.pow(this.r1,2);
        double r2Sq = Math.pow(this.r2,2);
        double r3Sq = Math.pow(this.r3,2);

        double xnum = ((r1Sq - r2Sq) + (i2Sq - i1Sq) + (j2Sq - j1Sq)) *
                (2 * j3 - 2 * j2) - ((r2Sq - r3Sq) + (i3Sq - i2Sq) + (j3Sq - j2Sq)) *
                (2 * j2 - 2 * j1);
        double xden = (2 * i2 - 2 * i3) * (2 * j2 - 2* j1) -
                (2 * i1 - 2 * i2) * (2 * j3 - 2 * j2);
        double x = xnum/xden;

        double ynum = (r1Sq - r2Sq) + (i2Sq - i1Sq) + (j2Sq - j1Sq) +
                x * (2 * i1 - 2 * i2);
        double yden = 2 * j2 - 2 * j1;
        double y = ynum/yden;

        this.u.setX(x);
        this.u.setY(y);
    }

    public Point getPoint(){
        this.calculateCoordinates();
        return this.u;
    }
}
