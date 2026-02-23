package com.game;

public class Target {
    public double x, y; // позиция мишени
    public double speed; // скорость движения вниз
    public int points; // сколько очков даётся
    public double radius; // радиус мишени для проверки попадания
    public boolean visible; // видима ли мишень

    public Target(double x, double y, double speed, int points, double radius) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.points = points;
        this.radius = radius;
        this.visible = true;
    }
}
