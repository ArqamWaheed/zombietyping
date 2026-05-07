package com.typingmaster.model;

// Bullet — short visual tracer when a letter lands
public class Bullet extends Entity {
    private final double tx, ty;
    private double life = 0.18;

    public Bullet(double x, double y, double tx, double ty) {
        super(x, y);
        this.tx = tx; this.ty = ty;
    }

    @Override
    public void update(double dt) {
        // ease toward target then expire
        x += (tx - x) * 0.5;
        y += (ty - y) * 0.5;
        life -= dt;
        if (life <= 0) dead = true;
    }

    public double getLife() { return life; }
}
