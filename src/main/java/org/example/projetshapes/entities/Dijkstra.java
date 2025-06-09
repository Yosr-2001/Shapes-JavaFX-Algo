package org.example.projetshapes.entities;

import java.util.*;

public class Dijkstra {

    public static List<Node> findShortestPath(Graph graph, Node start, Node end) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Node node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current == end) break;

            for (Edge edge : current.getNeighbors()) {
                Node neighbor = edge.getTarget();
                double newDist = distances.get(current) + edge.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(0, at);
        }
        return path;
    }
}
