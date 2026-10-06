package com.typingmaster.controller;

import com.typingmaster.model.Bullet;
import com.typingmaster.model.Game;
import com.typingmaster.view.GameView;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// GameController — wires keyboard input + game loop
public class GameController {
    private final Game game;
    private final GameView view;
    private final Timer timer;
    private long lastNs;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        // ~60 fps
        this.timer = new Timer(1000 / 60, e -> tick());

        view.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { onKey(e); }
            @Override public void keyTyped(KeyEvent e)    { onTyped(e); }
        });
    }

    public void run() {
        view.requestFocusInWindow();
        lastNs = System.nanoTime();
        timer.start();
    }

    private void onKey(KeyEvent e) {
        // ENTER starts a fresh game from the start screen or after game over
        if (e.getKeyCode() == KeyEvent.VK_ENTER && (!game.isRunning() || game.isOver())) {
            game.start();
        }
    }

    private void onTyped(KeyEvent e) {
        if (!game.isRunning() || game.isOver()) return;
        char c = Character.toLowerCase(e.getKeyChar());
        if (c < 'a' || c > 'z') return;
        double[][] hits = game.typeChar(c);
        if (hits != null) {
            for (double[] hit : hits) {
                game.addBullet(new Bullet(70, view.getHeight() / 2.0, hit[0], hit[1]));
            }
        }
    }

    private void tick() {
        long now = System.nanoTime();
        double dt = Math.min(0.05, (now - lastNs) / 1_000_000_000.0);
        lastNs = now;
        game.update(dt);
        view.repaint();
    }
}
