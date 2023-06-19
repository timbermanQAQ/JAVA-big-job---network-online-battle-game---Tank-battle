package tancky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Msg {
    private Missile missile;
    private TankClient tankClient;

    MissileNewMsg(Missile missile) {
        this.missile = missile;
    }

    MissileNewMsg(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(MISSILE_NEW_MSG);
            dataOutputStream.writeInt(missile.tankID);
            dataOutputStream.writeInt(missile.id);
            dataOutputStream.writeInt(missile.x);
            dataOutputStream.writeInt(missile.y);
            dataOutputStream.writeInt(this.missile.direction.ordinal());
            dataOutputStream.writeBoolean(missile.isGood());
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            datagramSocket.send(new DatagramPacket(byteArray, byteArray.length, new InetSocketAddress(IP, udpPort)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dataInputStream) {
        try {
            int tankId = dataInputStream.readInt();
            int ID = dataInputStream.readInt();
            if (tankId == tankClient.tank.id) {
                return;
            }

            int x = dataInputStream.readInt();
            int y = dataInputStream.readInt();
            Direction direction = Direction.values()[dataInputStream.readInt()];
            boolean isGood = dataInputStream.readBoolean();
            Missile missile = new Missile(tankId, x, y, isGood, direction, tankClient);
            missile.id = ID;
            tankClient.missiles.add(missile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
