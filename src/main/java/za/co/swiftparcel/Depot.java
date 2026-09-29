package za.co.swiftparcel;

public class Depot {

    private final String name;
    private final Zone zone;
    private final double maxWeightKg;
    private final boolean acceptsFragile;
    private int spacesRemaining;
    private boolean open;

    /**
     * @param zone the only zone this depot serves, or {@code null} if it serves every zone
     */
    public Depot(String name, Zone zone, double maxWeightKg, boolean acceptsFragile, int spacesRemaining) {
        this.name = name;
        this.zone = zone;
        this.maxWeightKg = maxWeightKg;
        this.acceptsFragile = acceptsFragile;
        this.spacesRemaining = spacesRemaining;
        this.open = true;
    }

    public String getName() {
        return name;
    }

    public Zone getZone() {
        return zone;
    }

    public double getMaxWeightKg() {
        return maxWeightKg;
    }

    public boolean acceptsFragile() {
        return acceptsFragile;
    }

    public int getSpacesRemaining() {
        return spacesRemaining;
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        this.open = false;
    }

    public void reopen() {
        this.open = true;
    }

    @Override
    public String toString() {
        return name;
    }
}
