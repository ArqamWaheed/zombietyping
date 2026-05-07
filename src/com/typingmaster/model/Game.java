package com.typingmaster.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Game — encapsulates all state and rules
public class Game {
    private final int width, height;
    private final Random rng = new Random();

    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private int score, health, wave;
    private Zombie locked;
    private double spawnTimer, waveTimer;
    private boolean running, over;

    // typing stats — only count time while user is actively typing
    private int correctChars, totalChars;
    private long activeNs;          // accumulated active typing time
    private long lastTypeNs;        // timestamp of previous keystroke
    // gaps shorter than this count as "still typing"; longer = idle/waiting and ignored.
    // 600ms is generous for in-word pauses but skips the wait between zombies.
    private static final long IDLE_GAP_NS = 600_000_000L;

    public Game(int width, int height) {
        this.width = width; this.height = height;
        reset();
    }

    public void reset() {
        zombies.clear();
        bullets.clear();
        score = 0;
        health = 100;
        wave = 1;
        locked = null;
        spawnTimer = 0;
        waveTimer = 0;
        running = false;
        over = false;
        correctChars = 0;
        totalChars = 0;
        activeNs = 0;
        lastTypeNs = 0;
    }

    public void start() {
        reset();
        running = true;
    }

    // spawn rate gets faster as waves climb
    private double spawnInterval() { return Math.max(0.7, 2.4 - wave * 0.18); }

    private void spawnZombie() {
        double y = 100 + rng.nextDouble() * (height - 240);
        double x = width + 40;
        double r = rng.nextDouble();
        double tankP = Math.min(0.25, 0.05 + wave * 0.02);
        double runnerP = 0.35;
        Zombie z;
        if (r < tankP) z = new Tank(x, y);
        else if (r < tankP + runnerP) z = new Runner(x, y);
        else z = new Walker(x, y);
        zombies.add(z);
    }

    // returns the hit point (for bullet effect) or null on miss
    public double[] typeChar(char ch) {
        if (!running || over) return null;

        // only count time between keystrokes if the gap is short — pauses don't count
        long now = System.nanoTime();
        if (lastTypeNs != 0) {
            long gap = now - lastTypeNs;
            if (gap < IDLE_GAP_NS) activeNs += gap;
        }
        lastTypeNs = now;

        totalChars++;

        // already locked — only that target accepts input
        if (locked != null && !locked.isDead()) {
            if (locked.tryType(ch)) {
                correctChars++;
                double[] hit = { locked.getX(), locked.getY() };
                if (locked.isFullyTyped()) {
                    score += 10 + locked.getWord().length() * 2;
                    locked.kill();
                    locked = null;
                }
                return hit;
            }
            return null;
        }

        // pick the closest zombie whose word starts with ch
        Zombie best = null;
        for (Zombie z : zombies) {
            if (z.isDead()) continue;
            if (z.getWord().charAt(0) == ch) {
                if (best == null || z.getX() < best.getX()) best = z;
            }
        }
        if (best != null) {
            best.tryType(ch);
            locked = best;
            correctChars++;
            return new double[] { best.getX(), best.getY() };
        }
        return null;
    }

    public void update(double dt) {
        if (!running || over) return;

        spawnTimer += dt;
        if (spawnTimer >= spawnInterval()) {
            spawnTimer = 0;
            spawnZombie();
        }

        // every 20s difficulty escalates
        waveTimer += dt;
        if (waveTimer >= 20) { waveTimer = 0; wave++; }

        for (Zombie z : zombies) {
            z.update(dt);
            if (z.getX() < 110) {
                // reached base
                z.kill();
                if (locked == z) locked = null;
                int dmg = switch (z.getKind()) {
                    case "tank"   -> 20;
                    case "runner" -> 8;
                    default       -> 12;
                };
                health -= dmg;
            }
        }
        for (Bullet b : bullets) b.update(dt);

        zombies.removeIf(Entity::isDead);
        bullets.removeIf(Entity::isDead);

        if (health <= 0) {
            health = 0;
            over = true;
            running = false;
        }
    }

    public void addBullet(Bullet b) { bullets.add(b); }

    // wpm uses only active typing time so idle pauses don't deflate it
    public int wpm() {
        if (activeNs <= 0) return 0;
        double minutes = activeNs / 60_000_000_000.0;
        return (int) Math.round((correctChars / 5.0) / minutes);
    }
    public int accuracy() {
        if (totalChars == 0) return 100;
        return (int) Math.round((correctChars * 100.0) / totalChars);
    }

    // getters used by view
    public List<Zombie> getZombies() { return zombies; }
    public List<Bullet> getBullets() { return bullets; }
    public int getScore()   { return score; }
    public int getHealth()  { return health; }
    public int getWave()    { return wave; }
    public Zombie getLocked() { return locked; }
    public boolean isRunning() { return running; }
    public boolean isOver()    { return over; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}
