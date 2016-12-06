package com.EveSrl.Indoornavigation.utils;

// This interface is used to receive results from TrilaterationTask execution.
public interface AsyncResponse {
    void processFinish(Point result);
}
