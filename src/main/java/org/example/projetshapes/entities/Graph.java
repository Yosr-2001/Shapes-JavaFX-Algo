package org.example.projetshapes.entities;


import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
