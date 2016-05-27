import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */

/**
 * @author jules
 *
 */
public class Test {
	public  static void main(String[] args){
		/*Entite en = new Entite(args[0],args[1], args[2],args[3], args[4], args[5]);
		Insertion ins = new Insertion(en);
		boolean isDuplication = false;
		if(args.length == 9)
			isDuplication = true;
		ins.askInsertion(args[6], args[7], isDuplication);
		try {
			DatagramSocket dso1 = new DatagramSocket(Integer.parseInt(en.getPortUDP1()));
			DatagramSocket dso2 = new DatagramSocket(Integer.parseInt(en.getPortUDP2()));
			TraitementMessageUDP tm1 = new TraitementMessageUDP(en, dso1);
			TraitementMessageUDP tm2 = new TraitementMessageUDP(en, dso2);
			tm1.start();
			tm2.start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*try {
			Socket sock = new Socket("192.168.001.099", 4458);
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		try {
			File f = new File("test.txt");
			FileInputStream fis = new FileInputStream(f);
			String hello = "12";
			byte[] b = ByteBuffer.allocate(8).put(hello.getBytes()).order(ByteOrder.LITTLE_ENDIAN).array();
			//fis.read(b);
			String chaine = new String(b);
			int chaine2 = ByteBuffer.wrap(chaine.getBytes()).order(ByteOrder.BIG_ENDIAN).getInt();
			//int chaine2 = Integer.parseInt(new String(b),8);
			System.out.println("size "+f.length()+" "+chaine+"-"+(int)(chaine2)+"bbbb");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
