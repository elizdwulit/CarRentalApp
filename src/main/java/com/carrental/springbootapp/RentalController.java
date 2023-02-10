package com.carrental.springbootapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping(path = "/rent",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void rentCar() {
        //return "{ true }";
    }

}
