package com.typingmaster.model;

// Tank — slow, beefy, long words
public class Tank extends Zombie {
    public Tank(double x, double y) {
        super(x, y, WordBank.longWord());
        this.kind = "tank";
        this.speed = 18;
        this.size = 48;
    }
}
