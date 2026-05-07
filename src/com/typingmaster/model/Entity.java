package com.typingmaster.model;

// base Entity — shared abstraction for anything in the world
public abstract class Entity {
    protected double x, y;
    protected boolean dead = false;

    public Entity(double x, double y) { this.x = x; this.y = y; }

    public abstract void update(double dt);

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isDead() { return dead; }
    public void kill() { dead = true; }
}
