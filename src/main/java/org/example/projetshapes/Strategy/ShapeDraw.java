package org.example.projetshapes.Strategy;

import javafx.scene.canvas.GraphicsContext;

public interface ShapeDraw {
    void draw(GraphicsContext gc);
    double getX();
    double getY();

    double getWidth();
    double getHeight();
}
