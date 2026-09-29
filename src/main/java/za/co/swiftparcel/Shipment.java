package za.co.swiftparcel;

public abstract class Shipment {

    private final String reference;
    private final double weightKg;

    protected Shipment(String reference, double weightKg) {
        this.reference = reference;
        this.weightKg = weightKg;
    }

    public String getReference() {
        return reference;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public abstract double deliveryFee();

    public abstract int estimatedDays();

    @Override
    public String toString() {
        return String.format(
                "%s[reference=%s, weightKg=%.1f, fee=%.2f, estimatedDays=%d]",
                getClass().getSimpleName(), reference, weightKg, deliveryFee(), estimatedDays());
    }
}
