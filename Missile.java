package tancky;

import java.awt.*;

class Missile {
    int x, y;
    int tankID;
    int id;

    static final int WIDTH = 10;
    static final int HEIGHT = 10;
    private static int ID = 1;
    private static final int SPEED = 20;

    private boolean good;
    boolean live = true;

    Direction direction;

    private TankClient tankClient;

    boolean isGood() {
        return good;
    }

    private Missile(int tankID, int x, int y, Direction direction) {
        this.tankID = tankID;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.id = ID++;
    }

    Missile(int tankID, int x, int y, boolean good, Direction direction, TankClient tankClient) {
        this(tankID, x, y, direction);
        this.good = good;
        this.tankClient = tankClient;
    }

    void draw(Graphics g) {
        if (!this.live) {
            tankClient.missiles.remove(this);
            return;
        }
        Color c = g.getColor();

        if (this.good) {
            g.setColor(Color.black);
        } else {
            g.setColor(Color.blue);
        }

        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);
        move();
    }

    private void move() {
        switch (direction) {
            case U:
                y -= SPEED;
                break;
            case RU:
                x += SPEED;
                y -= SPEED;
                break;
            case R:
                x += SPEED;
                break;
            case RD:
                x += SPEED;
                y += SPEED;
                break;
            case D:
                y += SPEED;
                break;
            case LD:
                x -= SPEED;
                y += SPEED;
                break;
            case L:
                x -= SPEED;
                break;
            case LU:
                x -= SPEED;
                y -= SPEED;
                break;
            case STOP:
                break;
        }

        if (x < 0 || y < 0 || y > TankClient.GAME_HEIGHT || x > TankClient.GAME_WIDTH) {
            this.live = false;
        }
    }

    private Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    boolean hitTank(Tank tank) {
        if (this.live && this.getRect().intersects(tank.getRect()) && tank.isLive() && this.good != tank.isGood()) {
            tank.setLive();
            this.live = false;
            tankClient.explodes.add(new Explode(x, y, tankClient));
            return true;
        }
        return false;
    }
}
