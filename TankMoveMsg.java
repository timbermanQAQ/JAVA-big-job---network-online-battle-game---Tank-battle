package tancky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg {
    private int id;
    private int x;
    private int y;
    private Direction direction;
    private Direction ptDirection;
    private TankClient tankClient;

    TankMoveMsg(int id, int x, int y, Direction direction, Direction ptDirection) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.ptDirection = ptDirection;
    }

    TankMoveMsg(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(Msg.TANK_MOVE_MSG);
            dataOutputStream.writeInt(this.id);
            dataOutputStream.writeInt(x);
            dataOutputStream.writeInt(y);
            dataOutputStream.writeInt(this.direction.ordinal());
            dataOutputStream.writeInt(this.ptDirection.ordinal());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();
        try {
            datagramSocket.send(new DatagramPacket(bytes, bytes.length, new InetSocketAddress(IP, udpPort)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dataInputStream) {
        try {
            int id = dataInputStream.readInt();

            if (tankClient.tank.id == id) {
                return;
            }

            int x = dataInputStream.readInt();
            int y = dataInputStream.readInt();
            Direction direction = Direction.values()[dataInputStream.readInt()];
            Direction barrelDirection = Direction.values()[dataInputStream.readInt()];

            for (int i = 0; i < tankClient.enemyTanks.size(); i++) {
                Tank tank = tankClient.enemyTanks.get(i);
                if (tank.id == id) {
                    tank.tankX = x;
                    tank.tankY = y;
                    tank.direction = direction;
                    tank.barrelDirection = barrelDirection;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
