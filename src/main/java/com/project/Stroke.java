package com.project;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A freehand stroke: an ordered list of points with its own style
public class Stroke implements Drawable {

    private final List<Point2D> points = new ArrayList<>();
    private final Color color;
    private final double width;
    // Identificador único del trazo
    private int id;

    public Stroke(Color color, double width) {
        this.color = color;
        this.width = width;
    }

    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));
    }

    @Override // Indicamos que implementamos el método de la interfaz Drawable
    public void draw(GraphicsContext gc) {
        if (points.isEmpty())
            return;

        gc.setStroke(color);
        gc.setLineWidth(width);

        // Join each point with the next one
        gc.beginPath();
        gc.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point2D p : points) {
            gc.lineTo(p.getX(), p.getY());
        }
        gc.stroke();
    }

    // Calculamos el recuadro que contiene todos los puntos del trazo
    public double[] getBounds() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Point2D punto : points) {
            minX = Math.min(minX, punto.getX());
            minY = Math.min(minY, punto.getY());
            maxX = Math.max(maxX, punto.getX());
            maxY = Math.max(maxY, punto.getY());
        }

        return new double[] { minX, minY, maxX - minX, maxY - minY };
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
    // Movemos todos los puntos del trazo
    public void move(double dx, double dy) {

        for (int i = 0; i < points.size(); i++) {
            Point2D punto = points.get(i);

            double x = punto.getX() + dx;
            double y = punto.getY() + dy;

            points.set(i, new Point2D(x, y));
        }
    }

}
