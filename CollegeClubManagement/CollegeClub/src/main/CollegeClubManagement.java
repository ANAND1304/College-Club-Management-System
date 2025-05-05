package main;

import dao.ClubDAO;
import dao.EventDAO;
import dao.MemberDAO;
import model.Club;
import model.Event;
import model.Member;

import java.util.List;
import java.util.Scanner;

public class CollegeClubManagement {
    public static void main(String[] args) {
        MemberDAO memberDAO = new MemberDAO();
        ClubDAO clubDAO = new ClubDAO();
        EventDAO eventDAO = new EventDAO();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- College Club Management System ---");
            System.out.println("1. Add Member");
            System.out.println("2. Display All Members");
            System.out.println("3. Add Club");
            System.out.println("4. Display All Clubs");
            System.out.println("5. Add Event");
            System.out.println("6. Display All Events");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Member Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Club Name: ");
                    String clubName = scanner.nextLine();
                    System.out.print("Enter Role: ");
                    String role = scanner.nextLine();

                    Member member = new Member(0, name, email, clubName, role);
                    memberDAO.addMember(member);
                    break;

                case 2:
                    List<Member> members = memberDAO.getAllMembers();
                    System.out.println("\nAll Members:");
                    for (Member m : members) {
                        m.displayDetails();
                        System.out.println();
                    }
                    break;

                case 3:
                    System.out.print("Enter Club Name: ");
                    String club = scanner.nextLine();
                    System.out.print("Enter President ID: ");
                    int presidentId = scanner.nextInt();

                    Club newClub = new Club(0, club, presidentId);
                    clubDAO.addClub(newClub);
                    break;

                case 4:
                    List<Club> clubs = clubDAO.getAllClubs();
                    System.out.println("\nAll Clubs:");
                    for (Club c : clubs) {
                        c.displayDetails();
                        System.out.println();
                    }
                    break;

                case 5:
                    System.out.print("Enter Event Name: ");
                    String eventName = scanner.nextLine();
                    System.out.print("Enter Event Date (YYYY-MM-DD): ");
                    String eventDate = scanner.nextLine();
                    System.out.print("Enter Organizing Club ID: ");
                    int organizingClubId = scanner.nextInt();

                    Event event = new Event(0, eventName, eventDate, organizingClubId);
                    eventDAO.addEvent(event);
                    break;

                case 6:
                    List<Event> events = eventDAO.getAllEvents();
                    System.out.println("\nAll Events:");
                    for (Event e : events) {
                        e.displayDetails();
                        System.out.println();
                    }
                    break;

                case 7:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Try again.");
            }
 }}
}
