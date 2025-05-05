package model;

public class Member extends Person {
    private String clubName;
    private String role;
    private String email;

    public Member(int id, String name, String email, String clubName, String role) {
        super(id, name, email);
        this.clubName = clubName;
        this.role = role;
        this.email = email;
    }

    public String getClubName() {
        return clubName;
    }

    public String getRole() {
        return role;
    }
    public String getEmail() {
        return email;
    }


    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Club: " + clubName);
        System.out.println("Role: " +role);
        System.out.println("Email: " +email);
}
}