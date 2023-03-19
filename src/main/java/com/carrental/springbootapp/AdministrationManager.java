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

    /**
     * Adds a user to the database
     * @param fname User first name
     * @param lname User last name
     * @param email User email
     * @param phoneNum User phone number
     * @return Created User
     */
    public User addUser(String fname, String lname, String email, String phoneNum) {
        System.out.println("AdministrationManager.addUser -- BEGIN");
        User user = new User(fname, lname, email, phoneNum);
        int addedUserId = dbManager.addUser(user);
        if (addedUserId == -1) {
            System.out.println("AdministrationManager.addUser -- Failed to add user");
        }
        user.setId(addedUserId);
        System.out.println("AdministrationManager.addUser -- Added new user with userId=" + addedUserId);
        System.out.println("AdministrationManager.addUser -- END");
        return user;
    }

    /**
     * Delete a user from the database
     * @param userId id of user to delete
     * @return true if delete successful, else false
     */
    public boolean deleteUser(int userId) {
        System.out.println("AdministrationManager.deleteUser -- BEGIN");
        System.out.println("AdministrationManager.deleteUser -- Deleting user with userId=" + userId);
        boolean deleteSuccessful = dbManager.deleteUser(userId);
        System.out.println("AdministrationManager.deleteUser -- END");
        return deleteSuccessful;
    }

    /**
     * Modify a user in the database
     * @param userId id of user to modify
     * @param fname new first name
     * @param lname new last name
     * @param email new email
     * @param phoneNum new phone number
     * @return New User object
     */
    public User modifyUser(int userId, String fname, String lname, String email, String phoneNum) {
        System.out.println("AdministrationManager.modifyUser -- BEGIN");
        User user = new User(fname, lname, email, phoneNum);
        user.setId(userId);
        dbManager.modifyUser(userId, user);
        System.out.println("AdministrationManager.modifyUser -- END");
        return user;
    }
}
