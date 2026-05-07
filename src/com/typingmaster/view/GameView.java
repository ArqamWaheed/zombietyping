package com.typingmaster.view;

import com.typingmaster.model.Bullet;
import com.typingmaster.model.Game;
import com.typingmaster.model.Zombie;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

// GameView — paints the world. Knows nothing about rules.
public class GameView extends JPanel {
    private final Game game;
    private final Font hudFont   = new Font(Font.MONOSPACED, Font.BOLD, 14);
    private final Font wordFont  = new Font(Font.MONOSPACED, Font.BOLD, 18);
    private final Font titleFont = new Font(Font.MONOSPACED, Font.BOLD, 48);
    private final Font bigFont   = new Font(Font.MONOSPACED, Font.BOLD, 64);

    public GameView(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(game.getWidth(), game.getHeight()));
        setBackground(Theme.BG_1);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g);
        drawHud(g);
        drawBase(g);
        drawZombies(g);
        drawBullets(g);

        if (!game.isRunning() && !game.isOver()) drawStartPanel(g);
        if (game.isOver()) drawGameOver(g);

        g.dispose();
    }

    private void drawBackground(Graphics2D g) {
        // grid lines for that retro/terminal feel
        g.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < getWidth(); x += 24)  g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 24) g.drawLine(0, y, getWidth(), y);

        // ground
        int gy = getHeight() - 70;
        g.setColor(new Color(108, 242, 108, 45));
        g.drawLine(0, gy, getWidth(), gy);

        // moon
        g.setColor(new Color(214, 226, 238, 22));
        g.fill(new Ellipse2D.Double(getWidth() - 160, 60, 100, 100));
    }

    private void drawHud(Graphics2D g) {
        // top bar
        g.setColor(Theme.BG_2);
        g.fillRect(0, 0, getWidth(), 56);
        g.setColor(Theme.LINE);
        g.drawLine(0, 56, getWidth(), 56);

        g.setFont(hudFont);
        FontMetrics fm = g.getFontMetrics();

        // left pills
        int x = 20, y = 36;
        x = drawPill(g, x, y, "BASE " + game.getHealth(), Theme.RED, fm);
        x = drawPill(g, x + 8, y, "SCORE " + game.getScore(), Theme.GREEN, fm);

        // center title
        String title = "TYPINGMASTER";
        int tw = fm.stringWidth(title);
        g.setColor(Theme.TEXT);
        g.drawString("TYPING", getWidth()/2 - tw/2, 28);
        g.setColor(Theme.GREEN);
        g.drawString("MASTER", getWidth()/2 - tw/2 + fm.stringWidth("TYPING"), 28);
        g.setColor(Theme.MUTED);
        g.setFont(hudFont.deriveFont(10f));
        FontMetrics smf = g.getFontMetrics();
        String sub = "SURVIVE  THE  HORDE";
        g.drawString(sub, getWidth()/2 - smf.stringWidth(sub)/2, 46);

        // right pills
        g.setFont(hudFont);
        fm = g.getFontMetrics();
        int rx = getWidth() - 20;
        rx = drawPillRight(g, rx, y, "WAVE " + game.getWave(), Theme.AMBER, fm);
        rx = drawPillRight(g, rx - 8, y, "ACC " + game.accuracy() + "%", Theme.PURPLE, fm);
        rx = drawPillRight(g, rx - 8, y, "WPM " + game.wpm(), Theme.GREEN, fm);
    }

    private int drawPill(Graphics2D g, int x, int y, String text, Color dot, FontMetrics fm) {
        int w = fm.stringWidth(text) + 34;
        int h = 24;
        g.setColor(Theme.BG_1);
        g.fill(new RoundRectangle2D.Double(x, y - h + 4, w, h, 999, 999));
        g.setColor(Theme.LINE);
        g.draw(new RoundRectangle2D.Double(x, y - h + 4, w, h, 999, 999));
        g.setColor(dot);
        g.fill(new Ellipse2D.Double(x + 10, y - 9, 7, 7));
        g.setColor(Theme.TEXT);
        g.drawString(text, x + 22, y - 2);
        return x + w;
    }

    private int drawPillRight(Graphics2D g, int xRight, int y, String text, Color dot, FontMetrics fm) {
        int w = fm.stringWidth(text) + 34;
        int x = xRight - w;
        drawPill(g, x, y, text, dot, fm);
        return x;
    }

    // base / tower on the left — drawn from primitives
    private void drawBase(Graphics2D g) {
        int bx = 30, by = getHeight()/2 - 100, bw = 80, bh = 200;

        // body gradient
        java.awt.GradientPaint gp = new java.awt.GradientPaint(
                bx, by, new Color(0x2a3a4a),
                bx, by + bh, new Color(0x0e1620));
        g.setPaint(gp);
        g.fillRect(bx, by, bw, bh);

        g.setColor(Theme.GREEN);
        g.setStroke(new BasicStroke(2));
        g.drawRect(bx, by, bw, bh);

        // top accent
        g.setColor(new Color(108, 242, 108, 60));
        g.fillRect(bx, by, bw, 14);

        // battlements
        g.setColor(Theme.LINE);
        for (int i = 0; i < 4; i++) g.fillRect(bx + i * 16, by - 6, 10, 10);

        // windows
        g.setColor(new Color(0x0c1218));
        g.fillRect(bx + 12, by + 30, 14, 16);
        g.fillRect(bx + 54, by + 30, 14, 16);
        g.fillRect(bx + 12, by + 60, 14, 16);
        g.fillRect(bx + 54, by + 60, 14, 16);

        // door
        g.fillRect(bx + 30, by + 140, 20, 60);
        g.setColor(Theme.GREEN);
        g.drawRect(bx + 30, by + 140, 20, 60);
        g.fill(new Ellipse2D.Double(bx + 46, by + 168, 3, 3));

        // health bar
        int hbx = bx, hby = by - 22, hbw = bw + 60, hbh = 8;
        g.setColor(Theme.LINE);
        g.fillRect(hbx, hby, hbw, hbh);
        double pct = Math.max(0, game.getHealth()) / 100.0;
        g.setColor(pct > 0.5 ? Theme.GREEN : pct > 0.25 ? Theme.AMBER : Theme.RED);
        g.fillRect(hbx, hby, (int)(hbw * pct), hbh);
    }

    private void drawZombies(Graphics2D g) {
        for (Zombie z : game.getZombies()) drawZombie(g, z);
    }

    // hand-drawn zombie out of Java2D shapes
    private void drawZombie(Graphics2D g, Zombie z) {
        Color skin, shadow;
        boolean fast = false, big = false;
        switch (z.getKind()) {
            case "runner" -> { skin = Theme.AMBER;  shadow = new Color(0x3a2a08); fast = true; }
            case "tank"   -> { skin = Theme.PURPLE; shadow = new Color(0x3a2a5a); big = true; }
            default       -> { skin = Theme.GREEN;  shadow = new Color(0x1a3a1f); }
        }

        double cx = z.getX(), cy = z.getY();
        double s  = z.getSize() * 2.0;     // sprite box edge
        double scale = s / 100.0;          // sprite was designed in 100x100 units

        java.awt.geom.AffineTransform old = g.getTransform();
        g.translate(cx - s/2, cy - s/2);
        g.scale(scale, scale);

        // foot shadow
        g.setColor(new Color(0, 0, 0, 110));
        g.fill(new Ellipse2D.Double(28, 91, 44, 6));

        // legs
        g.setColor(shadow);
        g.fillRect(40, 74, 8, 18);
        g.fillRect(52, 74, 8, 18);

        // body
        int bodyH = big ? 30 : 24;
        g.setColor(skin);
        g.fill(new RoundRectangle2D.Double(34, 46, 32, bodyH, 6, 6));
        g.setColor(shadow);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(34, 46, 32, bodyH, 6, 6));

        // torn shirt zigzag
        Path2D.Double zig = new Path2D.Double();
        zig.moveTo(34, 60); zig.lineTo(40, 66); zig.lineTo(46, 60);
        zig.lineTo(52, 66); zig.lineTo(58, 60); zig.lineTo(66, 66);
        g.draw(zig);

        // arms — runners reach forward, others droop
        g.setColor(skin);
        g.setStroke(new BasicStroke(fast ? 6f : 7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (fast) {
            g.drawLine(22, 60, 8, 50);
            g.drawLine(78, 60, 92, 50);
        } else {
            g.drawLine(22, 62, 10, 78);
            g.drawLine(78, 62, 90, 78);
        }

        // head
        g.setColor(skin);
        g.fill(new Ellipse2D.Double(34, 16, 32, 32));
        g.setColor(shadow);
        g.setStroke(new BasicStroke(2f));
        g.draw(new Ellipse2D.Double(34, 16, 32, 32));

        // dead eyes
        g.fill(new Ellipse2D.Double(42, 28, 5, 5));
        g.fill(new Ellipse2D.Double(54, 28, 5, 5));

        // crooked mouth
        Path2D.Double mouth = new Path2D.Double();
        mouth.moveTo(42, 40);
        mouth.quadTo(50, 44, 58, 40);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(mouth);

        // stitches
        g.drawLine(42, 22, 46, 26);
        g.drawLine(50, 20, 54, 24);

        g.setTransform(old);

        drawWordLabel(g, z);
    }

    private void drawWordLabel(Graphics2D g, Zombie z) {
        g.setFont(wordFont);
        FontMetrics fm = g.getFontMetrics();
        String full = z.getWord();
        String typed = z.typedPart();
        String left  = z.remaining();

        int fullW = fm.stringWidth(full);
        int lx = (int) (z.getX() - fullW / 2.0);
        int ly = (int) (z.getY() - z.getSize() - 14);
        int padX = 8, padY = 4;

        // background
        g.setColor(new Color(7, 9, 13, 200));
        g.fillRoundRect(lx - padX, ly - fm.getAscent() - padY, fullW + padX * 2,
                fm.getHeight() + padY * 2, 6, 6);

        boolean lockedHere = game.getLocked() == z;
        g.setColor(lockedHere ? Theme.RED : new Color(108, 242, 108, 90));
        g.setStroke(new BasicStroke(lockedHere ? 2f : 1f));
        g.drawRoundRect(lx - padX, ly - fm.getAscent() - padY, fullW + padX * 2,
                fm.getHeight() + padY * 2, 6, 6);

        // typed (dim) + remaining (bright)
        g.setColor(new Color(0x3a4a5a));
        g.drawString(typed, lx, ly);
        g.setColor(lockedHere ? Theme.RED : Theme.TEXT);
        g.drawString(left, lx + fm.stringWidth(typed), ly);
    }

    private void drawBullets(Graphics2D g) {
        for (Bullet b : game.getBullets()) {
            float a = (float) Math.max(0, Math.min(1, b.getLife() / 0.18));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
            g.setColor(Theme.GREEN);
            g.setStroke(new BasicStroke(2f));
            int sx = 70, sy = getHeight() / 2;
            g.drawLine(sx, sy, (int) b.getX(), (int) b.getY());
            g.fill(new Ellipse2D.Double(b.getX() - 3, b.getY() - 3, 6, 6));
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawStartPanel(Graphics2D g) {
        // dim background
        g.setColor(new Color(7, 9, 13, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        int pw = 480, ph = 320;
        int px = getWidth() / 2 - pw / 2;
        int py = getHeight() / 2 - ph / 2;

        g.setColor(Theme.BG_2);
        g.fill(new RoundRectangle2D.Double(px, py, pw, ph, 14, 14));
        g.setColor(Theme.LINE);
        g.draw(new RoundRectangle2D.Double(px, py, pw, ph, 14, 14));

        g.setFont(titleFont);
        FontMetrics fm = g.getFontMetrics();
        String t = "TYPINGMASTER";
        int tw = fm.stringWidth(t);
        g.setColor(Theme.TEXT);
        g.drawString("TYPING", px + pw/2 - tw/2, py + 70);
        g.setColor(Theme.GREEN);
        g.drawString("MASTER", px + pw/2 - tw/2 + fm.stringWidth("TYPING"), py + 70);

        g.setFont(hudFont);
        g.setColor(Theme.MUTED);
        String desc = "type the words above the zombies before they reach your base";
        FontMetrics sm = g.getFontMetrics();
        g.drawString(desc, px + pw/2 - sm.stringWidth(desc)/2, py + 100);

        // legend
        int lx = px + 50, ly = py + 140;
        drawLegend(g, lx, ly,      Theme.AMBER,  "RUNNER  fast,  short  words");
        drawLegend(g, lx, ly + 28, Theme.GREEN,  "WALKER  average  pace");
        drawLegend(g, lx, ly + 56, Theme.PURPLE, "TANK    slow,  long  words");

        // start hint
        g.setColor(Theme.GREEN);
        g.setFont(hudFont.deriveFont(16f));
        String hint = "PRESS  ENTER  TO  START";
        FontMetrics hf = g.getFontMetrics();
        g.drawString(hint, px + pw/2 - hf.stringWidth(hint)/2, py + ph - 50);

        g.setColor(Theme.MUTED);
        g.setFont(hudFont.deriveFont(11f));
        String h2 = "first matching letter locks a target";
        FontMetrics h2f = g.getFontMetrics();
        g.drawString(h2, px + pw/2 - h2f.stringWidth(h2)/2, py + ph - 28);
    }

    private void drawLegend(Graphics2D g, int x, int y, Color chip, String label) {
        g.setColor(chip);
        g.fillRoundRect(x, y - 12, 16, 16, 4, 4);
        g.setColor(Theme.MUTED);
        g.setFont(hudFont);
        g.drawString(label, x + 28, y);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(7, 9, 13, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(bigFont);
        FontMetrics fm = g.getFontMetrics();
        String t = "GAME OVER";
        g.setColor(Theme.RED);
        g.drawString(t, getWidth()/2 - fm.stringWidth(t)/2, getHeight()/2);

        g.setFont(hudFont);
        FontMetrics sm = g.getFontMetrics();
        g.setColor(Theme.TEXT);
        String s = "score " + game.getScore() + "  ·  wpm " + game.wpm() + "  ·  acc " + game.accuracy() + "%";
        g.drawString(s, getWidth()/2 - sm.stringWidth(s)/2, getHeight()/2 + 30);

        g.setColor(Theme.MUTED);
        String r = "press ENTER to restart";
        g.drawString(r, getWidth()/2 - sm.stringWidth(r)/2, getHeight()/2 + 56);
    }
}
