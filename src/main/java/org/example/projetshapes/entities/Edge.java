package org.example.projetshapes.entities;

public class Edge {
    public final Node target;
    public final double weight;

    public Edge(Node target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    public Node getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }
}

