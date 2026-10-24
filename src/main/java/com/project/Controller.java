package com.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

public class Controller implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Canvas colorsCanvas;
    @FXML
    private RadioButton thickRadio;

    // Añado los tres botones radio q he creado
    @FXML
    private RadioButton freehandRadio;
    @FXML
    private RadioButton lineRadio;
    @FXML
    private RadioButton rectangleRadio;

    private GraphicsContext gc;
    private ColorPalette colorPalette;

    private static final double THIN_WIDTH = 2;
    private static final double THICK_WIDTH = 8;

    // Guardamos las líneas y rectángulos que hemos dibujado
    private final List<Drawable> shapes = new ArrayList<>();
    private Stroke currentStroke = null;

    // Controles para elegir los colores y activar el relleno
    @FXML
    private ColorPicker strokeColorPicker;
    @FXML
    private ColorPicker fillColorPicker;
    @FXML
    private CheckBox fillCheckBox;
    // Lista de objetos y botón para eliminarlos
    @FXML
    private ListView<String> objectsListView;

    @FXML
    private Button deleteButton;

    // Contador para identificar cada objeto
    private int objectId = 0;

    // Guardamos la figura que estamos dibujando
    private Drawable currentShape = null;

    // Coordenadas donde empezamos a pulsar el ratón
    private double startX, startY;

    // Guardamos si estamos moviendo un objeto seleccionado
    private boolean movingShape = false;

    // Última posición del ratón durante el movimiento
    private double lastMouseX, lastMouseY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        // The palette handles its own drawing and clicks
        colorPalette = new ColorPalette(colorsCanvas);

        colorPalette.setOnColorSelected(color -> {
            strokeColorPicker.setValue(color);
        });

        strokeColorPicker.setOnAction(event -> {
            colorPalette.setSelectedColor(strokeColorPicker.getValue());
        });

        // Drawing canvas events
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        // Detectamos cuando el usuario selecciona un objeto de la lista
        objectsListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {

                    int indice = newValue.intValue();

                    if (indice >= 0 && indice < shapes.size()) {
                        System.out.println("Objeto seleccionado: " + shapes.get(indice));
                    }

                    // Activamos el botón solo si hay un objeto seleccionado
                    if (indice >= 0 && indice < shapes.size()) {
                        deleteButton.setDisable(false);
                    } else {
                        deleteButton.setDisable(true);
                    }
                    // Actualizamos el dibujo cuando cambia la selección
                    redraw();
                });
        // Quitamos la selección cuando cambiamos de herramienta
        freehandRadio.setOnAction(event -> {
            objectsListView.getSelectionModel().clearSelection();
        });

        lineRadio.setOnAction(event -> {
            objectsListView.getSelectionModel().clearSelection();
        });

        rectangleRadio.setOnAction(event -> {
            objectsListView.getSelectionModel().clearSelection();
        });

        // Eliminamos el objeto seleccionado
        deleteButton.setOnAction(event -> {
            int indice = objectsListView.getSelectionModel().getSelectedIndex();

            if (indice >= 0 && indice < shapes.size()) {
                shapes.remove(indice);
                objectsListView.getItems().remove(indice);
                objectsListView.getSelectionModel().clearSelection();
                redraw();
            }
        });
        // Dibujamos el Canvas al iniciar la aplicación
        redraw();
    }

    private double getSelectedWidth() {
        // Comprobamos si está seleccionado el grosor grueso
        if (thickRadio.isSelected()) {
            return THICK_WIDTH;
        } else {
            return THIN_WIDTH;
        }
    }

    // Drawing canvas

    private void onMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY)
            return;

        // Comprobamos si hay un objeto seleccionado
        int indice = objectsListView.getSelectionModel().getSelectedIndex();

        if (indice >= 0 && indice < shapes.size()) {

            Drawable shape = shapes.get(indice);

            if (shape instanceof RectangleShape) {
                RectangleShape rect = (RectangleShape) shape;

                double margen = rect.getLineWidth() / 2 + 5;

                // Comprobamos si hemos pulsado dentro del recuadro
                if (event.getX() >= rect.getX() - margen
                        && event.getX() <= rect.getX() + rect.getWidth() + margen
                        && event.getY() >= rect.getY() - margen
                        && event.getY() <= rect.getY() + rect.getHeight() + margen) {

                    movingShape = true;
                    lastMouseX = event.getX();
                    lastMouseY = event.getY();

                    return;
                }
            }

            else if (shape instanceof LineShape) {
                LineShape linea = (LineShape) shape;

                // Calculamos el recuadro de la línea
                double x = Math.min(linea.getX1(), linea.getX2());
                double y = Math.min(linea.getY1(), linea.getY2());
                double width = Math.abs(linea.getX2() - linea.getX1());
                double height = Math.abs(linea.getY2() - linea.getY1());

                double margen = linea.getLineWidth() / 2 + 5;

                // Comprobamos si hemos pulsado dentro del recuadro
                if (event.getX() >= x - margen
                        && event.getX() <= x + width + margen
                        && event.getY() >= y - margen
                        && event.getY() <= y + height + margen) {

                    movingShape = true;
                    lastMouseX = event.getX();
                    lastMouseY = event.getY();

                    return;
                }
            }

            else if (shape instanceof Stroke) {
                Stroke trazo = (Stroke) shape;

                // Obtenemos los límites del trazo libre
                double[] limites = trazo.getBounds();
                double margen = trazo.getLineWidth() / 2 + 5;

                // Comprobamos si hemos pulsado dentro del recuadro
                if (event.getX() >= limites[0] - margen
                        && event.getX() <= limites[0] + limites[2] + margen
                        && event.getY() >= limites[1] - margen
                        && event.getY() <= limites[1] + limites[3] + margen) {

                    movingShape = true;
                    lastMouseX = event.getX();
                    lastMouseY = event.getY();

                    return;
                }
            }

            // Si hay una selección, no empezamos un dibujo nuevo
            return;
        }

        // Guardamos dónde hemos pulsado para empezar a dibujar
        startX = event.getX();
        startY = event.getY();

        if (freehandRadio.isSelected()) {
            // Start a new stroke with the current style
            currentStroke = new Stroke(strokeColorPicker.getValue(), getSelectedWidth());
            currentStroke.addPoint(event.getX(), event.getY());
        }

        else if (lineRadio.isSelected()) {
            // Creamos una línea que empieza donde hemos pulsado
            currentShape = new LineShape(
                    startX, startY, startX, startY,
                    strokeColorPicker.getValue(),
                    getSelectedWidth());

        }

        else if (rectangleRadio.isSelected()) {

            // Comprobamos si el usuario quiere rellenar el rectángulo
            Color fillColor = null;

            if (fillCheckBox.isSelected()) {
                fillColor = fillColorPicker.getValue();
            }
            // Creamos un rectángulo inicialmente sin tamaño
            currentShape = new RectangleShape(
                    startX, startY, 0, 0,
                    strokeColorPicker.getValue(),
                    getSelectedWidth(),
                    fillColor // De momento sin relleno
            );
        }

        redraw();
    }

    private void onMouseDragged(MouseEvent event) {

        if (movingShape) {

            // Calculamos cuánto se ha movido el ratón
            double dx = event.getX() - lastMouseX;
            double dy = event.getY() - lastMouseY;

            // Obtenemos el objeto seleccionado
            int indice = objectsListView.getSelectionModel().getSelectedIndex();

            if (indice >= 0 && indice < shapes.size()) {
                Drawable shape = shapes.get(indice);

                // Movemos el objeto
                shape.move(dx, dy);

                // Guardamos la posición actual del ratón
                lastMouseX = event.getX();
                lastMouseY = event.getY();

                redraw();
            }

            return;
        }

        int indiceSeleccionado = objectsListView.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado >= 0) {
            return;
        }

        if (freehandRadio.isSelected()) {
            // Si dibujamos a mano alzada, seguimos añadiendo puntos
            if (currentStroke == null)
                return;

            currentStroke.addPoint(event.getX(), event.getY());

        } else if (lineRadio.isSelected()) {
            // Actualizamos la línea desde el punto inicial hasta el ratón
            currentShape = new LineShape(
                    startX, startY,
                    event.getX(), event.getY(),
                    strokeColorPicker.getValue(),
                    getSelectedWidth());

        } else if (rectangleRadio.isSelected()) {
            // Calculamos la esquina superior izquierda del rectángulo
            double x = Math.min(startX, event.getX());
            double y = Math.min(startY, event.getY());

            // Calculamos el ancho y alto, siempre positivos
            double width = Math.abs(event.getX() - startX);
            double height = Math.abs(event.getY() - startY);
            // Comprobamos si el usuario quiere rellenar el rectángulo
            Color fillColor = null;

            if (fillCheckBox.isSelected()) {
                fillColor = fillColorPicker.getValue();
            }
            // Creamos el rectángulo con las coordenadas corregidas
            currentShape = new RectangleShape(
                    x, y,
                    width, height,
                    strokeColorPicker.getValue(),
                    getSelectedWidth(),
                    fillColor);
        }
        redraw();
    }

    private void onMouseReleased(MouseEvent event) {
        // Si estábamos moviendo un objeto, terminamos el movimiento
        if (movingShape) {
            movingShape = false;
            redraw();
            return;
        }
        if (freehandRadio.isSelected()) {
            if (currentStroke == null) {
                return;
            }

            // Terminamos el trazo y lo guardamos
            currentStroke.addPoint(event.getX(), event.getY());

            objectId++;
            currentStroke.setId(objectId);
            shapes.add(currentStroke);
            currentStroke = null;

            // Lo añadimos a la lista visual
            objectsListView.getItems().add("Traç lliure " + objectId);

        } else if (currentShape != null) {

            // Comprobamos qué tipo de figura hemos dibujado
            String tipo = "";

            if (lineRadio.isSelected()) {
                tipo = "Línia";
            } else if (rectangleRadio.isSelected()) {
                tipo = "Rectangle";
            }

            // Guardamos la figura y la añadimos a la lista
            objectId++;
            currentShape.setId(objectId);
            shapes.add(currentShape);
            currentShape = null;

            objectsListView.getItems().add(tipo + " " + objectId);

        }

        redraw();
    }

    private void redraw() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujamos las líneas y rectángulos guardados
        for (Drawable shape : shapes) {
            shape.draw(gc);
        }

        // Stroke being drawn right now (preview)
        if (currentStroke != null) {
            currentStroke.draw(gc);
        }

        // Mostramos la línea o rectángulo mientras lo dibujamos
        if (currentShape != null) {
            currentShape.draw(gc);
        }

        // Dibujamos el recuadro del rectángulo seleccionado
        int indice = objectsListView.getSelectionModel().getSelectedIndex();

        if (indice >= 0 && indice < shapes.size()) {

            Drawable shape = shapes.get(indice);

            // RECTNGULO
            if (shape instanceof RectangleShape) {
                RectangleShape rect = (RectangleShape) shape;

                double margen = rect.getLineWidth() / 2 + 5;

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.setLineDashes(5, 5);

                gc.strokeRect(
                        rect.getX() - margen,
                        rect.getY() - margen,
                        rect.getWidth() + margen * 2,
                        rect.getHeight() + margen * 2);

                // Quitamos los guiones para no afectar a otros dibujos
                gc.setLineDashes();
            }

            // LINEA

            else if (shape instanceof LineShape) {
                LineShape linea = (LineShape) shape;

                // Calculamos los límites de la línea
                double x = Math.min(linea.getX1(), linea.getX2());
                double y = Math.min(linea.getY1(), linea.getY2());
                double width = Math.abs(linea.getX2() - linea.getX1());
                double height = Math.abs(linea.getY2() - linea.getY1());

                // Dibujamos el recuadro discontinuo
                double margen = linea.getLineWidth() / 2 + 5;

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.setLineDashes(5, 5);

                gc.strokeRect(
                        x - margen,
                        y - margen,
                        width + margen * 2,
                        height + margen * 2);

                gc.setLineDashes();
            }

            else if (shape instanceof Stroke) {
                Stroke trazo = (Stroke) shape;

                // Obtenemos los límites de todos los puntos del trazo
                double[] limites = trazo.getBounds();
                double margen = trazo.getLineWidth() / 2 + 5;

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.setLineDashes(5, 5);

                gc.strokeRect(
                        limites[0] - margen,
                        limites[1] - margen,
                        limites[2] + margen * 2,
                        limites[3] + margen * 2);

                gc.setLineDashes();
            }

        }
    }
}
