package com.project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LineShape implements Drawable {

    // Coordenadas del punto inicial y final de la línea
    private double x1, y1, x2, y2;

    // Color y grosor de la línea
    private Color color;
    private double width;
    private int id;

    // Constructor: recibimos las coordenadas y el estilo de la línea
    public LineShape(double x1, double y1, double x2, double y2, Color color, double width) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.width = width;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Configuramos el color y el grosor
        gc.setStroke(color);
        gc.setLineWidth(width);

        // Dibujamos una línea recta entre los dos puntos
        gc.strokeLine(x1, y1, x2, y2);
    }

    // Devuelve la coordenada X del primer extremo
    public double getX1() {
        return x1;
    }

    // Devuelve la coordenada Y del primer extremo
    public double getY1() {
        return y1;
    }

    // Devuelve la coordenada X del segundo extremo
    public double getX2() {
        return x2;
    }

    // Devuelve la coordenada Y del segundo extremo
    public double getY2() {
        return y2;
    }

    public double getLineWidth() {
        return width;
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
    // Movemos los dos extremos de la línea
    public void move(double dx, double dy) {
        x1 = x1 + dx;
        y1 = y1 + dy;

        x2 = x2 + dx;
        y2 = y2 + dy;
    }

}