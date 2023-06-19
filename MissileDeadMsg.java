package tancky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {
    private int ID;
    private int tankId;
    private TankClient tankClient;

    MissileDeadMsg(int tankId, int ID) {
        this.tankId = tankId;
        this.ID = ID;
    }

    MissileDeadMsg(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(MISSILE_DEAD_MSG);
            dataOutputStream.writeInt(tankId);
            dataOutputStream.writeInt(ID);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = byteArrayOutputStream.toByteArray();
        try {
            datagramSocket.send(new DatagramPacket(buffer, buffer.length, new InetSocketAddress(IP, udpPort)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dataInputStream) {
        try {
            int tankId = dataInputStream.readInt();
            int ID = dataInputStream.readInt();

            for (int i = 0; i < tankClient.missiles.size(); i++) {
                Missile missile = tankClient.missiles.get(i);
                if (missile.tankID == tankId && missile.id == ID) {
                    missile.live = false;
                    tankClient.explodes.add(new Explode(missile.x, missile.y, tankClient));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
