package com.typingmaster.model;

// Zombie — base class for all enemy types
public class Zombie extends Entity {
    protected String word;
    protected int typed = 0;          // letters matched so far
    protected double speed = 28;      // px / sec moving toward base
    protected int size = 36;
    protected String kind = "walker";

    public Zombie(double x, double y, String word) {
        super(x, y);
        this.word = word;
    }

    @Override
    public void update(double dt) { x -= speed * dt; }

    public String remaining() { return word.substring(typed); }
    public String typedPart()  { return word.substring(0, typed); }

    public boolean tryType(char ch) {
        if (typed < word.length() && word.charAt(typed) == ch) {
            typed++;
            return true;
        }
        return false;
    }

    public boolean isFullyTyped() { return typed >= word.length(); }

    public String getWord() { return word; }
    public int getSize()    { return size; }
    public String getKind() { return kind; }
}
