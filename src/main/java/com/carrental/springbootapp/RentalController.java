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
import java.util.Map;
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
        adminManager = new AdministrationManager();
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
     * Endpoint to return all vehicle makes in the system
     * @return Array of vehicle make names
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getAllMakes")
    public String[] getAllMakes() {
        return rentalService.getAllMakes().toArray(new String[0]);
    }

    /**
     * Endpoint to return all vehicle models in the system
     * @return Array of vehicle model names
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getAllModels")
    public String[] getAllModels() {
        return rentalService.getAllModels().toArray(new String[0]);
    }

    /**
     * Endpoint to return all vehicle colors in the system
     * @return Array of vehicle color names
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getAllColors")
    public String[] getAllColors() {
        return rentalService.getAllColors().toArray(new String[0]);
    }

    /**
     * Endpoint to return all vehicle types in the system
     * @return Array of vehicle type names
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getAllVehicleTypes")
    public String[] getAllVehicleTypes() {
        return rentalService.getAllVehicleTypes().toArray(new String[0]);
    }

    /**
     * Endpoint to get a single vehicle by vid
     * @param vid ID of vehicle
     * @return Vehicle object
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getVehicle")
    public Vehicle getVehicle(@RequestParam("vid") int vid) {
        return rentalService.getVehicleFromId(vid);
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
    public String getTotalCost(
            @RequestParam("vid") int vid,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        Vehicle foundVehicle = rentalService.getVehicleFromId(vid);
        double calculatedPrice = transactionManager.getTotalCost(foundVehicle, startDate, endDate);

        return String.format("%.2f", calculatedPrice);
    }

    /**
     * Endpoint used to rent a vehicle
     * @param queryParameters request query parameters
     * @return id of transaction entry, -1 if transaction failed
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/rentVehicle", produces = {"text/plain", "application/*"})
    public int rentVehicle(@RequestParam Map<String, String> queryParameters) {
        try {
            int userId = Integer.parseInt(queryParameters.get("userid"));
            int vid = Integer.parseInt(queryParameters.get("vid"));
            double totalCost = Double.parseDouble(queryParameters.get("totalcost"));
            return rentalService.rentVehicle(userId, vid, totalCost);
        } catch (Exception e) {
            System.out.println("RentalController.rentVehicle -- Exception renting vehicle");
            System.out.println(e);
            return -1;
        }
    }

    /**
     * Endpoint used to handle the return of a vehicle previously rented out
     * @param queryParameters request query parameters
     * @return true if return successful, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/returnVehicle", produces = {"text/plain", "application/*"})
    public boolean returnVehicle(@RequestParam Map<String, String> queryParameters) {
        boolean result = false;
        try {
            int userId = Integer.parseInt(queryParameters.get("userid"));
            int vehicleId = Integer.parseInt(queryParameters.get("vid"));
            result = rentalService.returnVehicle(userId, vehicleId);
        } catch (Exception e) {
            System.out.println("RentalController.returnVehicle -- Exception returning vehicle");
            System.out.println(e);
        }
        return result;
    }

    /**
     * Admin endpoint to add a vehicle
     * @param queryParameters request query parameters
     * @return true if vehicle was successfully added, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/addVehicle", produces = {"text/plain", "application/*"})
    public int addVehicle(@RequestParam Map<String, String> queryParameters) {
        int vid = -1;
        try {
            String make = queryParameters.get("make");
            String model = queryParameters.get("model");
            int year = Integer.parseInt(queryParameters.get("year"));
            String color = queryParameters.get("color");
            int capacity = Integer.parseInt(queryParameters.get("capacity"));
            String pricePerDay = queryParameters.get("price");
            String type = queryParameters.get("type");
            Vehicle newVehicle = adminManager.addVehicle(make, model, year, color, capacity, pricePerDay, type);
            rentalService.loadVehicles();
            vid = newVehicle.getId();
        } catch (Exception e) {
            System.out.println("RentalController.addVehicle -- Exception adding vehicle");
            System.out.println(e);
        }
        return vid;
    }

    /**
     * Update an existing vehicle corresponding to given vehicle id
     * @param queryParameters request query parameters
     * @return true if update successful, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/updateVehicle", produces = {"text/plain", "application/*"})
    public boolean updateVehicle(@RequestParam Map<String, String> queryParameters) {
        try {
            int vid = Integer.parseInt(queryParameters.get("vid"));
            String make = queryParameters.get("make");
            String model = queryParameters.get("model");
            int year = Integer.parseInt(queryParameters.get("year"));
            String color = queryParameters.get("color");
            int capacity = Integer.parseInt(queryParameters.get("capacity"));
            String pricePerDay = queryParameters.get("price");
            String type = queryParameters.get("type");
            Vehicle modifiedVehicle = adminManager.updateVehicle(vid, make, model, year, color, capacity, pricePerDay, type);
            rentalService.replaceVehicle(modifiedVehicle);
        } catch (Exception e) {
            System.out.println("RentalController.updateVehicle -- Exception adding vehicle");
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * Deletes a vehicle corresponding to specified vehicle id from system
     * @param queryParameters request query parameters
     * @return true if delete successful, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/deleteVehicle", produces = {"text/plain", "application/*"})
    public boolean deleteVehicle(@RequestParam Map<String, String> queryParameters) {
            try {
            int vid = Integer.parseInt(queryParameters.get("vid"));
            adminManager.deleteVehicle(vid);
            rentalService.loadVehicles();
            return true;
        } catch (Exception e) {
            System.out.println("RentalController.deleteVehicle -- Exception deleting vehicle");
            System.out.println(e);
            return false;
        }
    }

    /**
     * Endpoint to add a user to the system
     * @param queryParameters Query Parameters
     * @return id of added user
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/addUser", produces = {"text/plain", "application/*"})
    public int addUser(@RequestParam Map<String, String> queryParameters) {
        String fname = queryParameters.get("fname");
        String lname = queryParameters.get("lname");
        String email = queryParameters.get("email");
        String phoneNum = queryParameters.get("phoneNum");
        User newUser = adminManager.addUser(fname, lname, email, phoneNum);
        rentalService.loadUsers();
        return newUser.getId();
    }

    /**
     * Endpoint to get a single user by userid
     * @param userId ID of user
     * @return Vehicle object
     */
    @CrossOrigin(maxAge = 3600)
    @GetMapping("/getUser")
    public User getUser(@RequestParam("id") int userId) {
        return rentalService.getUserFromId(userId);
    }

    /**
     * Endpoint used to modify an existing user
     * @param queryParameters Query Parameters containing id of user to modify and new user information
     * @return true if modify successful, else false
     */
    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/modifyUser", produces = {"text/plain", "application/*"})
    public boolean modifyUser(@RequestParam Map<String, String> queryParameters) {
        try {
            int id = Integer.parseInt(queryParameters.get("id"));
            String fname = queryParameters.get("fname");
            String lname = queryParameters.get("lname");
            String email = queryParameters.get("email");
            String phoneNum = queryParameters.get("phoneNum");
            adminManager.modifyUser(id, fname, lname, email, phoneNum);
            rentalService.loadUsers();
            return true;
        } catch (Exception e) {
            System.out.println("RentalController.modifyUser -- Exception modifying user");
            System.out.println(e);
            return false;
        }
    }

    @CrossOrigin(maxAge = 3600)
    @PostMapping(path = "/deleteUser", produces = {"text/plain", "application/*"})
    public boolean deleteUser(@RequestParam Map<String, String> queryParameters) {
        boolean result = false;
        try {
            int id = Integer.parseInt(queryParameters.get("id"));
            result = adminManager.deleteUser(id);
            rentalService.loadUsers();
        } catch (Exception e) {
            System.out.println("RentalController.deleteUser -- Exception deleting user");
            System.out.println(e);
        }
        return result;
    }
}
