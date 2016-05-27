import java.net.DatagramSocket;
import java.net.SocketException;


public class TraitementMessage {
	Entite entite;
	
	public TraitementMessage(Entite entite){
		this.entite = entite;
	}
	
	public void traitementUDP(){
		try {
			DatagramSocket dso1= new DatagramSocket(Integer.parseInt(this.entite.getPortUDP1()));
			DatagramSocket dso2= new DatagramSocket(Integer.parseInt(this.entite.getPortUDP2()));
			TraitementMessageUDP tm1 = new TraitementMessageUDP(this.entite, dso1);
			tm1.attendreUDP();
			TraitementMessageUDP tm2 = new TraitementMessageUDP(this.entite, dso2);
			tm2.attendreUDP();
		} catch (NumberFormatException | SocketException e) {
			System.out.println("Erreur : lors de l'attente sur un port UDP");
			e.printStackTrace();
		}
	}
	
	/*private static String idm = "ZDD99998"; //L'identifiant d'un message dans un anneau 
	Entite entite;
	
	public TraitementMessage(Entite entite){
		this.entite = entite;
	}
	
	
	public static String incrementIdm(){
		if(idm.equals("ZDD99999"))
			idm = "ZDD0";
		int n = Integer.parseInt(idm.substring(3,idm.length()))+1;
		idm = "ZDD"+Integer.toString(n);
		return idm;
	}
	
	public static void main(String[] args) {
		System.out.println(incrementIdm());
		System.out.println(incrementIdm());
		System.out.println(incrementIdm());
		System.out.println(incrementIdm());
		System.out.println(incrementIdm());
	}*/
}
