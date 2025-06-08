package org.example.projetshapes.entities;

import org.example.projetshapes.Strategy.ShapeDraw;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final int id;
    private final ShapeDraw shape;
    private final List<Edge> neighbors = new ArrayList<>();

    public Node(int id, ShapeDraw shape) {
        this.id = id;
        this.shape = shape;
    }

//    public double distanceTo(Node other) {
//        double dx = shape.getX() - other.shape.getX();
//        double dy = shape.getY() - other.shape.getY();
//        return Math.sqrt(dx*dx + dy*dy);
//    }

    public double distanceTo(Node other) {
        double thisCenterX = this.shape.getX() + this.shape.getX() / 2.0;
        double thisCenterY = this.shape.getY() + this.shape.getY() / 2.0;

        double otherCenterX = other.shape.getX() + other.shape.getX() / 2.0;
        double otherCenterY = other.shape.getY() + other.shape.getY() / 2.0;

        double dx = thisCenterX - otherCenterX;
        double dy = thisCenterY - otherCenterY;

        return Math.sqrt(dx * dx + dy * dy);
    }


    public List<Edge> getNeighbors() {
        return neighbors;
    }


    public ShapeDraw getShape() {
        return shape;
    }
}
