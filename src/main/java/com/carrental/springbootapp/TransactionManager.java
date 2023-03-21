/////////////////////////////////////////////////////////
//
//  TransactionManager.java
//
/////////////////////////////////////////////////////////
package com.carrental.springbootapp;

import java.text.SimpleDateFormat;
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

    /** The format used for time ranges */
    private static final String TIMERANGE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
     * @param vehicle the vehicle to get the cost for
     * @param startDateStr The start date of rental period
     * @param endDateStr The end date of the rental period
     * @return double representing total cost for rental period
     */
    public double getTotalCost(Vehicle vehicle, String startDateStr, String endDateStr) {
        // get the number of days between the start and end date (Note: Time is currently not considered)
        long numDays = getNumDaysBetweenDates(startDateStr, endDateStr);

        // get daily rate for vehicle
        double vehicleDailyRate = Double.parseDouble(vehicle.getPricePerDay());

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
        long numDays = 0;
        try {
            // get the num of days vehicle will be rented for
            SimpleDateFormat formatter = new SimpleDateFormat(TIMERANGE_DATE_FORMAT);
            Date startDate = formatter.parse(startDateStr);
            Date endDate = formatter.parse(endDateStr);

            // parse the start date
            Calendar startDateCal = Calendar.getInstance();
            startDateCal.setTime(startDate);

            // parse the end date
            Calendar endDateCal = Calendar.getInstance();
            endDateCal.setTime(endDate);

            numDays = ChronoUnit.DAYS.between(startDateCal.toInstant(), endDateCal.toInstant());
        } catch (Exception e) {
            System.out.println("TransactionManager.getNumDaysBetweenDates -- Exception parsing dates");
            System.out.println(e);
        }
        return numDays;
    }
}
