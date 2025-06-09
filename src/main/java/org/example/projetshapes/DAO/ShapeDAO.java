package org.example.projetshapes.DAO;

import org.example.projetshapes.Logging.Logger;
import org.example.projetshapes.Singleton.DatabaseConnection;
import org.example.projetshapes.entities.Shape;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShapeDAO {

    public void saveShape(Shape shape) throws SQLException {
        String sql = "INSERT INTO shapes (type, x, y, id_dessin) VALUES (?,?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shape.getType());
            stmt.setDouble(2, shape.getX());
            stmt.setDouble(3, shape.getY());
            stmt.setInt(4, shape.getIdDessin());
            stmt.executeUpdate();
            Logger.getInstance().log("Forme sauvegardée avec succès : " + shape);
        } catch (SQLException ex) {
            System.out.println("Erreur base : " + ex.getMessage());
        }
    }

    public List<Shape> getShapesByDessinId(int dessinId) {
        List<Shape> shapes = new ArrayList<>();
        String sql = "SELECT id, type, x, y, id_dessin FROM shapes WHERE id_dessin = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dessinId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Shape shape = new Shape(
                        rs.getString("type"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getInt("id_dessin")
                );
                shapes.add(shape);
            }

        } catch (SQLException e) {
            Logger.getInstance().log("Erreur getShapesByDessinId : " + e.getMessage());
        }
        return shapes;
    }
}
