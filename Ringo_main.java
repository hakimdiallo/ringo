import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;


public class Ringo_main {
	public static void main(String[] args){
		Entite entite = new Entite(args[0],args[1], args[2],args[3], args[4], args[5]);
		System.out.println(entite);
		Insertion ins = new Insertion(entite);
		ins.recInsertion();
		try {
			TraitementMessageUDP tudp = new TraitementMessageUDP(entite, new DatagramSocket());
			TraitementMessage tm = new TraitementMessage(entite);
			tm.traitementUDP();
			Scanner sc = new Scanner(System.in);
			while(true){
				System.out.println(" ======================================================== ");
        System.out.println("|                MANUEL DE COMMANDES                     |");
        System.out.println(" ======================================================== ");
        System.out.println("| Commandes:                                             |");
        System.out.println("|  --> Inserer l'entite: insert <adresse ip> <port>      |");
        System.out.println("|  --> Duplication de l'entite: dupl <adresse ip> <port> |");
				System.out.println("|  --> WHOS: whos                                        |");
				System.out.println("|  --> TEST: test                                        |");
        System.out.println(" ======================================================== ");
				String mess = sc.nextLine();
				if ( !mess.isEmpty() ) {
					String []tab = mess.split(" ");
					if( (tab.length == 3) && (tab[0].equals("insert")) )
						ins.askInsertion(tab[1], tab[2], false);
					else if((tab.length == 3) &&  (tab[0].equals("dupl")) )
						ins.askInsertion(tab[1], tab[2], true);
					else if( (tab[0].equals("whos")) )
						tudp.sendWHOS();
					else if( (tab[0].equals("gbye")) )
						tudp.sendGBYE();
					else if( (tab[0].equals("test")) ){
						tudp.sendTEST(true);
					}
					else if( (tab.length == 2) && (tab[0].equals("trans")) ){
						tudp.sendReq(mess);
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		Commandes cmd = new Commandes(en);
		cmd.executeCom();*/
	}
}
