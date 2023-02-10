package com.carrental.springbootapp;

/**
 * Class used to represent a user that uses the car rental system
 */
public class User {

    /** User id */
    private int id;

    /** First name of user */
    private String firstName;

    /** Last/Family name of the user */
    private String lastName;

    /** User's email */
    private String email;

    /** User's phone number */
    private String phoneNum;

    /**
     * Empty constructor
     */
    public User() {
        // empty constructor
    }

    /**
     * Constructor
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNum
     */
    public User(String firstName, String lastName, String email, String phoneNum) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                '}';
    }
}
