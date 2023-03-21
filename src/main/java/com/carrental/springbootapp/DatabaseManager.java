/////////////////////////////////////////////////////////
//
//  DatabaseManager.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton class used to interact with the vehicle rental database.
 * Implemented as Singleton so only one DatabaseManager exists at a time.
 *
 * The database has the following tables:
 * users [id, timestamp, first_name, last_name, email, phone_num]
 * vehicles [id, make, model, year, color, capacity, daily_price, type, is_taken, curr_user]
 * transaction_history [id, timestamp, user_id, vehicle_id, total_amount, transaction_type]
 * transcation_types [id, name]
 */
public class DatabaseManager {

    private static DatabaseManager INSTANCE;

    private String dbConnStr;

    private Properties dbConnProps = new Properties();

    /**
     * Empty private constructor used for singleton instance
     */
    private DatabaseManager() {
        String bitApiKey = "v2_3ywk4_KzvpSAfSp9hTsHm8v4hzMuW";
        String bitDB = "edwulit.cardata";
        String bitUser = "edwulit";
        String bitHost = "db.bit.io";
        String bitPort = "5432";
        dbConnProps.setProperty("sslmode", "require");
        dbConnProps.setProperty("user", bitUser);
        dbConnProps.setProperty("password", bitApiKey);
        dbConnStr = "jdbc:postgresql://" + bitHost + ":" + bitPort + "/" + bitDB;
    }

