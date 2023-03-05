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

    /**
     * RentalService object used to interact with rentals
     */
    private RentalService rentalService;

    /**
     * Transaction Manager used to handle transaction-related operations
     */
    private TransactionManager transactionManager;

    /**
     * Constructor
     *
     * Loads all users and vehicles from the db
     */
    RentalController() {
        rentalService = new RentalService();
        rentalService.loadUsers();
        rentalService.loadVehicles();
    }

    /**
     * Get all vehicles in the rental system
     * @return list of Vehicle objects
     */
    @GetMapping("/getAllVehicles")
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = rentalService.getAllVehicles();
        return vehicles;
    }

    /**
     *
     * @param color
     * @param maxCapacity
     * @param maxPrice
     * @param vehicleType
     * @return
     */
    @GetMapping("/getFilteredVehicles")
    public List<Vehicle> getFilteredVehicles(
            @RequestParam("color") Optional<String> color,
            @RequestParam("maxCapacity") Optional<Integer> maxCapacity,
            @RequestParam("maxPrice") Optional<String> maxPrice,
            @RequestParam("vtype") Optional<Integer> vehicleType) {
        List<Vehicle> filteredVehicles = rentalService.getFilteredVehicles(color, maxCapacity, maxPrice, vehicleType);
        return filteredVehicles;
    }

    /**
     *
     * @param vid
     * @param startDate
     * @param endDate
     * @return
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
     *
     * @param vid
     * @param fname
     * @param lname
     * @param email
     * @param phoneNum
     */
    @PostMapping(path = "/rent", produces = {"text/plain", "application/*"})
    public void rentVehicle(
            @RequestParam("vehicleId") int vid,
            @RequestParam("userFname") String fname,
            @RequestParam("userLname") String lname,
            @RequestParam("email") String email,
            @RequestParam("phoneNum") String phoneNum
    ) {
        // TODO: Create a User object with the RequestParam info and set all the user variables
        // use rentalService.rentVehicle(...)
    }

    /**
     * Endpoint used to handle the return of a vehicle previously rented out
     * @param vid id of vehicle to rent
     * @param userId id of user making the return
     */
    @PostMapping(path = "/returnVehicle", produces = {"text/plain", "application/*"})
    public void returnVehicle(
            @RequestParam("vehicleId") int vid,
            @RequestParam("userId") int userId) {
        // TODO: set the Vehicle db row entry's is_taken col = 0 and curr_user_id = -1 (do this using the DatabaseManager methods)
    }
}
