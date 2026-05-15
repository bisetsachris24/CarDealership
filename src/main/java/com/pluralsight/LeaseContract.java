package com.pluralsight;

public class LeaseContract extends Contract {
    private static final double EXPECTED_ENDING_VALUE_RATE = 0.50;  // 50% of original price
    private static final double LEASE_FEE_RATE = 0.07;              // 7% of original price
    private static final double LEASE_INTEREST_RATE = 0.04;         // 4.0% annual
    private static final int    LEASE_TERM_MONTHS = 36;

    public LeaseContract(String date, String customerName, String customerEmail,
                         Vehicle vehicleSold) {
        super(date, customerName, customerEmail, vehicleSold);
    }

    public double getExpectedEndingValue() {
        return getVehicleSold().getPrice() * EXPECTED_ENDING_VALUE_RATE;
    }

    public double getLeaseFee() {
        return getVehicleSold().getPrice() * LEASE_FEE_RATE;
    }

    /** Total price = depreciation (original - expected ending) + lease fee. */
    @Override
    public double getTotalPrice() {
        double original = getVehicleSold().getPrice();
        return (original - getExpectedEndingValue()) + getLeaseFee();
    }

    @Override
    public double getMonthlyPayment() {
        double r = LEASE_INTEREST_RATE / 12.0;
        double pow = Math.pow(1 + r, LEASE_TERM_MONTHS);
        return getTotalPrice() * r * pow / (pow - 1);
    }
}