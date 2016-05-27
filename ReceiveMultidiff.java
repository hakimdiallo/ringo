import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;


public class ReceiveMultidiff extends Thread{
	Entite entite;

	public ReceiveMultidiff(Entite entite){
		this.entite = entite;
	}

	public void run(){
		try{
			int portDiff = Integer.parseInt(this.entite.getAnneau1().getPortDiff());
			String ipDiff = this.entite.getAnneau1().getIpDiff();
			MulticastSocket mso=new MulticastSocket( portDiff );
			mso.joinGroup(InetAddress.getByName(ipDiff));
			byte[]data=new byte[512];
			DatagramPacket paquet=new DatagramPacket(data,data.length);
			while(true){
				/*RecDOWN recd = new RecDOWN(mso);
				recd.start();*/
				mso.receive(paquet);
				String mess=new String(paquet.getData(),0,paquet.getLength());
				if( (portDiff != Integer.parseInt(this.entite.getAnneau1().getPortDiff()) || (!ipDiff.equals(this.entite.getAnneau1().getIpDiff()))))
						break;
				System.out.println("Message reçu :"+mess);
				if(mess.equals("DOWN")){
					//on quite le programme principal
					System.out.println("Au revoir!!");
					System.exit(0);
				}
			}
			mso.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void attendreMultidiff(){
		this.start();
	}
}
