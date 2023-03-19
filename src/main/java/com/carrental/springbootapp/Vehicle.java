/////////////////////////////////////////////////////////
//
//  Vehicle.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

/**
 * Class used to represent a vehicle in the car rental system
 */
public class Vehicle {

    /** id of vehicle */
    private int id;

    /** Make of vehicle */
    private String make;

    /** Model of vehicle */
    private String model;

    /** Year of vehicle */
    private int year;

    /** Color of vehicle */
    private String color;

    /** The number of people the vehicle can accommodate */
    private int capacity;

    /** Price the vehicle costs to rent (per day) */
    private double pricePerDay;

    /** Type of vehicle (ex: car, truck, suv. etc) */
    private String type;

    /** Flag to indicate if the vehicle is already being rented out */
    private boolean taken = false;

    /** The id of the user currently renting out the vehicle */
    private int currentRenterId;

    /**
     * Empty constructor
     */
    public Vehicle() {
    }

    /**
     * Constructor
     * @param make
     * @param model
     * @param year
     * @param color
     * @param capacity
     * @param pricePerDay
     * @param type
     */
    public Vehicle(String make, String model, int year, String color, int capacity, double pricePerDay, String type) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.capacity = capacity;
        this.pricePerDay = pricePerDay;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", capacity=" + capacity +
                ", pricePerDay=" + pricePerDay +
                ", type='" + type + '\'' +
                ", taken=" + taken +
                ", currentRenterId=" + currentRenterId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public int getCurrentRenterId() {
        return currentRenterId;
    }

    public void setCurrentRenterId(int currentRenterId) {
        this.currentRenterId = currentRenterId;
    }
}
