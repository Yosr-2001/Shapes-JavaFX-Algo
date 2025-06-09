package org.example.projetshapes.Decorator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.projetshapes.Strategy.ShapeDraw;

public class ColorDecorator implements ShapeDraw {
    private final ShapeDraw decoratedShape;
    private final Color color;

    public ColorDecorator(ShapeDraw shape, Color color) {
        this.decoratedShape = shape;
        this.color = color;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Color prev = (Color) gc.getStroke();
        gc.setStroke(color);
        decoratedShape.draw(gc);
        gc.setStroke(prev);

    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

}
