package tancky;

import java.awt.*;
import java.awt.event.KeyEvent;

class Tank {
    int tankX;
    int tankY;
    int id;

    private boolean buttonUP = false;
    private boolean buttonDown = false;
    private boolean buttonLeft = false;
    private boolean buttonRight = false;
    private boolean isLive = true;
    private boolean isGood;

    private static final int TANK_WIDTH = 30;
    private static final int TANK_HEIGHT = 30;
    private static final int SPEED = 15;
    private static final int UP_LIMIT = 16;

    Direction direction;
    Direction barrelDirection = Direction.D;

    private TankClient tankClient;

    boolean isGood() {
        return isGood;
    }

    void setGood(boolean good) {
        this.isGood = good;
    }

    boolean isLive() {
        return isLive;
    }

    void setLive() {
        this.isLive = false;
    }

    private Tank(int x, int y, boolean isGood) {
        this.tankX = x;
        this.tankY = y;
        this.isGood = isGood;
    }

    Tank(int x, int y, boolean isGood, Direction direction, TankClient tankClient) {
        this(x, y, isGood);
        this.direction = direction;
        this.tankClient = tankClient;
    }

    private boolean checkEdge(int x, int y) {
        return x >= 0 && x <= (TankClient.GAME_WIDTH - TANK_WIDTH)
                && y >= UP_LIMIT && y <= TankClient.GAME_HEIGHT - TANK_HEIGHT;
    }

    private void move() {
        int x, y;
        switch (direction) {

            case U:
                y = tankY - SPEED;
                if (checkEdge(tankX, y)) {
                    tankY = y;
                }
                break;
            case RU:
                x = tankX + SPEED;
                y = tankY - SPEED;
                if (checkEdge(x, y)) {
                    tankX = x;
                    tankY = y;
                }
                break;
            case R:
                x = tankX + SPEED;
                if (checkEdge(x, tankY)) {
                    tankX = x;
                }
                break;
            case RD:
                x = tankX + SPEED;
                y = tankY + SPEED;
                if (checkEdge(x, y)) {
                    tankX = x;
                    tankY = y;
                }
                break;
            case D:
                y = tankY + SPEED;
                if (checkEdge(tankX, y)) {
                    tankY = y;
                }
                break;
            case LD:
                x = tankX - SPEED;
                y = tankY + SPEED;
                if (checkEdge(x, y)) {
                    tankX = x;
                    tankY = y;
                }
                break;
            case L:
                x = tankX - SPEED;
                if (checkEdge(x, tankY)) {
                    tankX = x;
                }
                break;
            case LU:
                x = tankX - SPEED;
                y = tankY - SPEED;
                if (checkEdge(x, y)) {
                    tankX = x;
                    tankY = y;
                }
                break;
            case STOP:
                break;
        }
        if (this.direction != Direction.STOP) {
            this.barrelDirection = this.direction;
        }
    }

    private void location() {
        Direction oldDirection = this.direction;

        if (buttonUP && !buttonDown && !buttonLeft && !buttonRight) {
            direction = Direction.U;
        } else if (buttonUP && !buttonDown && !buttonLeft) {
            direction = Direction.RU;
        } else if (!buttonUP && !buttonDown && !buttonLeft && buttonRight) {
            direction = Direction.R;
        } else if (!buttonUP && buttonDown && !buttonLeft && buttonRight) {
            direction = Direction.RD;
        } else if (!buttonUP && buttonDown && !buttonLeft) {
            direction = Direction.D;
        } else if (!buttonUP && buttonDown && !buttonRight) {
            direction = Direction.LD;
        } else if (!buttonUP && !buttonDown && buttonLeft && !buttonRight) {
            direction = Direction.L;
        } else if (buttonUP && !buttonDown && !buttonRight) {
            direction = Direction.LU;
        } else if (!buttonUP && !buttonDown && !buttonLeft) {
            direction = Direction.STOP;
        }
        if (this.direction != oldDirection) {
            TankMoveMsg msg = new TankMoveMsg(id, this.tankX, this.tankY, direction, this.barrelDirection);
            tankClient.netClient.send(msg);
        }
    }

