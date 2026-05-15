package com.pluralsight;

public class SalesContract extends Contract {
    private static final double SALES_TAX_RATE = 0.05;
    private static final double RECORDING_FEE  = 100.00;
    private static final double LOW_PROCESSING_FEE  = 295.00;  // price < $10,000
    private static final double HIGH_PROCESSING_FEE = 495.00;  // price >= $10,000
    private static final double PROCESSING_FEE_THRESHOLD = 10_000.00;

    // Loan terms
    private static final double HIGH_LOAN_RATE = 0.0425;  // price >= $10,000
    private static final int    HIGH_LOAN_MONTHS = 48;
    private static final double LOW_LOAN_RATE  = 0.0525;  // price <  $10,000
    private static final int    LOW_LOAN_MONTHS  = 24;

    private boolean financed;

    public SalesContract(String date, String customerName, String customerEmail,
                         Vehicle vehicleSold, boolean financed) {
        super(date, customerName, customerEmail, vehicleSold);
        this.financed = financed;
    }

    public boolean isFinanced() { return financed; }
    public void setFinanced(boolean financed) { this.financed = financed; }

    public double getSalesTaxAmount() {
        return getVehicleSold().getPrice() * SALES_TAX_RATE;
    }

    public double getRecordingFee() {
        return RECORDING_FEE;
    }

    public double getProcessingFee() {
        return getVehicleSold().getPrice() < PROCESSING_FEE_THRESHOLD
                ? LOW_PROCESSING_FEE
                : HIGH_PROCESSING_FEE;
    }

    @Override
    public double getTotalPrice() {
        return getVehicleSold().getPrice()
                + getSalesTaxAmount()
                + getRecordingFee()
                + getProcessingFee();
    }

    @Override
    public double getMonthlyPayment() {
        if (!financed) return 0.0;

        double principal = getTotalPrice();
        double annualRate;
        int months;
        if (getVehicleSold().getPrice() >= PROCESSING_FEE_THRESHOLD) {
            annualRate = HIGH_LOAN_RATE;
            months = HIGH_LOAN_MONTHS;
        } else {
            annualRate = LOW_LOAN_RATE;
            months = LOW_LOAN_MONTHS;
        }
        return amortizedPayment(principal, annualRate, months);
    }


    private static double amortizedPayment(double principal, double annualRate, int months) {
        double r = annualRate / 12.0;
        double pow = Math.pow(1 + r, months);
        return principal * r * pow / (pow - 1);
    }
}
