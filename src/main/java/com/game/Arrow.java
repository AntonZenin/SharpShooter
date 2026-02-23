package com.game;

public class Arrow {
    public double x, y;     //позиция стрелы
    public boolean active; //летит ли стрела

    public Arrow() {
        active = false;
    }

    public void shoot(double startX, double startY) {
        x = startX;
        y = startY;
        active = true;
    }
}
