package org.example.projetshapes.Factory;

import org.example.projetshapes.Strategy.ShapeDraw;

public interface IShapeFactory {
    ShapeDraw createShape(String type, double x, double y);

}
