package za.co.swiftparcel;

public class ExpressShipment extends Shipment {

    public static final double BASE_FEE = 120.00;
    public static final double RATE_PER_KG = 18.00;
    private static final int ESTIMATED_DAYS = 1;

    public ExpressShipment(String reference, double weightKg) {
        super(reference, weightKg);
    }

    @Override
    double deliveryFee() {
        return BASE_FEE + getWeightKg() * RATE_PER_KG;
    }

    @Override
    public int estimatedDays() {
        return ESTIMATED_DAYS;
    }
}
