import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Vehicle {
	
	private DatagramSocket s;
	private DatagramSocket r;

	public void vehicle(int cp, int sp, String pi, byte[] h, float m){
		
		int clientPort = cp;
		int serverPort = sp;
		String packetInfo = pi;
		byte[] hostIP = h;
		String message;
		String[] pid = {"1F", "0F", "11", "0C", "0C", "05"};
		
		long loopTime = (long) (m*60000);
		long startOfLoop = System.currentTimeMillis();
		long curTime = 0;
		Writer engineRunTime = null;
		Writer intakeAirTemp = null;
		Writer throttlePosition = null;
		Writer engineRPM = null;
		Writer vehicleSpeed = null;
		Writer coolantTemperature = null;

		try {
			engineRunTime = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("engineRunTime.txt"), "utf-8"));
			engineRunTime.write("engineRunTime with request code " + pi);
			intakeAirTemp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("intakeAirTemp.txt"), "utf-8"));
			intakeAirTemp.write("intakeAirTemp with request code " + pi);
			throttlePosition = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("throttlePosition.txt"), "utf-8"));
			throttlePosition.write("throttlePosition with request code " + pi);
			engineRPM = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("engineRPM.txt"), "utf-8"));
			engineRPM.write("engineRPM with request code " + pi);
			vehicleSpeed = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("vehicleSpeed.txt"), "utf-8"));
			vehicleSpeed.write("vehicleSpeed with request code " + pi);
			coolantTemperature = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("coolantTemperature.txt"), "utf-8"));
			coolantTemperature.write("coolantTemperature with request code " + pi);
			
			s = new DatagramSocket();
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(10000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			do {
				curTime = System.currentTimeMillis();
				for (int i = 0; i<6; i++) {
					byte[] txbuffer = (packetInfo+pid[i]).getBytes();

					DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
					s.send(p);
					r.receive(q);
					message = new String(rxbuffer,0,q.getLength());
					System.out.println(message);
					
					switch(i) {
						case 0:
							String xx0 = new String(rxbuffer,6,2);
							String yy0 = new String(rxbuffer,9,2);
							int ixx0 = Integer.parseInt(xx0, 16);
							int iyy0 = Integer.parseInt(yy0, 16);
						    ((BufferedWriter) engineRunTime).newLine();
						    engineRunTime.write(256*ixx0+iyy0 + "");
						    break;
						    
						case 1:
							String xx1 = new String(rxbuffer,6,2);
							int ixx1 = Integer.parseInt(xx1, 16);
							((BufferedWriter) intakeAirTemp).newLine();
							intakeAirTemp.write((ixx1-40) + "");
							break;
							
						case 2:
							String xx2 = new String(rxbuffer,6,2);
							int ixx2 = Integer.parseInt(xx2, 16);
							((BufferedWriter) throttlePosition).newLine();
							throttlePosition.write((ixx2*100/255) + "");
							break;
							
						case 3:
							String xx3 = new String(rxbuffer,6,2);
							String yy3 = new String(rxbuffer,9,2);
							int ixx3 = Integer.parseInt(xx3, 16);
							int iyy3 = Integer.parseInt(yy3, 16);
						    ((BufferedWriter) engineRPM).newLine();
						    engineRPM.write(((ixx3*256)+iyy3)/4 + "");
						    break;
							
						case 4:
							String xx4 = new String(rxbuffer,6,2);
							int ixx4 = Integer.parseInt(xx4, 16);
							((BufferedWriter) vehicleSpeed).newLine();
							vehicleSpeed.write(ixx4 + "");
							break;
							
						case 5:
							String xx5 = new String(rxbuffer,6,2);
							int ixx5 = Integer.parseInt(xx5, 16);
							((BufferedWriter) coolantTemperature).newLine();
							coolantTemperature.write((ixx5-40) + "");
							break;
					}
				}
			}while (curTime <= (loopTime+startOfLoop));
		}catch (Exception x) {
			System.out.println(x);
		} finally {
			try {
				engineRunTime.close();
				intakeAirTemp.close();
				throttlePosition.close();
				engineRPM.close();
				vehicleSpeed.close();
				coolantTemperature.close();
			} catch (Exception ex) {}
		}
	}
}
