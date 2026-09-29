package za.co.swiftparcel;

/**
 * Validation rules for parcels accepted by SwiftParcel.
 *
 * <ul>
 *   <li>A parcel must weigh <b>more than 0 kg</b> and <b>at most 30 kg</b>.</li>
 *   <li>A postal code is exactly four digits (e.g. {@code 2000}).</li>
 *   <li>A tracking number is {@code SP} followed by exactly eight digits
 *       (e.g. {@code SP12345678}). The prefix is upper case.</li>
 *   <li>Chargeable weight is the larger of the actual weight and the volumetric
 *       weight, where volumetric weight = (length x width x height in cm) / 5000.</li>
 * </ul>
 */
public final class ParcelValidator {

    public static final double MAX_WEIGHT_KG = 30.0;
    private static final double VOLUMETRIC_DIVISOR = 5000.0;

    private ParcelValidator() {
    }

    /**
     * @throws IllegalArgumentException if the weight is not greater than 0 or is more than 30 kg
     */
    public static void requireValidWeight(double weightKg) {
        if (weightKg < 0 || weightKg > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    "Weight must be greater than 0 kg and at most " + MAX_WEIGHT_KG + " kg but was " + weightKg);
        }
    }

    public static boolean isValidPostalCode(String code) {
        return code != null && code.matches("\\d{4}");
    }

    public static boolean isValidTrackingNumber(String trackingNo) {
        return trackingNo != null && trackingNo.matches("SP\\d{8}");
    }

    public static double chargeableWeight(double actualKg, double lengthCm, double widthCm, double heightCm) {
        double volumetricKg = (lengthCm * widthCm * heightCm) / VOLUMETRIC_DIVISOR;
        return Math.max(actualKg, volumetricKg);
    }
}
