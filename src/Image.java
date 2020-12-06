import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Image {
	
	private DatagramSocket s;
	private DatagramSocket r;

	public void image(int cp, int sp, String pi, byte[] h, String f) throws IOException {
		
		int clientPort = cp;
		int serverPort = sp;
		String packetInfo = pi;
		byte[] hostIP = h;
		String filename = f;
		ArrayList<Byte> bytesList = new ArrayList<Byte>(); 
		
		try {
			s = new DatagramSocket();
			byte[] txbuffer = packetInfo.getBytes();
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
			
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(2000);
			byte[] rxbuffer = new byte[1024];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			s.send(p);

			for (;;) {
				try {
					r.receive(q);
					for (int i=0; i<rxbuffer.length; i++) {
						bytesList.add(rxbuffer[i]);
						if ((bytesList.get(bytesList.size()-1) == (byte)0xD8) && (bytesList.get(bytesList.size()-2) == (byte)0xFF)) {
							System.out.println("Let the image begin");
							bytesList.clear();
							bytesList.add((byte)0xFF);
							bytesList.add((byte)0xD8);
						}else if((bytesList.get(bytesList.size()-1) == (byte)0xD9) && (bytesList.get(bytesList.size()-2) == (byte)0xFF)) {
							System.out.println("Let's get outa here");
							break;
						}
					}
					if((bytesList.get(bytesList.size()-1) == (byte)0xD9) && (bytesList.get(bytesList.size()-2) == (byte)0xFF)) {
						System.out.println("One more step");
						break;
					}
				} catch (Exception x) {
					 System.out.println(x);
					 break;
				}
			}
		}catch (Exception x) {
			System.out.println(x);
		}
		
		byte[] arrayOfBytes = new byte[bytesList.size()];
		for (int i=0; i<bytesList.size(); i++) {
			arrayOfBytes[i] = bytesList.get(i);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(arrayOfBytes);
		BufferedImage bImage = ImageIO.read(bis);
		ImageIO.write(bImage, "jpg", new File(filename) );
		System.out.println(filename + " created");

	}
}
