package za.co.swiftparcel;

import com.google.gson.Gson;

public final class ParcelJson {

    private static final Gson GSON = new Gson();

    private ParcelJson() {
    }

    public static String toJson(Parcel parcel) {
        return GSON.toJson(parcel);
    }
}
