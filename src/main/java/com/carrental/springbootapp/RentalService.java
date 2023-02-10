package com.carrental.springbootapp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Functions to be called from front end
 */
public class RentalService {

    /** Manager used to handle all database interactions */
    DatabaseManager dbManager;

    /** Map used to store all vehicle information */
    Map<Integer, Vehicle> vehicleMap = new HashMap<>();

    /** Set of ids of all vehicles that are currently available to rent */
    Set<Integer> availableVehicleIds = new HashSet<>();

    /** Map used to store information of users registered in the system */
    Map<Integer, User> usersMap = new HashMap<>();

    /**
     * Constructor
     */
    public RentalService() {
        dbManager = DatabaseManager.getInstance();
    }

    /**
     * Load all the vehicles from the database
     */
    public void loadVehicles() {
        vehicleMap = dbManager.getAllVehicles();
        availableVehicleIds = vehicleMap.entrySet().stream()
                .filter(mapEntry -> !mapEntry.getValue().isTaken())
                .map(mapEntry -> mapEntry.getKey())
                .collect(Collectors.toSet());
    }

    /**
     * Load all users from the database
     */
    public void loadUsers() {
        usersMap = dbManager.getAllUsers();
    }

    /**
     * Get all vehicles in the system
     * @return list of Vehicle objects
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleMap.entrySet().stream()
                .map(mapEntry -> mapEntry.getValue())
                .collect(Collectors.toList());
    }

    /**
     * Get set containing the ids of all vehicles that are currently available to rent
     *
     *  @return Set of available vehicle ids (can be empty)
     */
    public Set<Integer> getAllAvailVehicleIds() {
        return availableVehicleIds;
    }

    /**
     * Handles renting a vehicle
     *
     * @param user User doing the rental
     * @param vehicleId id of the vehicle to rent
     * @param totalCost the total cost of the rental
     * @return true if successfully rented vehicle, else false
     */
    public boolean rentVehicle(User user, int vehicleId, double totalCost) {
        return rentVehicle(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNum(), vehicleId, totalCost);
    }

    /**
     * Handles renting a vehicle
     *
     * @param firstName first name provided by user
     * @param lastName last name provided by user
     * @param email email provided by user
     * @param phoneNum phone number provided by user
     * @param vehicleId id of the vehicle to rent
     * @param totalCost the total cost of the rental
     * @return true if successfully rented vehicle, else false
     */
    public boolean rentVehicle(String firstName, String lastName, String email, String phoneNum, int vehicleId, double totalCost) {
        System.out.println("RentalFunctions.rentVehicle -- BEGIN");

        // Get the vehicle corresponding to the requested vehicle id and check if it is available to rent
        // This is a sanity check. If it is in the list then it should be available.
        Vehicle foundVehicle = vehicleMap.get(vehicleId);
        boolean isVehicleAvailable = foundVehicle != null ? !foundVehicle.isTaken() : false;
        if (!isVehicleAvailable) {
            System.out.println("RentalFunctions.rentVehicle -- Vehicle " + vehicleId + " is not available");
            System.out.println("RentalFunctions.rentVehicle -- END1");
            return false;
        }

        // get existing user id, or add new user and get their assigned id
        int userId = getUserId(firstName, lastName, email, phoneNum);

        // after user is found or added, do the transaction
        boolean transactionSuccess = dbManager.addTransactionEntry(userId, vehicleId, totalCost);
        if (!transactionSuccess) {
            System.out.println("RentalFunctions.rentVehicle -- Failed to make transaction");
            System.out.println("RentalFunctions.rentVehicle -- END2");
            return false;
        }

        // if transaction was successful, mark the vehicle as taken
        boolean vehicleUpdated = dbManager.setVehicleTaken(vehicleId, true, userId);
        if (!vehicleUpdated) {
            System.out.println("RentalFunctions.rentVehicle -- Failed to make transaction");
            System.out.println("RentalFunctions.rentVehicle -- END3");
            return false;
        }

        // after rental is completed, remove the vehicle from the available vehicles set
        availableVehicleIds.remove(vehicleId);
        // also update the vehicle map's vehicle object
        foundVehicle.setAvailable(false);
        vehicleMap.put(vehicleId, foundVehicle);

        System.out.println("RentalFunctions.rentVehicle -- User " + userId + " successfully rented vehicle " + vehicleId + " for $" + totalCost);
        System.out.println("RentalFunctions.rentVehicle -- END");
        return true;
    }

    /**
     * Handles the return of the vehicle. Marks vehicle as available in db
     *
     * @param vehicleId the id of the vehicle to mark available
     * @return true if update successful, else false
     */
    public boolean returnVehicle(int vehicleId) {
        System.out.println("RentalFunctions.returnVehicle -- BEGIN");
        System.out.println("RentalFunctions.returnVehicle -- Mark vehicle " + vehicleId + " as available");

        boolean returnSuccessful = dbManager.setVehicleTaken(vehicleId, false, -1);

        System.out.println("RentalFunctions.returnVehicle -- END");
        return returnSuccessful;
    }

    /**
     * Get the total amount in USD that a specified vehicle will cost for a specified amount of days
     *
     * @param numDays number of days the vehicle will be rented out
     * @param vehicleId the id of the vehicle being rented
     * @return total rental cost in USD rounded up to 2 decimal places.
     *          If vehicle was not found, returns -1
     */
    public double getTotalCost(int numDays, int vehicleId) {
        // look up the vehicle in the map
        Vehicle foundVehicle = vehicleMap.get(vehicleId);
        if (foundVehicle == null) { // should not happen, but check is here just in case vehicle not found
            return -1;
        } else {
            double vehicleDailyRate = foundVehicle.getPricePerDay();
            double totalCost = numDays * vehicleDailyRate;
            // return the total cost rounded up to 2 decimal places
            return (double) Math.round(totalCost * 100) / 100;
        }
    }

    /**
     * Get and ID associated with the provided user information.
     * If no existing user found, create a new user and add to db.
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNum
     * @return id of user
     */
    private int getUserId(String firstName, String lastName, String email, String phoneNum) {
        System.out.println("RentalFunctions.getUserId -- BEGIN");
        int userId = -1;
        // look through the map
        Optional<Map.Entry<Integer, User>> matchingUser = usersMap.entrySet().stream()
                .filter(userEntry -> firstName.equalsIgnoreCase(userEntry.getValue().getFirstName())
                        && lastName.equalsIgnoreCase(userEntry.getValue().getLastName())
                        && email.equalsIgnoreCase(userEntry.getValue().getEmail())
                        && phoneNum.equalsIgnoreCase(userEntry.getValue().getPhoneNum()))
                .findAny();
        // if user found in already loaded map, use that entry id
        if (matchingUser.isPresent()) {
            userId = matchingUser.get().getKey();
        } else { // if no existing user found, add new user and get the added id
            User user = new User(firstName, lastName, email, phoneNum);
            userId = dbManager.addUser(user);
            if (userId > -1) { // can be -1 if add failed
                user.setId(userId);
                usersMap.put(userId, user);
            }
        }
        System.out.println("RentalFunctions.getUserId -- END");
        return userId;
    }
}
