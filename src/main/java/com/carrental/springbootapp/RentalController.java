/////////////////////////////////////////////////////////
//
//  RentalController.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

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

    /**
     * Constructor
     *
     * Loads all users and vehicles from the db
     */
    public RentalController() {
        rentalService = new RentalService();
        rentalService.loadUsers(); // load all users
        rentalService.loadVehicles(); // load all vehicles
    }

    /**
     * Endpoint to get all vehicles in the rental system
     * @return list of Vehicle objects
     */
    @GetMapping("/getAllVehicles")
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = rentalService.getAllVehicles();
        return vehicles;
    }

    /**
     * Endpoint to get a list of vehicles, filtered by optional input parameters
     * @param color Vehicle color
     * @param minCapacity Number of seats needed in vehicle
     * @param maxPrice The max amount the user wants to spend
     * @param vehicleType The type of vehicle
     * @return List of Vehicles that meet specifications
     */
    @GetMapping("/getFilteredVehicles")
    public List<Vehicle> getFilteredVehicles(
            @RequestParam("color") Optional<String> color,
            @RequestParam("minCapacity") Optional<Integer> minCapacity,
            @RequestParam("maxPrice") Optional<String> maxPrice,
            @RequestParam("type") Optional<Integer> vehicleType) {
        List<Vehicle> filteredVehicles = rentalService.getFilteredVehicles(color, minCapacity, maxPrice, vehicleType);
        return filteredVehicles;
    }

    /**
     * Endpoint used to get the total amount needed to rent a vehicle between a start and end date
     * @param vid ID of vehicle to rent
     * @param startDate Rental start date
     * @param endDate Rental end date
     * @return total cost of vehicle, rounded to 2 decimal places
     */
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
    @PostMapping(path = "/returnVehicle", produces = {"text/plain", "application/*"})
    public boolean returnVehicle(
            @RequestParam("vehicleId") int vehicleId,
            @RequestParam("userId") int userId) {
        return rentalService.returnVehicle(userId, vehicleId);
    }
}
