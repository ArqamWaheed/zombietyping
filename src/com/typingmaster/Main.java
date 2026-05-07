package com.typingmaster;

import com.typingmaster.controller.GameController;
import com.typingmaster.model.Game;
import com.typingmaster.view.GameView;
import com.typingmaster.view.Theme;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// app entry point — boots the MVC trio inside a JFrame
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int W = 1200, H = 700;
            Game game = new Game(W, H);
            GameView view = new GameView(game);
            GameController controller = new GameController(game, view);

            JFrame frame = new JFrame("TypingMaster — Zombie Survival");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.getContentPane().setBackground(Theme.BG_1);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            controller.run();
        });
    }
}
