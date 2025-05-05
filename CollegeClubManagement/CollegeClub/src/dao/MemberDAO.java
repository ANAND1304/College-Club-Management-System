package dao;
import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Member;

public class MemberDAO {
    public void addMember(Member member) {
        String sql = "INSERT INTO members (name, email, club_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getClubName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getClubName());
            stmt.setString(4, member.getRole());
            stmt.executeUpdate();
            System.out.println("Member added: " + member.getClubName());
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                members.add(new Member(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("club_name"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
        }
        return members;
}
}

