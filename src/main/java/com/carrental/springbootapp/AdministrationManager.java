/////////////////////////////////////////////////////////
//
//  AdministrationManager.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

/**
 * Functions used for administrative actions
 */
public class AdministrationManager {

    /** Manager used to handle all database interactions */
    private DatabaseManager dbManager;

    /**
     * Constructor
     */
    public AdministrationManager() {
        dbManager = DatabaseManager.getInstance();
    }

    /**
     * Add a vehicle to the system
     * @param model
     * @param make
     * @param year
     * @param color
     * @param capacity
     * @param pricePerDay
     * @param type
     * @return new Vehicle with vid set
     */
    public Vehicle addVehicle(String model, String make, int year, String color, int capacity, String pricePerDay, String type) {
        System.out.println("AdministrationManager.addVehicle -- BEGIN");
        Vehicle v = new Vehicle(model, make, year, color, capacity, pricePerDay, type);
        int addedVehicleId = dbManager.addVehicle(v);
        if (addedVehicleId == -1) {
            System.out.println("AdministrationManager.addVehicle -- Failed to add vehicle");
        }
        v.setId(addedVehicleId);
        System.out.println("AdministrationManager.addVehicle -- Added new vehicle with vid=" + addedVehicleId);
        System.out.println("AdministrationManager.addVehicle -- END");
        return v;
    }

    /**
     * Delete a vehicle from the system
     * @param vehicleId id of vehicle to delete
     * @return true if delete successful, else false
     */
    public boolean deleteVehicle(int vehicleId) {
        System.out.println("AdministrationManager.deleteVehicle -- BEGIN");
        System.out.println("AdministrationManager.deleteVehicle -- Deleting vehicle with vid=" + vehicleId);
        boolean deleteSuccessful = dbManager.deleteVehicle(vehicleId);
        System.out.println("AdministrationManager.deleteVehicle -- END");
        return deleteSuccessful;
    }

    /**
     * Modify a vehicle in the system
     * @param model
     * @param make
     * @param year
     * @param color
     * @param minCapacity
     * @param pricePerDay
     * @param type
     * @return Vehicle object containing update information
     */
    public Vehicle updateVehicle(int vid, String model, String make, int year, String color, int minCapacity, String pricePerDay, String type) {
        System.out.println("AdministrationManager.updateVehicle -- BEGIN");
        Vehicle v = new Vehicle(model, make, year, color, minCapacity, pricePerDay, type);
        v.setId(vid);
        dbManager.updateVehicle(vid, v);
        System.out.println("AdministrationManager.updateVehicle -- END");
        return v;
    }
}
