package za.co.swiftparcel;

public class Main {

    public static void main(String[] args) {
        Shipment economy = new EconomyShipment("ECO-1001", 4.0);
        Shipment express = new ExpressShipment("EXP-2001", 4.0);
        System.out.println(economy);
        System.out.println(express);

        Parcel parcel = new Parcel("SP00000001", 4.0, true, 1200.0, Zone.REGIONAL);
        QuoteService quotes = new QuoteService();
        System.out.println(ParcelJson.toJson(parcel));
        System.out.printf("Quote: R%.2f%n", quotes.calculateQuote(parcel));
    }
}
