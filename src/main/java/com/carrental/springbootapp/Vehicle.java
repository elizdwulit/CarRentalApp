package com.carrental.springbootapp;

/**
 * Class used to represent a vehicle in the car rental system
 */
public class Vehicle {

    /** id of vehicle */
    private int id;

    /** Model name of vehicle */
    private String modelName;

    /** Color of vehicle */
    private String color;

    /** The max number of people the vehicle can accomodate */
    private int maxCapacity;

    /** Price the vehicle costs to rent (per day) */
    private double pricePerDay;

    /** Type of vehicle (ex: car, truck, suv. etc) */
    private int type;

    /** Flag to indicate if the vehicle is already being rented out */
    private boolean taken = false;

    /** The id of the user currently renting out the vehicle */
    private int currentRenterId;

    /**
     * Empty constructor
     */
    public Vehicle() {
        // empty constructor
    }

    /**
     * Constructor
     * (Note: available param is purposely not included in constructor. Has default val = true)
     *
     * @param modelName
     * @param color
     * @param maxCapacity
     * @param pricePerDay
     * @param type
     */
    public Vehicle(String modelName, String color, int maxCapacity, double pricePerDay, int type) {
        this.modelName = modelName;
        this.color = color;
        this.maxCapacity = maxCapacity;
        this.pricePerDay = pricePerDay;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setAvailable(boolean taken) {
        this.taken = taken;
    }

    public int getCurrentRenterId() {
        return currentRenterId;
    }

    public void setCurrentRenterId(int userId) {
        this.currentRenterId = userId;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + "\'" +
                ", modelName='" + modelName + '\'' +
                ", color=" + color +
                ", maxCapacity=" + maxCapacity +
                ", pricePerDay=" + pricePerDay +
                ", type=" + type +
                ", taken=" + taken +
                ", currentRenterId=" + currentRenterId +
                '}';
    }
}
