package org.example.projetshapes.Strategy;

import javafx.scene.canvas.GraphicsContext;

public class RectangleShape implements  ShapeDraw{

    private double x,y;

    private double width = 60;
    private double height = 40;
    public RectangleShape(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeRect(x, y, 100, 60);
    }


    @Override public double getX() { return x; }
    @Override public double getY() { return y; }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
