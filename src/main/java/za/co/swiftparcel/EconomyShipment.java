package za.co.swiftparcel;

public class EconomyShipment {

    public static final double RATE_PER_KG = 9.50;
    private static final int ESTIMATED_DAYS = 5;

    public EconomyShipment(String reference, double weightKg) {
        super(reference, weightKg);
    }

    @Override
    public double deliveryFee() {
        return getWeightKg() * RATE_PER_KG;
    }

    @Override
    public int estimatedDays() {
        return ESTIMATED_DAYS;
    }
}
