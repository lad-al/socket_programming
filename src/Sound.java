import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Sound {
	
	private DatagramSocket s;
	private DatagramSocket r;

	public void sound(int cp, int sp, String pi, byte[] h, int n, boolean a) throws IOException {
		
		int clientPort = cp;
		int serverPort = sp;
		String packetInfo = pi;
		byte[] hostIP = h;
		int numOfPackets = n;
		boolean aq = a;
		int aqAddSize = 0;
		int qs = 8;
		if (aq) {
			aqAddSize = 4;
			qs = 16;
		}
		
		Writer differences = null;
		Writer samples = null;
		Writer means = null;
		Writer steps = null;
		AudioFormat audioFormat = new AudioFormat(8000, qs, 1, true, false);
		
		try {
			differences = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("differences.txt"), "utf-8"));
			((BufferedWriter) differences).newLine();
			samples = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("samples.txt"), "utf-8"));
			((BufferedWriter) samples).newLine();
			means = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("means.txt"), "utf-8"));
			((BufferedWriter) means).newLine();
			steps = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("steps.txt"), "utf-8"));
			((BufferedWriter) steps).newLine();
			
			s = new DatagramSocket();
			byte[] txbuffer = packetInfo.getBytes();
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
			
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(4000);
			
			
			byte[] rxbuffer = new byte[128 + aqAddSize];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);	
			s.send(p);
			
			byte[] bytesArray = new byte[numOfPackets*rxbuffer.length*4];
			int s2 = 0;
			int s1 = 0;
			int counter = 0;
			
			
			
			for (int i=0; i<numOfPackets; i++) {
				try {
					r.receive(q);
					if (aq) {
						int mean = (int) (rxbuffer[0] + Math.pow(2.0, 8.0) * rxbuffer[1]);
						int step = (int) (rxbuffer[2] + Math.pow(2.0, 8.0) * rxbuffer[3]);
						means.write(mean + "");
						((BufferedWriter) means).newLine();
						steps.write(step + "");
						((BufferedWriter) steps).newLine();
						for (int j=4; j<132; j++) {
							int byteForCutting = rxbuffer[j];
							
							int firstHalf = (int) (byteForCutting & 0x0000000F) - 8;
							int secondHalf = (int) ((byteForCutting & 0x000000F0)>>4) - 8;
							
							differences.write(firstHalf + "");
							((BufferedWriter) differences).newLine();
							differences.write(secondHalf + "");
							((BufferedWriter) differences).newLine();
							
							s1 = secondHalf*step + mean;
							samples.write(s1 + "");
							((BufferedWriter) samples).newLine();
							
							bytesArray[counter] = (byte) (s1 & 0xFF);
							counter++;
							bytesArray[counter] = (byte) ((s1 >> 8) & 0xFF);
							counter++;
							
							s2 = firstHalf*step + mean;
							samples.write(s2 + "");
							((BufferedWriter) samples).newLine();
							
							bytesArray[counter] = (byte) (s2 & 0xFF);
							counter++;
							bytesArray[counter] = (byte) ((s2 >> 8) & 0xFF);
							counter++;
						}
					}else {
						for (int j=0; j<128; j++) {
							int byteForCutting = rxbuffer[j];
							int firstHalf = (15 & byteForCutting) - 8;
							int secondHalf = ((240 & byteForCutting) >> 4) - 8;
							
							differences.write(firstHalf + "");
							((BufferedWriter) differences).newLine();
							differences.write(secondHalf + "");
							((BufferedWriter) differences).newLine();
							
							s1 = s2 + secondHalf;
							bytesArray[counter] = (byte)s1;
							counter++;
							s2 = s1 + firstHalf;
							bytesArray[counter] = (byte)s2;
							counter++;
							
							samples.write(s1 + "");
							((BufferedWriter) samples).newLine();
							samples.write(s2 + "");
							((BufferedWriter) samples).newLine();
						}
					}
						
					
				} catch (Exception x) {
					 System.out.println(x);
					 break;
				}
			}
			
			System.out.println(bytesArray.length);
			SourceDataLine sdl = AudioSystem.getSourceDataLine(audioFormat);
			sdl.open(audioFormat, numOfPackets*rxbuffer.length*2);
			sdl.start();
			sdl.write(bytesArray, 0, numOfPackets*rxbuffer.length*2);
			
			sdl.close();
			
			
			
		}catch (Exception x) {
			System.out.println(x);
		}finally {
			try {
				samples.close();
				differences.close();
				means.close();
				steps.close();
			} catch (Exception ex) {}
		}
		
	}
}
