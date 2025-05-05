package dao;

import db.DBConnection;
import model.Club;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {
    public void addClub(Club club) {
        String sql = "INSERT INTO clubs (club_name, president_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getClubName());
            stmt.setInt(2, club.getPresidentId());
            stmt.executeUpdate();
            System.out.println("Club added successfully: " + club.getClubName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Club> getAllClubs() {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clubs.add(new Club(
                        rs.getInt("club_id"),
                        rs.getString("club_name"),
                        rs.getInt("president_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
}
}