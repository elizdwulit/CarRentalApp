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
 * users [id, first_name, last_name, email, phone_num]
 * vehicles [id, model_name, color, max_capacity, daily_price, v_type, is_taken, curr_user_id]
 * vehicle_types [id, name]
 * transaction_history [id, timestamp, user_id, vehicle_id, total_amount, transaction_type]
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
        System.out.println("DBUtils.getAllVehicles -- BEGIN");

        String sqlStr = "SELECT * FROM vehicles";
        Map<Integer, Vehicle> allVehicles = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlStr))
        {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("model_name");
                String color = rs.getString("color");
                int maxCapacity = rs.getInt("max_capacity");
                double dailyPrice = rs.getDouble("daily_price");
                int type = rs.getInt("v_type");
                int isTaken = rs.getInt("is_taken");
                int currUserId = rs.getInt("curr_user_id");

                // loop through results and create vehicle objects from retrieved data
                Vehicle v = new Vehicle();
                v.setId(id);
                v.setModelName(name);
                v.setColor(color);
                v.setMaxCapacity(maxCapacity);
                v.setPricePerDay(dailyPrice);
                v.setType(type);
                v.setAvailable(isTaken == 0);
                v.setCurrentRenterId(currUserId);

                // add vehicle entry to map to be returned
                allVehicles.put(id, v);
            }
        } catch (Exception e) {
            System.out.println("DBUtils.getAllVehicles -- Exception getting all vehicles");
            System.out.println(e);
        }

        System.out.println("DBUtils.getAllVehicles -- END");

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
        System.out.println("DBUtils.setVehicleTaken -- BEGIN");
        System.out.println("DBUtils.setVehicleTaken -- Updating vehicle " + vehicleId + " with taken value " + takenStatus);

        boolean updateSuccessful = false;
        String sqlStr = "UPDATE vehicles SET is_taken = ? AND curr_user_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             PreparedStatement pstmt = conn.prepareStatement(sqlStr))
        {
            pstmt.setInt(1, takenStatus ? 0 : 1);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, vehicleId);
            pstmt.executeUpdate();
            updateSuccessful = true;
        } catch (Exception e) {
            System.out.println("DBUtils.setVehicleTaken -- Exception updating vehicle entry");
            System.out.println(e);
        }

        System.out.println("DBUtils.setVehicleTaken -- END");
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
        System.out.println("DBUtils.addTransactionEntry -- BEGIN");
        System.out.println("DBUtils.addTransactionEntry -- Add transaction entry [userId=" + userId + ", vehicleId=" + vehicleId + ", amount=" + amount + "]");

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
            addSuccessful = pstmt.execute();
        } catch (Exception e) {
            System.out.println("DBUtils.addTransactionEntry -- Exception adding transaction entry");
            System.out.println(e);
        }

        System.out.println("DBUtils.addTransactionEntry -- END");
        return addSuccessful;
    }


    /**
     * Get map of all users
     * @return Map containing all users in the system
     */
    public Map<Integer, User> getAllUsers() {
        System.out.println("DBUtils.getAllUsers -- BEGIN");

        // NOTE: This may be able to be condensed to just a set of user ids, depending on if we need all the user info or not
        String sqlStr = "SELECT * FROM users";
        Map<Integer, User> foundUsers = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(dbConnStr, dbConnProps);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlStr)) {

            User user = new User();
            int userId = rs.getInt("id");
            user.setId(userId);
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNum(rs.getString("phoneNum"));

            foundUsers.put(userId, user);

        } catch (Exception e) {
            System.out.println("DBUtils.getAllUsers -- Exception getting all users");
            System.out.println(e);
        }

        System.out.println("DBUtils.getAllUsers -- END");

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
        System.out.println("DBUtils.addUser -- BEGIN");
        System.out.println("DBUtils.addUser -- Adding new user [first_name=" + firstName + ", last_name=" + lastName + ", email=" + email + ", phoneNum=" + phoneNum + "]");

        int addedUserId = -1;
        try {
            // TODO: implement method. See addTransactionEntry(...) for example INSERT usage
            // INSERT INTO users (id, first_name, last_name, email, phoneNum) VALUES (NULL, ?, ?, ?, ?)
            addedUserId = 0; // TODO: replace 0 with newly inserted user entry id

            System.out.println("DBUtils.addUser -- Added new user. Generated user id= " + addedUserId);

        } catch (Exception e) {
            System.out.println("DBUtils.addUser -- Exception adding user");
            System.out.println(e);
        }

        System.out.println("DBUtils.addUser -- END");
        return addedUserId; // return -1 by default
    }

    /**
     * Delete a user from the db
     *
     * @param userId id of user to delete
     * @return true if delete successful, else false
     */
    public boolean deleteUser(int userId) {
        System.out.println("DBUtils.deleteUser -- BEGIN");
        System.out.println("DBUtils.deleteUser -- Deleting user with id=" + userId);

        boolean deleteSuccessful = false;
        try {
            // TODO: implement method
            // Note - we can call this method when a user is done renting/has returned the vehicle
            // In a more complex system with user management, a user can have the option to delete their account
            // DELETE FROM users WHERE id = ?
            // Similar to the addTransactionEntry INSERT logic
            deleteSuccessful = true;
        } catch (Exception e) {
            System.out.println("DBUtils.deleteUser -- Exception deleting user " + userId);
            System.out.println(e);
        }

        System.out.println("DBUtils.deleteUser -- END");
        return deleteSuccessful;
    }

    /**
     * NOT PLANNING TO BE USED -- Modify a user entry in the db
     *
     * @param user user info to replace
     * @return true if update successful, else false
     */
    public boolean modifyUser(User user) {
        System.out.println("DBUtils.modifyUser -- BEGIN");

        // This method is not needed for this level of prototype, but it is here for future if desired

        System.out.println("DBUtils.modifyUser -- END");
        return false;
    }
}
