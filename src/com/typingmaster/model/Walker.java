package com.typingmaster.model;

// Walker — average speed and word length
public class Walker extends Zombie {
    public Walker(double x, double y) {
        super(x, y, WordBank.mediumWord());
        this.kind = "walker";
        this.speed = 36;
        this.size = 36;
    }
}
