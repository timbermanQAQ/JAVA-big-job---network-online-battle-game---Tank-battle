package tancky;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
	int TANK_NEW_MSG = 1 ;
	int TANK_MOVE_MSG = 2 ;
	int MISSILE_NEW_MSG = 3 ;
	int TANK_DEAD_MSG = 4 ;
	int MISSILE_DEAD_MSG = 5 ;
	
	void send(DatagramSocket datagramSocket, String IP, int udpPort);
	void parse(DataInputStream dataInputStream);
}
