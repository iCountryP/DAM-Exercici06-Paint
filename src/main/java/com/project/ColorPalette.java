package com.project;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.function.Consumer;

// Predefined colors drawn as circles on their own Canvas
public class ColorPalette {

    // Colores disponibles en la paleta
    private static final Color[] COLORS = {
            Color.BLACK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE
    };
    private static final double RADIUS = 18;
    private static final double SPACING = 60;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private Color selectedColor = COLORS[0];
    private Consumer<Color> onColorSelected;

    public ColorPalette(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(this::onMouseClicked);
        draw();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color color) {
        selectedColor = color;
        draw();
    }

    public void setOnColorSelected(Consumer<Color> listener) {
        this.onColorSelected = listener;
    }

    private double getCenterX(int index) {
        int columna = index % 3;
        return 20 + columna * 40;
    }

    private double getCenterY(int index) {
        int fila = index / 3;
        return 40 + fila * 80;
    }

    private void onMouseClicked(MouseEvent event) {
        for (int i = 0; i < COLORS.length; i++) {
            double x = getCenterX(i);
            double y = getCenterY(i);

            // El lápiz seleccionado está 10 píxeles más arriba
            if (COLORS[i].equals(selectedColor)) {
                y = y - 10;
            }

            // Comprobamos si hemos pulsado sobre el lápiz
            if (event.getX() >= x - 10
                    && event.getX() <= x + 10
                    && event.getY() >= y - 30
                    && event.getY() <= y + 30) {

                selectedColor = COLORS[i];
                draw();

                if (onColorSelected != null) {
                    onColorSelected.accept(selectedColor);
                }

                return;
            }
        }
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < COLORS.length; i++) {
            double x = getCenterX(i);
            double y = getCenterY(i);

            // Si el lápiz está seleccionado, lo subimos un poco
            if (COLORS[i].equals(selectedColor)) {
                y = y - 10;
            }

            // Cuerpo del lápiz
            gc.setFill(COLORS[i]);
            gc.fillRect(x - 10, y - 15, 20, 45);

            // Punta de madera del lápiz
            gc.setFill(Color.BEIGE);
            gc.fillPolygon(
                    new double[] { x - 10, x + 10, x },
                    new double[] { y - 15, y - 15, y - 30 },
                    3);

            // Mina del lápiz
            gc.setFill(COLORS[i]);
            gc.fillPolygon(
                    new double[] { x - 4, x + 4, x },
                    new double[] { y - 24, y - 24, y - 30 },
                    3);

            // Borde del cuerpo
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);
            gc.strokeRect(x - 10, y - 15, 20, 45);
        }
    }

}
