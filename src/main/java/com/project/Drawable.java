package com.project;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    public void draw(GraphicsContext gc);

    // Todas las figuras deben poder moverse
    public void move(double dx, double dy);

    int getId();

    void setId(int id);

}
