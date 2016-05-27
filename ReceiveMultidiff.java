import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class ReceiveMultidiff {
	Entite entite;
	
	public ReceiveMultidiff(Entite entite){
		this.entite = entite;
	}
	
	public void recTEST(){
		try{
			MulticastSocket mso=new MulticastSocket( Integer.parseInt(this.entite.getAnneau1().getPortDiff()) );
			mso.joinGroup(InetAddress.getByName(this.entite.getAnneau1().getIpDiff()));
			byte[]data=new byte[512];
			DatagramPacket paquet=new DatagramPacket(data,data.length);
			while(true){
				mso.receive(paquet);
				String mess=new String(paquet.getData(),0,paquet.getLength());
				System.out.println("Message de multidiffusion reçu :"+mess);
				if(mess.equals("DOWN")){
					//on quite le programme principal
					System.exit(0);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
