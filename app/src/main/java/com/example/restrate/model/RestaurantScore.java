package com.example.restrate.model;

public class RestaurantScore {
    private int costMeter;
    private double rate;

    public RestaurantScore(int costMeter, double rate) {
        this.costMeter = costMeter;
        this.rate = rate;
    }

    public int getCostMeter() {
        return costMeter;
    }

    public double getRate() {
        return rate;
    }
}
