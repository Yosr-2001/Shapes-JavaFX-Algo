package org.example.projetshapes;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.projetshapes.DAO.DessinDAO;
import org.example.projetshapes.DAO.ShapeDAO;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.example.projetshapes.Decorator.ColorDecorator;
import org.example.projetshapes.Factory.CircleFactory;
import org.example.projetshapes.Factory.IShapeFactory;
import org.example.projetshapes.Factory.RectangleFactory;
import org.example.projetshapes.Strategy.CircleShape;
import org.example.projetshapes.Strategy.RectangleShape;
import org.example.projetshapes.Strategy.ShapeDraw;
import org.example.projetshapes.entities.*;

import org.example.projetshapes.Logging.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {

    @FXML private Label circleLabel;
    @FXML private Label rectLabel;
    @FXML private Canvas canvas;
    @FXML
    private Button shortestPathButton;

    @FXML
    private ChoiceBox<String> loggerChoiceBox;

    private final ContextLogger logger = new ContextLogger();

    private final ShapeDAO shapeDAO = new ShapeDAO();
    private final DessinDAO dessinDAO = new DessinDAO();

    private final List<Node> graphNodes = new ArrayList<>();
    private Node selectedNode1 = null;
    private Node selectedNode2 = null;
    private Node hoveredNode = null;

    private int currentDessinId = -1;
    private int shapesCount = 0;
    @FXML
    private Button openButton;

    @FXML private ColorPicker colorPicker;
    private void updateGraphEdges() {
        for (Node node : graphNodes) {
            node.getNeighbors().clear();
            for (Node other : graphNodes) {
                if (node != other) {
                    double dist = node.distanceTo(other);
                    node.getNeighbors().add(new Edge(other, dist));
                }
            }
        }
    }


    @FXML
    public void initialize() {
        setupLoggerChoiceBox();
        createNewDessin();
        setupDragEvents();
        setupOpenButton();
        setupMouseMoveOnCanvas();
        setupMouseClickOnCanvas();
        setupShortestPathButton();
    }

    private void setupLoggerChoiceBox() {
        loggerChoiceBox.getItems().addAll("Console Logger", "File Logger");
        loggerChoiceBox.setValue("Console Logger");
        logger.setStrategy(new ConsoleLogger());

        loggerChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case "Console Logger" -> logger.setStrategy(new ConsoleLogger());
                case "File Logger" -> logger.setStrategy(new FileLogger());
            }
            logger.log("Stratégie de logger changée en : " + newVal);
        });
    }

    private void createNewDessin() {
        try {
            Dessin dessin = new Dessin();
            String uniqueName = "Dessin de " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH'h'mm'm'ss"));
            dessin.setNom(uniqueName);
            dessin.setDateCreation(LocalDateTime.now());
            dessin.setNbShapes(0);
            currentDessinId = dessinDAO.saveDessin(dessin);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log("Erreur lors de la création du dessin : " + e.getMessage());
        }
    }

    private void setupOpenButton() {
        openButton.setOnAction(e -> {
            try {
                List<Dessin> dessins = dessinDAO.getAllDessins();
                if (dessins.isEmpty()) {
                    logger.log("Aucun dessin trouvé.");
                    return;
                }

                List<String> nomsDessins = dessins.stream().map(Dessin::getNom).collect(Collectors.toList());
                ChoiceDialog<String> dialog = new ChoiceDialog<>(nomsDessins.get(0), nomsDessins);
                dialog.setTitle("Ouvrir un dessin");
                dialog.setHeaderText("Sélectionnez un dessin à ouvrir");
                dialog.setContentText("Dessins disponibles :");

                dialog.showAndWait().ifPresent(nomSelectionne -> loadSelectedDessin(nomSelectionne, dessins));
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.log("Erreur lors de la récupération des dessins : " + ex.getMessage());
            }
        });
    }

    private void loadSelectedDessin(String nomSelectionne, List<Dessin> dessins) {
        try {
            Dessin selectionne = dessins.stream().filter(d -> d.getNom().equals(nomSelectionne)).findFirst().orElse(null);
            if (selectionne != null) {
                currentDessinId = selectionne.getIdDessin();
                shapesCount = selectionne.getNbShapes();

                canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                loadSavedShapesForDessin(currentDessinId);
                logger.log("Dessin '" + nomSelectionne + "' chargé.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log("Erreur lors du chargement du dessin: " + ex.getMessage());
        }
    }

    private void setupMouseMoveOnCanvas() {
        canvas.setOnMouseMoved(e -> {
            double x = e.getX();
            double y = e.getY();
            Node found = null;

            for (Node node : graphNodes) {
                if (node != null) {
                    double dx = node.getShape().getX() - x;
                    double dy = node.getShape().getY() - y;
                    if (Math.sqrt(dx * dx + dy * dy) < 30) {
                        found = node;
                        break;
                    }
                }
            }

            if (found != hoveredNode) {
                hoveredNode = found;
                redrawCanvas();
            }
        });
    }

    private void setupMouseClickOnCanvas() {
        canvas.setOnMouseClicked(e -> {
            double clickX = e.getX();
            double clickY = e.getY();

            for (Node node : graphNodes) {
                double dx = node.getShape().getX() - clickX;
                double dy = node.getShape().getY() - clickY;
                if (Math.sqrt(dx * dx + dy * dy) < 30) {
                    handleNodeSelection(node);
                    redrawCanvas();
                    break;
                }
            }
        });
    }

    private void handleNodeSelection(Node node) {
        if (selectedNode1 == null) {
            selectedNode1 = node;
            logger.log("Forme 1 sélectionnée");
        } else if (selectedNode2 == null && node != selectedNode1) {
            selectedNode2 = node;
            logger.log("Forme 2 sélectionnée");
        } else {
            selectedNode1 = node;
            selectedNode2 = null;
            logger.log("Nouvelle sélection Forme 1");
        }
    }

    private void setupShortestPathButton() {
        shortestPathButton.setOnAction(e -> {
            if (selectedNode1 != null && selectedNode2 != null) {
                updateGraphEdges();

                List<Node> path = Dijkstra.findShortestPath(new Graph() {{
                    getNodes().addAll(graphNodes);
                }}, selectedNode1, selectedNode2);

                redrawCanvas();
                drawPath(path);

                double dist = calculatePathDistance(path);
                logger.log("Chemin tracé entre les formes sélectionnées");
                logger.log(String.format("Distance totale du chemin : %.2f pixels", dist));
            } else {
                logger.log("Veuillez sélectionner deux formes.");
            }
        });
    }

    private double calculatePathDistance(List<Node> path) {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = path.get(i);
            Node to = path.get(i + 1);
            totalDistance += from.distanceTo(to);
        }
        return totalDistance;
    }


    private void drawPath(List<Node> path) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        for (int i = 0; i < path.size() - 1; i++) {
            Node a = path.get(i);
            Node b = path.get(i + 1);
            gc.strokeLine(a.getShape().getX(), a.getShape().getY(), b.getShape().getX(), b.getShape().getY());

        }
    }

    private void loadSavedShapesForDessin(int dessinId) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        selectedNode1 = null;
        selectedNode2 = null;
        hoveredNode = null;
        redrawCanvas();

        List<Shape> shapes = shapeDAO.getShapesByDessinId(dessinId);
        graphNodes.clear();
        int index = 0;

        for (Shape s : shapes) {

            String type = s.getType();
            IShapeFactory factory;

            switch (type.toLowerCase()) {
                case "circle":
                    factory = new CircleFactory();
                    break;
                case "rectangle":
                    factory = new RectangleFactory();
                    break;
                default:
                    logger.log("Type de forme inconnu : " + type);
                    continue;
            }

            ShapeDraw shapeDraw = factory.createShape(s.getType(),s.getX(), s.getY());
            shapeDraw.draw(gc);
            graphNodes.add(new Node(index++, shapeDraw));

            logger.log("Forme dessinée depuis la BDD : " + s);
        }

        redrawCanvas();
    }

    /**Drag events ***/
    private void setupDragEvents() {
        for (Label label : new Label[]{ rectLabel ,circleLabel}) {
            label.setOnDragDetected(e -> {
                Dragboard db = label.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(label.getText());
                db.setContent(content);
                e.consume();
            });
        }

        canvas.setOnDragOver(e -> {
            if (e.getGestureSource() != canvas && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        canvas.setOnDragDropped(e -> {
             Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String type = db.getString();
                GraphicsContext gc = canvas.getGraphicsContext2D();

                double x = e.getX();
                double y = e.getY();
                Color selectedColor = colorPicker.getValue();
                ShapeDraw baseShape = switch (type) {
                    case "⬛ Rectangle" -> new RectangleShape(x, y);
                    case "⚪ Cercle" -> new CircleShape(x, y);
                    default -> null;
                };

                if (baseShape != null) {
                    ShapeDraw coloredShape = new ColorDecorator(baseShape, selectedColor);
                    coloredShape.draw(gc);

                    Node newNode = new Node(graphNodes.size(), coloredShape);
                    graphNodes.add(newNode);
                       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String timestamp = LocalDateTime.now().format(formatter);

                     String hexColor = String.format("#%02X%02X%02X",
                            (int)(selectedColor.getRed()*255),
                            (int)(selectedColor.getGreen()*255),
                            (int)(selectedColor.getBlue()*255));

                    logger.log("[" + timestamp + "] " + type + " dessiné aux coordonnées : (x=" + x + ", y=" + y +  "), couleur : " + hexColor);

                    String shapeType = type.equals("⬛ Rectangle") ? "rectangle" : "circle";
                    Shape shapeToSave = new Shape(shapeType, x, y, currentDessinId);

                    try {
                        shapeDAO.saveShape(shapeToSave);
                        shapesCount++;
                        dessinDAO.updateNbShapes(currentDessinId, shapesCount);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.log("Erreur lors de la sauvegarde de la forme : " + ex.getMessage());
                    }

                    success = true;
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }



private void redrawCanvas() {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    for (Node node : graphNodes) {
        ShapeDraw shape = node.getShape();

        if (node == selectedNode1 || node == selectedNode2) {

            Color selectionColor = Color.RED;
            ShapeDraw coloredShape = new ColorDecorator(shape, selectionColor);
            coloredShape.draw(gc);
        } else {
            shape.draw(gc);
        }

        if (node == hoveredNode && node != selectedNode1 && node != selectedNode2) {
            double x = shape.getX();
            double y = shape.getY();
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(3);
            gc.strokeOval(x - 25, y - 25, 50, 50);
        }
    }
}



}