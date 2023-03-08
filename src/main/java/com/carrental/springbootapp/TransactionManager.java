/////////////////////////////////////////////////////////
//
//  TransactionManager.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

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

    /**
     * Make a payment
     * @return true if payment successful, else false
     */
    public boolean pay() {
        //TODO: This entire method needs to be defined and populated.
        // Not sure how we will do this since we aren't using real transaction service yet.
        return true;
    }

    /**
     * Get the total cost for renting a vehicle
     * @param vehicle
     * @return
     */
    public double getTotalCost(Vehicle vehicle, String startDate, String endDate) {
        // get the num of days vehicle will be rented for
        long numDays = getNumDaysBetweenDates(startDate, endDate);

        // get daily rate for vehicle
        double vehicleDailyRate = vehicle.getPricePerDay();

        // calculate the total cost and return the total cost rounded up to 2 decimal places
        double totalCost = numDays * vehicleDailyRate;
        return (double) Math.round(totalCost * 100) / 100;
    }

    /**
     * Get the number of days between two dates
     * @param startDateStr The start date as a String
     * @param endDateStr The end date as a String
     * @return number of days between dates
     */
    private long getNumDaysBetweenDates(String startDateStr, String endDateStr) {
        // parse the start date
        Date startDate = new Date(startDateStr);
        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTime(startDate);

        // parse the end date
        Date endDate = new Date(endDateStr);
        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTime(endDate);

        long numDays = ChronoUnit.DAYS.between(startDateCal.toInstant(), endDateCal.toInstant());

        return numDays;
    }
}
