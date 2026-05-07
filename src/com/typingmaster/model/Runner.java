package com.typingmaster.model;

// Runner — fast, short words
public class Runner extends Zombie {
    public Runner(double x, double y) {
        super(x, y, WordBank.shortWord());
        this.kind = "runner";
        this.speed = 70;
        this.size = 28;
    }
}
