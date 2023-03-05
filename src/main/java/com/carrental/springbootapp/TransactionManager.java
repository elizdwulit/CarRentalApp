package com.carrental.springbootapp;

/**
 * Class used for transaction-related utilities and management
 */
public class TransactionManager {

    /** ID of type indicating a purchase-type transaction. */
    public static final int TRANSACTION_TYPE_BUY = 1;

    /** ID of type indicating a return-type transaction (i.e: returning a vehicle) */
    public static final int TRANSACTION_TYPE_RETURN = 0;

    /**
     * Constructor
     */
    public TransactionManager() {
        // empty constructor
    }

    //TODO: This entire method needs to be defined and populated.
    // Not sure how we will do this since we aren't using real transaction service yet.
    public boolean pay() {
        return false;
    }

    /**
     * Get the total cost for renting a vehicle
     * @param vehicle
     * @return
     */
    public double getTotalCost(Vehicle vehicle, String startDate, String endDate) {
        // get the num of days vehicle will be rented for
        int numDays = getNumDaysBetweenDates(startDate, endDate);

        // get daily rate for vehicle
        double vehicleDailyRate = vehicle.getPricePerDay();

        // calculate the total cost and return the total cost rounded up to 2 decimal places
        double totalCost = numDays * vehicleDailyRate;
        return (double) Math.round(totalCost * 100) / 100;
    }

    /**
     * Get the number of days between two dates
     * @param startDate
     * @param endDate
     * @return number of days between dates
     */
    private int getNumDaysBetweenDates(String startDate, String endDate) {
        // TODO: Use Java Calendar methods to calculate num days and return
        return 0;
    }
}
