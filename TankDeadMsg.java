package tancky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg {
    private int ID;
    private TankClient tankClient;

    TankDeadMsg(int ID) {
        this.ID = ID;
    }

    TankDeadMsg(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(Msg.TANK_DEAD_MSG);
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
            int ID = dataInputStream.readInt();

            if (tankClient.tank.id == ID) {
                return;
            }

            for (int i = 0; i < tankClient.enemyTanks.size(); i++) {
                Tank tank = tankClient.enemyTanks.get(i);
                if (tank.id == ID) {
                    tank.setLive();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
