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
                        && (!make.isPresent() || v.getMake().equalsIgnoreCase(make.get()))
                        && (!model.isPresent() || v.getColor().equalsIgnoreCase(model.get()))
                        && (!year.isPresent() || v.getYear() == year.get())
                        && (!color.isPresent() || v.getColor().equalsIgnoreCase(color.get()))
                        && (!minCapacity.isPresent() || v.getCapacity() <= minCapacity.get())
                        && (!maxPrice.isPresent() || v.getPricePerDay() <= Integer.parseInt(maxPrice.get()))
                        && (!type.isPresent() || v.getType().equalsIgnoreCase(type.get())))
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
     * Handles renting a vehicle
     *
     * @param user User doing the rental
     * @param vehicleId id of the vehicle to rent
     * @param totalCost the total cost of the rental
     * @return integer indicating if rent was successful
     *      0 = success
     *      1 = add user failed
     *      2 = vehicle not found in system, or vehicle is already taken
     *      3 = failed to update vehicle entry in db
     *      4 = transaction failed
     */
    public int rentVehicle(User user, int vehicleId, double totalCost) {
        System.out.println("RentalService.rentVehicle -- BEGIN");

        // First, add the new user to the database
        // (Note 3/7/22 there are no user accounts feature currently implemented)
        int addedUserId = dbManager.addUser(user);
        if (addedUserId == -1) {
            System.out.println("RentalService.rentVehicle -- Failed to add user entry.");
            return 1;
        }

        // Check if vehicle is still in system and available to be rented
        Vehicle foundVehicle = getVehicleFromId(vehicleId);
        if (foundVehicle == null || foundVehicle.isTaken()) {
            System.out.println("RentalService.rentVehicle -- Available vehicle not found in system for vid " + vehicleId);
            return 2;
        }

        // Assign the vehicle in the db to the user
        boolean updateVehicleSuccessful = dbManager.setVehicleTaken(foundVehicle.getId(), true, addedUserId);
        if (!updateVehicleSuccessful) {
            System.out.println("RentalService.rentVehicle -- Failed to update vehicle entry for vid " + vehicleId);
            return 3;
        }

        // TODO: Add actual payment handling. Currently not in scope of prototype (3/7/22)
        //transactionManager.pay();
        boolean transactionSuccess = dbManager.addTransactionEntry(addedUserId, vehicleId, totalCost, TransactionManager.TRANSACTION_TYPE_BUY);
        if (!transactionSuccess) {
            System.out.println("RentalService.rentVehicle -- Failed to make transaction.");
            return 4;
        }

        System.out.println("RentalService.rentVehicle -- Successfully rented vehicle " + vehicleId + " to user " + addedUserId);
        System.out.println("RentalService.rentVehicle -- END");
        return 0;
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
        boolean transactionSuccess = dbManager.addTransactionEntry(userId, vehicleId, 0.0, TransactionManager.TRANSACTION_TYPE_RETURN);
        if (!transactionSuccess) {
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
     * Add a vehicle to the internal map
     * @param v New Vehicle
     */
    public void addVehicle(Vehicle v) {
        vehicleMap.put(v.getId(), v);
    }

    /**
     * Checks if the vehicle with the specified vid is currently being used, and if
     * it is not, deletes a vehicle from the internal map.
     * @param vid vehicle id
     * @return true if successfully deleted, else false
     */
    public boolean deleteVehicle(int vid) {
        try {
            Vehicle foundVehicle = vehicleMap.get(vid);
            if (foundVehicle.isTaken()) {
                System.out.println("RentalService.deleteVehicle -- Vehicle is currently assigned to user " + foundVehicle.getCurrentRenterId() + ". Not deleting");
                return false;
            } else {
                vehicleMap.remove(vid);
            }
        } catch (Exception e) {
            System.out.println("RentalService.deleteVehicle -- Exception removing vehicle with vid " + vid + " from map");
            System.out.println(e);
            return false;
        }
        return true;
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
