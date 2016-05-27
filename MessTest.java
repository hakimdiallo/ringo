import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MessTest extends Thread{
	Entite en;
	String idm;
	Anneau anneau;

	public MessTest(Entite en, Anneau anneau, String idm){
		this.en = en;
		this.anneau = anneau;
		this.idm = idm;
	}

	public void run(){
		try {
			DatagramSocket so=new DatagramSocket();
			byte[] data;
			System.out.println("Attente de message envoyé ...");
			Thread.sleep(7000);
			System.out.println("Vérification de la réception du message envoyé ...");
			//si le message envoyé n'a pas été reçu au bout d'un certains temps
			if(!this.en.idmList.contains(idm)){
				data = "DOWN".getBytes();
				DatagramPacket paquet = new DatagramPacket(data,data.length,InetAddress.getByName(this.anneau.getIpDiff()),
								Integer.parseInt(this.anneau.getPortDiff()));
				so.send(paquet);
			}else{
				System.out.println("L'anneau n'est pas cassé !!!");
			}
		}catch(Exception e){
			System.out.println("Erreur : message \"DOWN\" non envoyé");
			e.printStackTrace();
		}
	}
	/*
	public void run(){
  		String idm = this.en.getAnneau1().getIdm().newId();
		//envoie "TEST idm ip-diff port-diff"
		String mess = "Test "+idm+" "+this.en.getAnneau1().getIpDiff()+" "+this.en.getAnneau1().getPortDiff();
		try {
			DatagramSocket so=new DatagramSocket();
			byte[]data;
			data = mess.getBytes();
			DatagramPacket paquet = new
					DatagramPacket(data,data.length,InetAddress.getByName(this.en.getAnneau2().getIpNext()),
					Integer.parseInt(this.en.getAnneau2().getPortNextUDP()));;
			if(isAnneau1){
				paquet = new
							DatagramPacket(data,data.length,InetAddress.getByName(this.en.getAnneau1().getIpNext()),
							Integer.parseInt(this.en.getAnneau1().getPortNextUDP()));
			}
			so.send(paquet);
			so.close();
			System.out.println("Attente de message envoyé ...");
			Thread.sleep(5000);
			System.out.println("Vérification de la réception du message envoyé ...");
			//si le message envoyé n'a pas été reçu au bout d'un certains temps
			if(!this.en.idmList.contains(idm)){
				if(isAnneau1 ){
						data = "DOWN".getBytes();
						paquet = new DatagramPacket(data,data.length,InetAddress.getByName(this.en.getAnneau1().getIpDiff()),
								Integer.parseInt(this.en.getAnneau1().getPortDiff()));
						so.send(paquet);
				}else{
						data = "DOWN".getBytes();
						paquet = new DatagramPacket(data,data.length,InetAddress.getByName(this.en.getAnneau2().getIpDiff()),
								Integer.parseInt(this.en.getAnneau2().getPortDiff()));
						so.send(paquet);
				}
			}
		}catch(Exception e){
			System.out.println("Erreur : message \""+mess+"\" non envoyé");
			e.printStackTrace();
		}
  	}
*/
  	public void sendTEST(){
		this.start();
	}
}
