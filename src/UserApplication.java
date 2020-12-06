import java.io.IOException;
import java.util.Scanner;

public class UserApplication {
	
	public static void main(String[] args) throws IOException{
		
		System.out.println("Welcome");
		
		byte[] serverPublicAdress = {(byte)155, (byte)207, (byte)18, (byte)208};
		int clientListeningPort = 48021;
		int serverListeningPort = 38021;
		String echoRequestCode = "E6582";
		String imageRequestCode = "M9729 UDP=1024 CAM=";
		String soundRequestCode = "A3117 F400";
		String vehicleRequestCode = "V6484 OBD=01 ";
		
		try (Scanner myObj = new Scanner(System.in)) {
			System.out.println("Select application:");
			System.out.println("1: Echo");
			System.out.println("2: Temperature");
			System.out.println("3: Image");
			System.out.println("4: Sound");
			System.out.println("5: Helicopter");
			System.out.println("6: Vehicle");
			String select = myObj.nextLine();
			
			switch (select){
			
			case "1":
				System.out.println("Loop time:(in minutes)");
				float loopTime = Float.parseFloat(myObj.nextLine());
				System.out.println("Throughput sample:(8/16/32)");
				int throughputSample = Integer.parseInt(myObj.nextLine());
				Echo Echo = new Echo();
				Echo.echo(clientListeningPort, serverListeningPort, echoRequestCode, serverPublicAdress, loopTime, throughputSample);
				break;
				
			case "2":
				Temperature Temperature = new Temperature();
				Temperature.temperature(clientListeningPort, serverListeningPort, echoRequestCode, serverPublicAdress);
				break;
				
			case "3":
				System.out.println("Select Cam:(FIX/PTZ)");
				imageRequestCode += myObj.nextLine();
				Image Image = new Image();
				Image.image(clientListeningPort, serverListeningPort, imageRequestCode, serverPublicAdress, "image.jpg");
				break;
				
			case "4":
				System.out.println("Select aq:(true/false)");
				boolean aq = Boolean.parseBoolean(myObj.nextLine());
				if (aq) {
					soundRequestCode = "A3117 AQ F400";
				}
				Sound Sound = new Sound();
				Sound.sound(clientListeningPort, serverListeningPort, soundRequestCode, serverPublicAdress, 800, aq);
				break;
				
			case "5":
				IthakiCopter IthakiCopter = new IthakiCopter();
				IthakiCopter.ithakiCopter();
				break;
				
			case "6":
				System.out.println("Loop time:(in minutes)");
				float vehicleLoopTime2 = Float.parseFloat(myObj.nextLine());
				Vehicle Vehicle = new Vehicle();
				Vehicle.vehicle(clientListeningPort, serverListeningPort, vehicleRequestCode, serverPublicAdress, 2*vehicleLoopTime2);
				break;
				
			default:
				System.out.println("Invalid Input");
	
			}
		}
	}
}