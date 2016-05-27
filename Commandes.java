import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;


public class Commandes extends Thread{
	Entite entite;
	public Commandes(Entite entite){
		this.entite = entite;
	}
	
	public void run(){
		Scanner sc = new Scanner(System.in);
		Insertion ins = new Insertion(this.entite);
		DatagramSocket dso1;
		try {
			dso1 = new DatagramSocket();
			TraitementMessageUDP tm1 = new TraitementMessageUDP(this.entite, dso1);;
			while(true){
				String commande = sc.nextLine();
				String[] tab = commande.split(" ");
				if( (tab.length ==  3) && (tab[0].equals("insert")) ){
					ins.askInsertion(tab[1], tab[2], false);
				}else if( (tab.length ==  2) && (tab[0].equals("send")) ){
					if(tab[1].equals("whos")){
						tm1.sendWHOS();
					}
				}else if ( (tab.length == 6)&&(tab[0].equals("GBYE")) ) {
					tm1.sendGBYE();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void executeCom(){
		this.start();
	}
}
