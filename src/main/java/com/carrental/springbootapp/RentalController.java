/////////////////////////////////////////////////////////
//
//  RentalController.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class RentalController {

    /** RentalService object used to interact with rentals */
    private RentalService rentalService;

    /** TransactionManager object used for transaction logic */
    private TransactionManager transactionManager;

    /** AdministrationManager object used for administrative endpoint logic */
    private AdministrationManager adminManager;

    /**
     * Constructor
     *
     * Loads all users and vehicles from the db
     */
    public RentalController() {
        rentalService = new RentalService();
        rentalService.loadUsers(); // load all users
        rentalService.loadVehicles(); // load all vehicles
        transactionManager = new TransactionManager();
    }

    /**
     * Endpoint to get all vehicles in the rental system
     * @return list of Vehicle objects
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getAllVehicles")
    public List<Vehicle> getAllVehicles() {
        rentalService.loadVehicles();
        List<Vehicle> vehicles = rentalService.getAllVehicles();
        return vehicles;
    }

    /**
     * Endpoint to get a list of vehicles, filtered by optional input parameters
     * @param make Make of vehicle
     * @param model Model of vehicle
     * @param year Year vehicle was manufactured
     * @param color Vehicle color
     * @param minCapacity Number of seats needed in vehicle
     * @param maxPrice The max amount the user wants to spend
     * @param type The type of vehicle
     * @return List of Vehicles that meet specifications
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getFilteredVehicles")
    public List<Vehicle> getFilteredVehicles(
            @RequestParam("make") Optional<String> make,
            @RequestParam("model") Optional<String> model,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("color") Optional<String> color,
            @RequestParam("minCapacity") Optional<Integer> minCapacity,
            @RequestParam("maxPrice") Optional<String> maxPrice,
            @RequestParam("type") Optional<String> type) {
        List<Vehicle> filteredVehicles = rentalService.getFilteredVehicles(make, model, year, color, minCapacity, maxPrice, type);
        return filteredVehicles;
    }

    /**
     * Endpoint used to get the total amount needed to rent a vehicle between a start and end date
     * @param vid ID of vehicle to rent
     * @param startDate Rental start date
     * @param endDate Rental end date
     * @return total cost of vehicle, rounded to 2 decimal places
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getTotalCost")
    public double getTotalCost(
            @RequestParam("vehicleId") int vid,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        Vehicle foundVehicle = rentalService.getVehicleFromId(vid);
        double calculatedPrice = transactionManager.getTotalCost(foundVehicle, startDate, endDate);
        return calculatedPrice;
    }

    /**
     * Endpoint used to rent a vehicle
     * @param vehicleId ID of vehicle
     * @param firstName User first name
     * @param lastName User last name
     * @param email User email
     * @param phoneNum User phone number
     * @param totalCost Total transaction cost amount
     * @return int representing results of rent action
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/rent", produces = {"text/plain", "application/*"})
    public int rentVehicle(
            @RequestParam("vid") int vehicleId,
            @RequestParam("fname") String firstName,
            @RequestParam("lname") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNum") String phoneNum,
            @RequestParam("totalCost") double totalCost
    ) {
        User user = new User(firstName, lastName, email, phoneNum);
        int rentResult = rentalService.rentVehicle(user, vehicleId, totalCost);
        return rentResult;
    }

    /**
     * Endpoint used to handle the return of a vehicle previously rented out
     * @param vehicleId id of vehicle to rent
     * @param userId id of user making the return
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/returnVehicle", produces = {"text/plain", "application/*"})
    public boolean returnVehicle(
            @RequestParam("vehicleId") int vehicleId,
            @RequestParam("userId") int userId) {
        return rentalService.returnVehicle(userId, vehicleId);
    }

    /**
     * Admin endpoint to add a vehicle
     * @param make vehicle make
     * @param model vehicle model
     * @param year vehicle year
     * @param color vehicle color
     * @param capacity vehicle
     * @param pricePerDay vehicle price
     * @param type vehicle type
     * @return true if vehicle was successfully added, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/admin/addVehicle", produces = {"text/plain", "application/*"})
    public boolean addVehicle(
            @RequestParam("make") String make,
            @RequestParam("model") String model,
            @RequestParam("year") int year,
            @RequestParam("color") String color,
            @RequestParam("capacity") int capacity,
            @RequestParam("price") double pricePerDay,
            @RequestParam("type") String type) {
        Vehicle newVehicle = adminManager.addVehicle(make, model, year, color, minCapacity, pricePerDay, type);
        rentalService.addVehicle(newVehicle);
        return true;
    }

    /**
     * Update an existing vehicle corresponding to given vehicle id
     * @param vid vehicle id
     * @param make vehicle make
     * @param model vehicle model
     * @param year vehicle year
     * @param color vehicle color
     * @param capacity vehicle capacity
     * @param pricePerDay vehicle price per day
     * @param type vehicle type
     * @return true if update successful, else false
     */
    @PostMapping(path = "/admin/updateVehicle", produces = {"text/plain", "application/*"})
    public boolean updateVehicle(
            @RequestParam("vid") Integer vid,
            @RequestParam("make") String make,
            @RequestParam("model") String model,
            @RequestParam("year") int year,
            @RequestParam("color") String color,
            @RequestParam("capacity") int capacity,
            @RequestParam("price") double pricePerDay,
            @RequestParam("type") String type) {
        Vehicle modifiedVehicle = adminManager.updateVehicle(vid, make, model, year, color, capacity, pricePerDay, type);
        return rentalService.replaceVehicle(modifiedVehicle);
    }

    /**
     * Deletes a vehicle corresponding to specified vehicle id from system
     * @param vehicleId id of vehicle to delete
     * @return true if delete successful, else false
     */
    @PostMapping(path = "/admin/deleteVehicle", produces = {"text/plain", "application/*"})
    public boolean deleteVehicle(
            @RequestParam("vid") Integer vehicleId) {
        // add a check to make sure that a vehicle is not currently being used by someone before deleting from db
        boolean canRemove = rentalService.deleteVehicle(vehicleId);
        if (canRemove) {
            return adminManager.deleteVehicle(vehicleId);
        }
        return false;
    }
}
