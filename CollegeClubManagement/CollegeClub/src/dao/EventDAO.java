package dao;

import db.DBConnection;
import model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    public void addEvent(Event event) {
        String sql = "INSERT INTO events (event_name, event_date, organizing_club) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getEventDate());
            stmt.setInt(3, event.getOrganizingClubId());
            stmt.executeUpdate();
            System.out.println("Event added successfully: " + event.getEventName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("event_date"),
                        rs.getInt("organizing_club")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
}
}
