package com.project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleShape implements Drawable {

    // Posición inicial del rectángulo
    private double x, y;

    // Ancho y alto del rectángulo
    private double width, height;

    // Color, grosor del borde y color de relleno
    private Color color;
    private double lineWidth;
    private Color fillColor;

    private int id;

    // Constructor: recibimos la posición, tamaño y colores
    public RectangleShape(double x, double y, double width, double height,
            Color color, double lineWidth, Color fillColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillColor = fillColor;
    }

    @Override
    public void draw(GraphicsContext gc) {

        // Configuramos el color y el grosor del borde
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);

        // Si tenemos color de relleno, pintamos el interior
        if (fillColor != null) {
            gc.setFill(fillColor);
            gc.fillRect(x, y, width, height);
        }

        // Dibujamos el borde del rectángulo
        gc.strokeRect(x, y, width, height);
    }

    // Devuelve la posición horizontal del rectángulo
    public double getX() {
        return x;
    }

    // Devuelve la posición vertical del rectángulo
    public double getY() {
        return y;
    }

    // Devuelve el ancho del rectángulo
    public double getWidth() {
        return width;
    }

    // Devuelve el alto del rectángulo
    public double getHeight() {
        return height;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    // Movemos el rectángulo sin cambiar su tamaño
    public void move(double dx, double dy) {
        x = x + dx;
        y = y + dy;
    }
}