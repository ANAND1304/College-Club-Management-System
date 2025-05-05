package model;

public class Event {
    private int eventId;
    private String eventName;
    private String eventDate;
    private int organizingClubId;

    public Event(int eventId, String eventName, String eventDate, int organizingClubId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.organizingClubId = organizingClubId;
    }
    public int  getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }
    public String getEventDate() {
        return eventDate;
    }
    public int  getOrganizingClubId() {
        return organizingClubId;
    }

    public void displayDetails() {
        System.out.println("Event ID: " + eventId);
        System.out.println("Event Name: " + eventName);
        System.out.println("Event Date: " + eventDate);
        System.out.println("Organizing Club ID: " + organizingClubId);
}
}