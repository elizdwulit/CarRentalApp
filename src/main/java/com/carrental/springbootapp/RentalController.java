package com.carrental.springbootapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RentalController {

    private RentalService rentalService;

    RentalController() {
        rentalService = new RentalService();
        rentalService.loadUsers();
        rentalService.loadVehicles();
    }

    @GetMapping("/getAllVehicles")
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = rentalService.getAllVehicles();
        return vehicles;
    }

    @GetMapping("/getFilteredVehicles")
    public List<Vehicle> getFilteredVehicles(
            @RequestParam("color") Optional<String> color,
            @RequestParam("maxCapacity") Optional<Integer> maxCapacity,
            @RequestParam("maxPrice") Optional<String> maxPrice,
            @RequestParam("vtype") Optional<Integer> vehicleType) {
        return new ArrayList<Vehicle>();
    }

    @PostMapping(path = "/rent", produces = {"text/plain", "application/*"})
    public void rentVehicle(
            @RequestParam("vehicleId") Optional<Integer> vid,
            @RequestParam("userFname") Optional<String> fname,
            @RequestParam("userLname") Optional<String> lname,
            @RequestParam("email") Optional<String> email,
            @RequestParam("phoneNum") Optional<String> phoneNum
    ) {
        //return "{ true }";
    }
}