    /**
     * Get instance of the DatabaseManager class.
     * @return instance of DatabaseManager
     */
    public static DatabaseManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DatabaseManager();
        }
        return INSTANCE;
    }

    /**
     * Get map of all vehicles in the system
     * @return Map containing vehicles that are available to rent, indexed by vehicle id
     */
    public Map<Integer, Vehicle> getAllVehicles() {
        System.out.println("DatabaseManager.getAllVehicles -- BEGIN");

        String sqlStr = "SELECT * FROM vehicles";
        Map<Integer, Vehicle> allVehicles = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlStr))
        {
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setMake(rs.getString("make"));
                v.setModel(rs.getString("model"));
                v.setYear(rs.getInt("year"));
                v.setColor(rs.getString("color"));
                v.setCapacity(rs.getInt("capacity"));
                v.setPricePerDay(rs.getString("daily_price"));
                v.setType(rs.getString("type"));
                v.setTaken(rs.getBoolean("is_taken"));
                v.setCurrentRenterId(rs.getInt("curr_user_id"));
                v.setImgUrl(rs.getString("image"));

                // add vehicle entry to map to be returned
                allVehicles.put(v.getId(), v);
            }
        } catch (Exception e) {
            System.out.println("DatabaseManager.getAllVehicles -- Exception getting all vehicles");
            System.out.println(e);
        }

        System.out.println("DatabaseManager.getAllVehicles -- END");

        // return the final set
        return allVehicles;
    }

    /**
     * Mark a vehicle as taken or not taken
     *
     * @param vehicleId id of vehicle to udpate
     * @param takenStatus boolean indicating what to set the vehicle taken status as
     * @param userId the id of the user that rented it (-1 if returning the vehicle)
     * @return true if update successful, else false
     */
    public boolean setVehicleTaken(int vehicleId, boolean takenStatus, int userId) {
        System.out.println("DatabaseManager.setVehicleTaken -- BEGIN");
        System.out.println("DatabaseManager.setVehicleTaken -- Updating vehicle " + vehicleId + " with taken value " + takenStatus);

        boolean updateSuccessful = false;
        String sqlStr = "UPDATE vehicles SET is_taken = ?, curr_user_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             PreparedStatement pstmt = conn.prepareStatement(sqlStr))
        {
            pstmt.setBoolean(1, takenStatus);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, vehicleId);
            pstmt.executeUpdate();
            updateSuccessful = true;
            System.out.println("DatabaseManager.setVehicleTaken -- Successfully updated vehicle entry for vid: " + vehicleId);
        } catch (Exception e) {
            System.out.println("DatabaseManager.setVehicleTaken -- Exception updating vehicle entry");
            System.out.println(e);
        }

        System.out.println("DatabaseManager.setVehicleTaken -- END");
        return updateSuccessful;
    }

    /**
     * Add a transaction to the transaction_history table
     *
     * @param userId id of user that made purchase
     * @param vehicleId id of vehicle that was rented
     * @param amount amount associated with the transaction
     * @return true if entry successfully adeded, else false
     */
    public boolean addTransactionEntry(int userId, int vehicleId, double amount, int transactionType) {
        System.out.println("DatabaseManager.addTransactionEntry -- BEGIN");
        System.out.println("DatabaseManager.addTransactionEntry -- Add transaction entry [userId=" + userId + ", vehicleId=" + vehicleId + ", amount=" + amount + "]");

        boolean addSuccessful = false;

        String sqlStr = "INSERT INTO transaction_history (timestamp, user_id, vehicle_id, total_amount, transaction_type) "
                + " VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            PreparedStatement pstmt = conn.prepareStatement(sqlStr)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, userId);
            pstmt.setInt(3, vehicleId);
            pstmt.setDouble(4, amount);
            pstmt.setInt(5, transactionType);
            pstmt.execute();
            addSuccessful = true;
        } catch (Exception e) {
            System.out.println("DatabaseManager.addTransactionEntry -- Exception adding transaction entry");
            System.out.println(e);
        }

        System.out.println("DatabaseManager.addTransactionEntry -- END");
        return addSuccessful;
    }


    /**
     * Get map of all users
     * @return Map containing all users in the system
     */
    public Map<Integer, User> getAllUsers() {
        System.out.println("DatabaseManager.getAllUsers -- BEGIN");

        // NOTE: This may be able to be condensed to just a set of user ids, depending on if we need all the user info or not
        String sqlStr = "SELECT * FROM users";
        Map<Integer, User> foundUsers = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlStr)) {
            while (rs.next()) {
                User user = new User();
                int userId = rs.getInt("id");
                user.setId(userId);
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone_num"));

                foundUsers.put(userId, user);
            }
        } catch (Exception e) {
            System.out.println("DatabaseManager.getAllUsers -- Exception getting all users");
            System.out.println(e);
        }

        System.out.println("DatabaseManager.getAllUsers -- END");

        // return the final set
        return foundUsers;
    }

    /**
     * Add a user to the db. A new user entry will have a unique integer id assigned.
     * @param user User to add
     * @return id of newly added user
     */
    public int addUser(User user) {
        return addUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNum());
    }

    /**
     * Add a user to the db. A new user entry will have a unique integer id assigned.
     *
     * @param firstName first name
     * @param lastName last/family name
     * @param email email
     * @param phoneNum phone number
     * @return the auto-assigned id of the added user
     */
    public int addUser(String firstName, String lastName, String email, String phoneNum) {
        System.out.println("DatabaseManager.addUser -- BEGIN");
        System.out.println("DatabaseManager.addUser -- Adding new user [first_name=" + firstName + ", last_name=" + lastName + ", email=" + email + ", phoneNum=" + phoneNum + "]");

        // Set user id to -1. Should be reassigned after INSERT completes
        int addedUserId = -1;

        String sqlStr = "INSERT INTO users (first_name, last_name, email, phone_num) "
                    + " VALUES (?, ?, ?, ?) RETURNING id";
        try {
            Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            // Create statement to insert the user into the db
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);
            int i = 0;
            pstmt.setString(++i, firstName);
            pstmt.setString(++i, lastName);
            pstmt.setString(++i, email);
            pstmt.setString(++i, phoneNum);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) { // get the resulting ID of the inserted row
                addedUserId = rs.getInt("id");
            }
        } catch (Exception e) {
            System.out.println("DatabaseManager.addUser -- Exception adding user");
            System.out.println(e);
        }

        System.out.println("DatabaseManager.addUser -- Added new user. Generated user id= " + addedUserId);
        System.out.println("DatabaseManager.addUser -- END");
        return addedUserId; // return -1 by default
    }

    /**
     * Delete a user from the db
     *
     * @param userId id of user to delete
     * @return true if delete successful, else false
     */
    public boolean deleteUser(int userId) {
        System.out.println("DatabaseManager.deleteUser -- BEGIN");
        System.out.println("DatabaseManager.deleteUser -- Deleting user with id=" + userId);

        boolean deleteSuccessful = false;
        String sqlStr = "DELETE FROM users WHERE id = " + userId;
        try(Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            PreparedStatement st = conn.prepareStatement(sqlStr)) {
            st.executeUpdate();
            deleteSuccessful = true;
            System.out.println("DatabaseManager.deleteUser -- Successfully deleted user: " + userId);
        } catch (Exception e) {
            System.out.println("DatabaseManager.deleteUser -- Exception deleting user " + userId);
            System.out.println(e);
        }

        System.out.println("DatabaseManager.deleteUser -- END");
        return deleteSuccessful;
    }

    /**
     * Modify a user entry in the db
     * @param userId ID of user to update information for
     * @param user user info to replace
     * @return true if update successful, else false
     */
    public boolean modifyUser(int userId, User user) {
        System.out.println("DatabaseManager.modifyUser -- BEGIN");
        System.out.println("DatabaseManager.modifyUser -- Modifying user with userId=" + userId);
        boolean updateSuccessful = false;
        String sqlStr = "UPDATE users SET"
                + " first_name='" + user.getFirstName() + "'"
                + ", last_name='" + user.getLastName() + "'"
                + ", email='" + user.getEmail() + "'"
                + ", phone_num='" + user.getPhoneNum() + "'"
                + " WHERE id = " + userId;
        try(Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            PreparedStatement st = conn.prepareStatement(sqlStr)) {
            st.executeUpdate();
            updateSuccessful = true;
            System.out.println("DatabaseManager.modifyUser -- Successfully updated user: " + userId);
        } catch (Exception e) {
            System.out.println("DatabaseManager.modifyUser -- Exception updating vehicle " + userId);
            System.out.println(e);
        }
        System.out.println("DatabaseManager.modifyUser -- END");
        return updateSuccessful;
    }

    /**
     * Add a new vehicle to the database
     * @param v The new vehicle
     * @return auto-generated ID of added vehicle. -1 if failed to add
     */
    public int addVehicle(Vehicle v) {
        System.out.println("DatabaseManager.addVehicle -- BEGIN");

        int addedVehicleId = -1; // default -1
        String sqlStr = "INSERT INTO vehicles (make, model, year, color, capacity, daily_price, type, is_taken, curr_user_id, image) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ? , ?, ?) RETURNING id";
        try {
            Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            // Create statement to insert the vehicle into the db
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);
            int i = 0;
            pstmt.setString(++i, v.getMake());
            pstmt.setString(++i, v.getModel());
            pstmt.setInt(++i, v.getYear());
            pstmt.setString(++i, v.getColor());
            pstmt.setInt(++i, v.getCapacity());
            pstmt.setDouble(++i, Double.parseDouble(v.getPricePerDay()));
            pstmt.setString(++i, v.getType());
            pstmt.setBoolean(++i, false);
            pstmt.setInt(++i, -1);
            pstmt.setString(++i, "");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { // get the resulting ID of the inserted row
                addedVehicleId = rs.getInt("id");
            }
        } catch (Exception e) {
            System.out.println("DatabaseManager.addVehicle -- Exception adding vehicle");
            System.out.println(e);
        }
        return addedVehicleId;
    }

    /**
     * Delete a vehicle from the database
     * @param vehicleId id of vehicle to delete
     * @return true if delete successful, else false
     */
    public boolean deleteVehicle(int vehicleId) {
        System.out.println("DatabaseManager.deleteVehicle -- BEGIN");
        System.out.println("DatabaseManager.deleteVehicle -- Deleting user with id=" + vehicleId);

        boolean deleteSuccessful = false;
        String sqlStr = "DELETE FROM vehicles WHERE id = " + vehicleId + " AND curr_user_id != 0";
        try(Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            PreparedStatement st = conn.prepareStatement(sqlStr)) {
            st.executeUpdate();
            deleteSuccessful = true;
            System.out.println("DatabaseManager.deleteVehicle -- Successfully deleted vehicle: " + vehicleId);
        } catch (Exception e) {
            System.out.println("DatabaseManager.deleteVehicle -- Exception deleting vehicle " + vehicleId);
            System.out.println(e);
        }

        System.out.println("DatabaseManager.deleteVehicle -- END");
        return deleteSuccessful;
    }

    /**
     * Update a vehicle in the database
     * @param vid id of vehicle to update
     * @param v Vehicle object containing new information
     * @return true if update successful, else false
     */
    public boolean updateVehicle(int vid, Vehicle v) {
        System.out.println("DatabaseManager.updateVehicle -- BEGIN");
        System.out.println("DatabaseManager.updateVehicle -- Modifying vehicle with vid=" + vid);

        boolean updateSuccessful = false;
        String sqlStr = "UPDATE vehicles SET"
                + " make='" + v.getMake() + "'"
                + ", model='" + v.getModel() + "'"
                + ", year=" + v.getYear()
                + ", color='" + v.getColor() + "'"
                + ", capacity=" + v.getCapacity()
                + ", daily_price='" + v.getPricePerDay()  + "'"
                + ", type='" + v.getType() + "'"
                + " WHERE id = " + vid;
        try(Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
            PreparedStatement st = conn.prepareStatement(sqlStr)) {
            st.executeUpdate();
            updateSuccessful = true;
            System.out.println("DatabaseManager.updateVehicle -- Successfully updated vehicle: " + vid);
        } catch (Exception e) {
            System.out.println("DatabaseManager.updateVehicle -- Exception updating vehicle " + vid);
            System.out.println(e);
        }

        System.out.println("DatabaseManager.updateVehicle -- END");
        return updateSuccessful;
    }
}
