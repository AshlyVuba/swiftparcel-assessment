package za.co.swiftparcel;

/**
 * The SQL that the reporting screens run. Replace each TODO with a query that
 * satisfies the description above it. See ParcelQueriesTest for the expected
 * column names.
 */
public final class ParcelQueries {

    private ParcelQueries() {
    }

    /**
     * One parameter (?) : the customer's id.
     * Columns: tracking_no, weight_kg.
     * Every parcel that belongs to that customer, ordered by tracking_no (A to Z).
     */
    public static final String PARCELS_FOR_CUSTOMER = "TODO";

    /**
     * Columns: full_name, parcel_count.
     * One row for EVERY customer (including customers with no parcels) showing
     * how many parcels they have sent. Most parcels first; ties broken by
     * full_name (A to Z).
     */
    public static final String PARCEL_COUNT_PER_CUSTOMER = "TODO";

    /**
     * Columns: tracking_no, status.
     * One row per parcel showing the status of its most recent tracking event
     * (latest recorded_at). Parcels with no tracking events must not appear.
     * Ordered by tracking_no (A to Z).
     */
    public static final String LATEST_STATUS_PER_PARCEL = "TODO";
}
