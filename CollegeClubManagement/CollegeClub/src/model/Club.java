package model;

public class Club {
    private int clubId;
    private String clubName;
    private int presidentId;

    public Club(int clubId, String clubName, int presidentId) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.presidentId = presidentId;
    }

    public int getClubId() {
        return clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public int getPresidentId() {
        return presidentId;
    }

    public void displayDetails() {
        System.out.println("Club ID: " + clubId);
        System.out.println("Club Name: " + clubName);
        System.out.println("President ID: " + presidentId);
}
}