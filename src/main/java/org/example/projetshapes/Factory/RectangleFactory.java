package org.example.projetshapes.Factory;

import org.example.projetshapes.Strategy.RectangleShape;
import org.example.projetshapes.Strategy.ShapeDraw;

public class RectangleFactory implements  IShapeFactory{

    @Override
    public ShapeDraw createShape(String type, double x, double y) {
        return new RectangleShape(x,y);
    }
}
