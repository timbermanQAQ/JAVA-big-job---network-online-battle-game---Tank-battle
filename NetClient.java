package tancky;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetClient {
    private int udpPort;
    private TankClient tankClient;
    private String IP;

    void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    private DatagramSocket datagramSocket;

    NetClient(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    boolean connect(String IP, int port) {
        this.IP = IP;
        try {
            datagramSocket = new DatagramSocket(udpPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Socket socket = null;
        try {
            socket = new Socket(IP, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(this.udpPort);

            int readInt = dataInputStream.readInt();
            this.tankClient.tank.id = readInt;

            if (readInt % 2 == 0) {
                tankClient.tank.setGood(false);
            } else {
                tankClient.tank.setGood(true);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        send(new TankNewMsg(tankClient.tank));
        new Thread(new UDPReceiveThread()).start();
        return true;
    }


    public void send(Msg msg) {
        msg.send(datagramSocket, IP, TankServer.UDP_SERVER_PORT);
    }

    private class UDPReceiveThread implements Runnable {
        byte[] buffer = new byte[1024];

        public void run() {
            while (datagramSocket != null) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    parse(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket datagramPacket) {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buffer, 0, datagramPacket.getLength()));
            try {
                int msgType = dataInputStream.readInt();
                Msg msg;
                switch (msgType) {
                    case Msg.TANK_NEW_MSG:
                        msg = new TankNewMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.TANK_MOVE_MSG:
                        msg = new TankMoveMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.MISSILE_NEW_MSG:
                        msg = new MissileNewMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.TANK_DEAD_MSG:
                        msg = new TankDeadMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.MISSILE_DEAD_MSG:
                        msg = new MissileDeadMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