    void draw(Graphics graphics) {
        if (!this.isLive) {
            if (!this.isGood) {
                tankClient.enemyTanks.remove(this);
            }
            return;
        }
        Color color = graphics.getColor();
        if (this.isGood) {
            graphics.setColor(new Color(231, 76, 60));
        } else {
            graphics.setColor(new Color(26, 186, 154));
        }

        graphics.fillRect(this.tankX, this.tankY, TANK_WIDTH, Tank.TANK_HEIGHT);
        graphics.drawString("ID : " + this.id, this.tankX, this.tankY - 10);
        graphics.setColor(Color.black);

        int x1 = this.tankX + TANK_WIDTH / 2;
        int y1 = this.tankY + TANK_HEIGHT / 2;
        int x2 = countX2();
        int y2 = countY2();

        graphics.drawLine(x1, y1, x2, y2);
        graphics.setColor(color);
        move();
    }

    private int countX2() {
        double x = 0;
        switch (barrelDirection) {
            case U:
                x = this.tankX + TANK_WIDTH / 2;
                break;
            case RU:
                x = this.tankX + TANK_WIDTH * 1.5;
                break;
            case R:
                x = this.tankX + TANK_WIDTH * 1.5;
                break;
            case RD:
                x = this.tankX + TANK_WIDTH * 1.5;
                break;
            case D:
                x = this.tankX + TANK_WIDTH / 2;
                break;
            case LD:
                x = this.tankX - TANK_WIDTH / 2;
                break;
            case L:
                x = this.tankX - TANK_WIDTH / 2;
                break;
            case LU:
                x = this.tankX - TANK_WIDTH / 2;
                break;
            default:
                break;
        }
        return (int) x;
    }

    private int countY2() {
        double y = 0;
        switch (barrelDirection) {
            case U:
                y = this.tankY - TANK_HEIGHT / 2;
                break;
            case RU:
                y = this.tankY - TANK_HEIGHT / 2;
                break;
            case R:
                y = this.tankY + TANK_HEIGHT / 2;
                break;
            case RD:
                y = this.tankY + TANK_HEIGHT * 1.5;
                break;
            case D:
                y = this.tankY + TANK_HEIGHT * 1.5;
                break;
            case LD:
                y = this.tankY + TANK_HEIGHT * 1.5;
                break;
            case L:
                y = this.tankY + TANK_WIDTH / 2;
                break;
            case LU:
                y = this.tankY - TANK_HEIGHT / 2;
                break;
            default:
                break;
        }
        return (int) y;
    }

    void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        switch (key) {
            case KeyEvent.VK_W:
                buttonUP = true;
                break;
            case KeyEvent.VK_S:
                buttonDown = true;
                break;
            case KeyEvent.VK_A:
                buttonLeft = true;
                break;
            case KeyEvent.VK_D:
                buttonRight = true;
                break;
        }
        location();
    }

    private void fire() {
        if (!this.isLive) {
            return;
        }

        int x = this.tankX + Tank.TANK_WIDTH / 2 - Missile.WIDTH / 2;
        int y = this.tankY + Tank.TANK_HEIGHT / 2 - Missile.HEIGHT / 2;

        Missile missile = new Missile(id, x, y, isGood, this.barrelDirection, this.tankClient);
        tankClient.missiles.add(missile);
        tankClient.netClient.send(new MissileNewMsg(missile));
    }

    void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_J:
                fire();
                break;
            case KeyEvent.VK_W:
                buttonUP = false;
                break;
            case KeyEvent.VK_S:
                buttonDown = false;
                break;
            case KeyEvent.VK_A:
                buttonLeft = false;
                break;
            case KeyEvent.VK_D:
                buttonRight = false;
                break;
        }
        location();
    }

    Rectangle getRect() {
        return new Rectangle(tankX, tankY, TANK_WIDTH, TANK_HEIGHT);
    }
}
