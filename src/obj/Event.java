package obj;

import java.util.Date;

/**
 * Event
 * A class containing typical information about all events stored in the database
 */
public class Event {
    //Fields
    int id;
    String eventName;
    Date startDate;
    Date endDate;
    String address;
    int[][] assignedAssets; //IDs of assets assigned to event with the quantity in a 2D array - IDs
    int[] assignedPeople; //Staff/freelancers assigned to the event - IDs

    public Event(String eventName, Date startDate, Date endDate, String address, int[][] assignedAssets, int[]assignedPeople){
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.assignedAssets = assignedAssets;
        this.assignedPeople = assignedPeople;
    }
}
