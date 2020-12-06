import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class IthakiCopter {

	private DatagramSocket r;

	public void ithakiCopter(){
		
		int clientPort = 48078;
		Writer LRMotor = null;
		Writer Altitude = null;
		
		try {
			LRMotor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("LR.txt"), "utf-8"));
			Altitude = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Altitude.txt"), "utf-8"));
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(4000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			String message;
			String lrmotor;
			String altitude;

			for (int i=0; i<150; i++) {
				try {
					r.receive(q);
					message = new String(rxbuffer,0,q.getLength());
					lrmotor = new String(rxbuffer,40,3);
					altitude = new String(rxbuffer,64,3);
					System.out.println(message);
					LRMotor.write(lrmotor + "");
				    ((BufferedWriter) LRMotor).newLine();
				    Altitude.write(altitude + "");
				    ((BufferedWriter) Altitude).newLine();
				    
				} catch (Exception x) {
					System.out.println(x);
				}
			}
		}catch (Exception x) {
			 System.out.println(x);
		} finally {
			try {
				LRMotor.close();
				Altitude.close();
			} catch (Exception ex) {}
		}
	}
}
