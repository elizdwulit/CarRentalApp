/////////////////////////////////////////////////////////
//
//  RentalService.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    /** Set of all vehicle makes in the system */
    private Set<String> vehicleMakes = new HashSet<>();

    /** Set of all vehicle models in the system */
    private Set<String> vehicleModels = new HashSet<>();

    /** Set of all vehicle colors in the system */
    private Set<String> vehicleColors = new HashSet<>();

    /** Set of all vehicle types in the system */
    private Set<String> vehicleTypes = new HashSet<>();

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
        for (Map.Entry<Integer, Vehicle> entry : vehicleMap.entrySet()) {
            Vehicle v = entry.getValue();
            vehicleMakes.add(v.getMake());
            vehicleModels.add(v.getModel());
            vehicleColors.add(v.getColor());
            vehicleTypes.add(v.getType());
        }
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
     * Get all vehicle makes
     * @return Set of make names
     */
    public Set<String> getAllMakes() {
        return vehicleMakes;
    }

    /**
     * Get all vehicle models
     * @return Set of model names
     */
    public Set<String> getAllModels() {
        return vehicleModels;
    }

    /**
     * Get all vehicle colors
     * @return Set of color names
     */
    public Set<String> getAllColors() {
        return vehicleColors;
    }

    /**
     * Get all types of vehicles
     * @return Set of vehicle type names
     */
    public Set<String> getAllVehicleTypes() {
        return vehicleTypes;
    }

    /**
     * Get list of vehicles that meet requested trait requirements
     * @param make Make of vehicle
     * @param model Vehicle model
     * @param year Year vehicle was manufactured
     * @param color Specified color
     * @param minCapacity The minimum number of seats needed in a vehicle
     * @param maxPrice Max price of vehicle
     * @param type Type of vehicle
     * @return list of vehicles that match search criteria
     */
    public List<Vehicle> getFilteredVehicles(Optional<String> make, Optional<String> model, Optional<Integer> year,
                                             Optional<String> color, Optional<Integer> minCapacity, Optional<String> maxPrice,
                                             Optional<String> type) {
        return vehicleMap.values().stream() // for filters, check if param is defined, then compare accordingly
                .filter(v -> !v.isTaken()
                        && (make.isPresent() && !make.get().isEmpty() && !make.get().equalsIgnoreCase("Any")? v.getMake().equalsIgnoreCase(make.get()) : true)
                        && (model.isPresent() && !model.get().isEmpty() && !model.get().equalsIgnoreCase("Any") ? v.getModel().equalsIgnoreCase(model.get()) : true)
                        && (year.isPresent() ? v.getYear() == year.get() : true)
                        && (color.isPresent() && !color.get().isEmpty() && !color.get().equalsIgnoreCase("Any")? v.getColor().equalsIgnoreCase(color.get()) : true)
                        && (minCapacity.isPresent() ? v.getCapacity() >= minCapacity.get() : true)
                        && (maxPrice.isPresent() && !maxPrice.get().isEmpty() ? Double.parseDouble(v.getPricePerDay()) <= Double.parseDouble(maxPrice.get()) : true)
                        && (type.isPresent() && !type.get().isEmpty() && !type.get().equalsIgnoreCase("Any")? v.getType().equalsIgnoreCase(type.get()) : true))
                .collect(Collectors.toList());
    }

    /**
     * Get a Vehicle object that has the specified vehicle id
     * @param vehicleId id of vehicle
     * @return Vehicle object if found in the system, else returns null
     */
    public Vehicle getVehicleFromId(int vehicleId) {
        Vehicle foundVehicle = vehicleMap.get(vehicleId);
        if (foundVehicle == null) {
            System.out.println("RentalService.getVehicleFromId -- No vehicle found for vid: " + vehicleId);
        }
        return foundVehicle;
    }

    /**
     * Get a User object that has the specified user id
     * @param userId id of user
     * @return User object if found in the system, else returns null
     */
    public User getUserFromId(int userId) {
        User foundUser = usersMap.get(userId);
        if (foundUser == null) {
            System.out.println("RentalService.getUserFromId -- No user found for user id: " + userId);
        }
        return foundUser;
    }

    /**
     * Handles renting a vehicle
     *
     * @param userId ID of the user doing the rental
     * @param vehicleId id of the vehicle to rent
     * @param totalCost the total cost of the rental
     * @return id of transaction, -1 if transaction failed
     */
    public int rentVehicle(int userId, int vehicleId, double totalCost) {
        System.out.println("RentalService.rentVehicle -- BEGIN");

        int transactionId = -1;

        // Check if vehicle is still in system and available to be rented
        Vehicle foundVehicle = getVehicleFromId(vehicleId);
        if (foundVehicle == null || foundVehicle.isTaken()) {
            System.out.println("RentalService.rentVehicle -- Available vehicle not found in system for vid " + vehicleId);
            return transactionId;
        }

        // Assign the vehicle in the db to the user
        boolean updateVehicleSuccessful = dbManager.setVehicleTaken(foundVehicle.getId(), true, userId);
        if (!updateVehicleSuccessful) {
            System.out.println("RentalService.rentVehicle -- Failed to update vehicle entry for vid " + vehicleId);
            return transactionId;
        }

        // TODO: Add actual payment handling. Currently not in scope of prototype (3/7/22)
        //transactionManager.pay();
        transactionId = dbManager.addTransactionEntry(userId, vehicleId, totalCost, TransactionManager.TRANSACTION_TYPE_BUY);
        if (transactionId == -1) {
            System.out.println("RentalService.rentVehicle -- Failed to make transaction.");
            return transactionId;
        }

        System.out.println("RentalService.rentVehicle -- Successfully rented vehicle " + vehicleId + " to user " + userId);
        System.out.println("RentalService.rentVehicle -- END");
        return transactionId;
    }

    /**
     * Handles the return of the vehicle. Marks vehicle as available in db
     *
     * @param vehicleId the id of the vehicle to mark available
     * @return true if update successful, else false
     */
    public boolean returnVehicle(int userId, int vehicleId) {
        System.out.println("RentalService.returnVehicle -- BEGIN");
        System.out.println("RentalService.returnVehicle -- Mark vehicle " + vehicleId + " as available");

        // Check if vehicle is still in system (Should not happen)
        Vehicle foundVehicle = getVehicleFromId(vehicleId);
        if (foundVehicle == null || foundVehicle.isTaken()) {
            System.out.println("RentalService.returnVehicle -- Available vehicle not found in system for vid " + vehicleId);
            return false;
        }

        // Reset the vehicle entry to be not taken and not have a corresponding user
        boolean updateVehicleSuccessful = dbManager.setVehicleTaken(foundVehicle.getId(), false, -1);
        if (!updateVehicleSuccessful) {
            System.out.println("RentalService.returnVehicle -- Failed to update vehicle entry for vid " + vehicleId);
            return false;
        }

        // Add transaction entry for history saving purposes (TODO: Separate into different table)
        int transactionId = dbManager.addTransactionEntry(userId, vehicleId, 0.0, TransactionManager.TRANSACTION_TYPE_RETURN);
        if (transactionId == -1) {
            System.out.println("RentalService.returnVehicle -- Failed to make transaction.");
            return false;
        }

        System.out.println("RentalService.returnVehicle -- END");
        return true;
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
        System.out.println("RentalService.getUserId -- BEGIN");
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
        System.out.println("RentalService.getUserId -- END");
        return userId;
    }

    /**
     * Replaces a vehicle in the vehicle map
     * @param v New vehicle
     * @return true if update successful, else false
     */
    public boolean replaceVehicle(Vehicle v) {
        try {
            vehicleMap.replace(v.getId(), v);
        } catch (Exception e) {
            System.out.println("RentalService.replaceVehicle -- Exception replacing vehicle with vid " + v.getId() + " in map");
            System.out.println(e);
            return false;
        }
        return true;
    }
}
