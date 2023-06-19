package tancky;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TankServer {
    private static int tankID = 1;
    static final int UDP_SERVER_PORT = 65432;
    private static final int TCP_SERVER_PORT = 46464;

    private List<Client> clients = new ArrayList<>();

    private void launch() {
        new Thread(new UDPThread()).start();

        try {
            ServerSocket serverSocket = new ServerSocket(TCP_SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                String socketIP = socket.getInetAddress().getHostAddress();
                int socketPort = socket.getPort();
                int socketUdpPort = new DataInputStream(socket.getInputStream()).readInt();
                try {
                    String logString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n" + socketIP + ":" + socketPort + " -> " + socketUdpPort + "\r\n\r\n";
                    if (Files.exists(Paths.get("server.log"))) {
                        Files.write(Paths.get("server.log"), logString.getBytes(), StandardOpenOption.APPEND);
                    } else {
                        Files.createFile(Paths.get("server.log"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                clients.add(new Client(socketIP, socketUdpPort));
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeInt(tankID++);
                dataOutputStream.flush();
                dataOutputStream.close();
                socket.close();

                if (serverSocket.isClosed()) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TankServer().launch();
    }

    private class Client {
        String IP;
        int udpPort;

        Client(String IP, int udpPort) {
            this.IP = IP;
            this.udpPort = udpPort;
        }

        public String toString() {
            return "IP :" + this.IP + " \n Port :" + this.udpPort + "\n";
        }
    }

    private class UDPThread implements Runnable {
        byte[] buffer = new byte[1024];

        public void run() {
            try {
                DatagramSocket datagramSocket = new DatagramSocket(UDP_SERVER_PORT);
                while (true) {
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(datagramPacket);
                    for (Client client : clients) {
                        datagramPacket.setSocketAddress(new InetSocketAddress(client.IP, client.udpPort));
                        datagramSocket.send(datagramPacket);
                    }
                    if (datagramSocket.isClosed()) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
