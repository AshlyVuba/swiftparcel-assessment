package za.co.swiftparcel;

public class Parcel {

    private final String trackingNo;
    private final double weightKg;
    private final boolean fragile;
    private final double declaredValue;
    private final Zone zone;

    public Parcel(String trackingNo, double weightKg, boolean fragile, double declaredValue, Zone zone) {
        this.trackingNo = trackingNo;
        this.weightKg = weightKg;
        this.fragile = fragile;
        this.declaredValue = declaredValue;
        this.zone = zone;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public boolean isFragile() {
        return fragile;
    }

    public double getDeclaredValue() {
        return declaredValue;
    }

    public Zone getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return trackingNo + " (" + weightKg + "kg, " + zone + ")";
    }
}
