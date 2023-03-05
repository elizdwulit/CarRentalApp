/////////////////////////////////////////////////////////
//
//  RentalService.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Functions to be called from front end. Used to indirectly communicate with db.
 */
public class RentalService {

    /** Manager used to handle all database interactions */
    private DatabaseManager dbManager;

    /** Manager used for all transaction-related operations */
    private TransactionManager transactionManager;

    /** Map used to store all vehicle information */
    private Map<Integer, Vehicle> vehicleMap = new HashMap<>();

    /** Map used to store information of users registered in the system */
    private Map<Integer, User> usersMap = new HashMap<>();

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
     * Get list of vehicles that meet requested trait requirements
     * @param color
     * @param maxCapacity
     * @param maxPrice
     * @param vehicleType
     * @return list of vehicles that match search traits
     */
    public List<Vehicle> getFilteredVehicles(Optional<String> color, Optional<Integer> maxCapacity, Optional<String> maxPrice, Optional<Integer> vehicleType) {
        return vehicleMap.values().stream()
                .filter(v -> !v.isTaken() && (!color.isPresent() || v.getColor().equalsIgnoreCase(color.get())) // check if color was provided then use it for filter
                        && (!maxCapacity.isPresent() || v.getMaxCapacity() >= maxCapacity.get()) // check for maxCapacity presence and add to filter
                        && (!maxPrice.isPresent() || v.getPricePerDay() <= Integer.parseInt(maxPrice.get())) // check for maxPrice and add to filter
                        && (!vehicleType.isPresent() || (vehicleType.get() == -1 || v.getType() == vehicleType.get()))) // -1 vehicleType param means any vehicle type is ok
                .collect(Collectors.toList());
    }

    /**
     * Get a Vehicle object that has the specified vehicle id
     * @param vehicleId
     * @return numerical ID of vehicle
     */
    public Vehicle getVehicleFromId(int vehicleId) {
        // TODO: Get vehicle object from map (class variable "vehicleMap") and return it

        return new Vehicle();
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
        System.out.println("RentalFunctions.rentVehicle -- BEGIN");

        //TODO: call methods needed to make a rental, then return true if rental process succeeded
        // NOTE: We already have dbManager.addTransactionEntry(...) to add to the transaction db table
        // Workflow should be similar to
        // 1. add user to users table in db
        // 2. get vehicle object from vehicleId
        // 3. make transaction with dbManager addTransactionEntry(...) with transactionType=TransactionMAnager.TRANSACTION_TYPE_BUY
        // 4. Set the vehicle as taken after transaction is found to be successful, dbManager.setVehicleTaken(...)

        System.out.println("RentalFunctions.rentVehicle -- END");
        return false;
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

        //TODO: set vehicle db entry is_taken=0 and curr_user_id=-1
        // add transaction entry with dbManager addTransactionEntry
        // (for input params, utilize amount=0 and transactionType=TransactionManager.TRANSACTION_TYPE_RETURN)
        // Note: in the future we may want to also delete the user, but don't have to worry about this for now

        System.out.println("RentalFunctions.returnVehicle -- END");
        return false;
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
