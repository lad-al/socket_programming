import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Temperature {

	private DatagramSocket s;
	private DatagramSocket r;

	public void temperature(int cp, int sp, String pi, byte[] h){
		
		int clientPort = cp;
		int serverPort = sp;
		String packetInfo = pi;
		byte[] hostIP = h;
		
		Writer temperatures = null;
		
		try {
			
			temperatures = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temperatures.txt"), "utf-8"));

			temperatures.write("temperatures with request code " + pi);
		    ((BufferedWriter) temperatures).newLine();
		    ((BufferedWriter) temperatures).newLine();
			
			s = new DatagramSocket();
			
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(4000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			try {
				for (int i=0; i<10; i++) {
					byte[] txbuffer = (packetInfo + " T0" + String.valueOf(i)).getBytes();
					System.out.println((String)(packetInfo + " T0" + String.valueOf(i)));
					DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
					
					s.send(p);
					r.receive(q);
					String message = new String(rxbuffer, 0, q.getLength());
					System.out.println(message);
					
					if(q.getLength() > 40) {
						temperatures.write(new String(rxbuffer, 27, 21));
						((BufferedWriter) temperatures).newLine();
					}
				}
				for (int i=10; i<99; i++) {
					byte[] txbuffer = ((packetInfo + " T" + String.valueOf(i))).getBytes();
					System.out.println(packetInfo + " T" + String.valueOf(i));
					DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
					
					s.send(p);
					r.receive(q);
					String message = new String(rxbuffer, 0, q.getLength());
					System.out.println(message);
					
					if(q.getLength() > 40) {
						temperatures.write(new String(rxbuffer, 27, 21));
						((BufferedWriter) temperatures).newLine();
					}
				}
			} catch (Exception x) {
				System.out.println(x);
			}
		}catch (Exception x) {
			System.out.println(x);
		} finally {
			try {
				temperatures.close();
			} catch (Exception ex) {}
		}
		
		System.out.println("files created!");
	}
}
