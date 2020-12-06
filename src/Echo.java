import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Echo {

	private DatagramSocket s;
	private DatagramSocket r;

	public void echo(int cp, int sp, String pi, byte[] h, float m, int ts){
		
		int clientPort = cp;
		int serverPort = sp;
		String packetInfo = pi;
		byte[] hostIP = h;
		int throughputSample = ts;
		
		Writer responseTimes = null;
		Writer movingAverage = null;
		Writer srtt = null;
		Writer sigma = null;
		Writer rto = null;

		try {

			responseTimes = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("G1.txt"), "utf-8"));
			movingAverage = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("G2.txt"), "utf-8"));
			srtt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("srtt.txt"), "utf-8"));
			sigma = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("sigma.txt"), "utf-8"));
		    rto = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("rto.txt"), "utf-8"));
			responseTimes.write("Echo response times with request code " + pi + "(in milliseconds)");
		    ((BufferedWriter) responseTimes).newLine();
		    ((BufferedWriter) responseTimes).newLine();
		    movingAverage.write("Echo moving average with request code " + pi + "(in milliseconds, ");
		    ((BufferedWriter) movingAverage).newLine();
		    ((BufferedWriter) movingAverage).newLine();
		    srtt.write("srtt with request code " + pi );
		    ((BufferedWriter) srtt).newLine();
		    ((BufferedWriter) srtt).newLine();
		    sigma.write("sigma with request code " + pi );
		    ((BufferedWriter) sigma).newLine();
		    ((BufferedWriter) sigma).newLine();
		    rto.write("rto with request code " + pi );
		    ((BufferedWriter) rto).newLine();
		    ((BufferedWriter) rto).newLine();

			s = new DatagramSocket();
			byte[] txbuffer = packetInfo.getBytes();
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
			
			r = new DatagramSocket(clientPort);
			r.setSoTimeout(4000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			long loopTime = (long) (m*60000);
			long curTime = 0;
			List<Long> packageTime=new ArrayList<Long>();
			long throughputTime = 0;
			long startOfLoop = System.currentTimeMillis();
			long tempAverage = 0;
			int i=1;
			
			do {
				try {
					while(throughputTime < 1000) {
					
					curTime = System.currentTimeMillis();
					s.send(p);
					r.receive(q);
					String message = new String(rxbuffer, 0, q.getLength());
					System.out.println(message);
					
					packageTime.add(System.currentTimeMillis() - curTime);
					
					responseTimes.write(packageTime.get(packageTime.size()-1) + "");
				    ((BufferedWriter) responseTimes).newLine();
					
					throughputTime += System.currentTimeMillis() - curTime;

					}
					throughputTime = 0;
					
					do{
						tempAverage += packageTime.get(packageTime.size()-i);
						i++;
						if (packageTime.size() < i) {
							break;
						}
					} while (tempAverage < throughputSample*1000);
					
					tempAverage /= i;
					movingAverage.write((tempAverage*0.256) + "");
				    ((BufferedWriter) movingAverage).newLine();
					tempAverage=0;
					i=1;
					
				} catch (Exception x) {
					System.out.println(x);
				}
			}while (curTime <= (loopTime+startOfLoop));
			
			double a = 0.2;
			double b = 0.5;
			double c = 1;
			double d_srtt = 0;
			double d_sigma = 0;
			double d_rto = 0;
			for (int j=0; j<packageTime.size(); j++) {
				d_srtt = a*d_srtt + packageTime.get(j)*(1-a);
				srtt.write(d_srtt + "");
				((BufferedWriter) srtt).newLine();
				d_sigma = b*d_sigma + Math.abs(d_srtt-packageTime.get(j)*(1-b));
				sigma.write(d_sigma + "");
			    ((BufferedWriter) sigma).newLine();
				d_rto = d_srtt + d_sigma * c;
				rto.write(d_rto + "");
				((BufferedWriter) rto).newLine();
			}
		
			((BufferedWriter) responseTimes).newLine();
		    responseTimes.write("Number of packages recieved in " + m + " minutes: " + packageTime.size());
		
		}catch (Exception x) {
			System.out.println(x);
		} finally {
			try {
				responseTimes.close();
				movingAverage.close();
				srtt.close();
				sigma.close();
				rto.close();
			} catch (Exception ex) {}
		}
		
		System.out.println("files created!");
	}		
}
