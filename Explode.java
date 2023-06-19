package tancky;

import java.awt.*;

class Explode {
    private int x, y;
    private int step = 0;
    private int[] diameter = {1, 4, 7, 12, 18, 26, 32, 40, 49, 30, 20, 14, 6};

    private boolean live = true;

    private TankClient tankClient;

    Explode(int x, int y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tankClient = tc;
    }

    void draw(Graphics graphics) {
        if (!this.live) {
            tankClient.explodes.remove(this);
            return;
        }

        if (step == diameter.length) {
            live = false;
            step = 0;
            return;
        }

        Color color = graphics.getColor();
        graphics.setColor(Color.ORANGE);
        graphics.fillOval(x, y, diameter[step], diameter[step]);
        graphics.setColor(color);
        step++;
    }
}
