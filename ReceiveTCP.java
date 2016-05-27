import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;


public class ReceiveTCP extends Thread{
	private Entite entite; 
	
	public ReceiveTCP(Entite entite) {
		this.setEntite(entite);
	}
	
	//Thread pour accepter une connexion tcp
		public void run(){
			try {
				ServerSocket server = new ServerSocket(Integer.parseInt(this.entite.getPortTCP()));
				while(true){
					Socket socket=server.accept();
					System.out.println("Nouvelle connexion TCP avec la machine :"+socket.getInetAddress().getHostAddress());
					BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					//Si l'entité qui reçoit une demande est doubleur 
					if( (entite.getAnneau2()!=null) && (entite.getAnneau1()!=null)){
						pw.println(Message.NOTC);
						pw.flush();
						socket.close();
						System.out.println("Déconnexion avec la machine "+socket.getInetAddress().getHostAddress()+" (entité doubleur)");
						continue;
					}
					/*
					boolean isAnneau1 = false;//vrai si l'anneau1 n'est pas null
					String port;//Pour recupérer le port d'écoute de l'entité sur son anneau qui n'est pas utilisé
					Anneau anneau;
					if(entite.getAnneau1() != null){
						anneau = entite.getAnneau1();
						isAnneau1 = true;
						port = this.entite.getPortUDP2(); //car l'anneau 1 est utilisé 
					}
					else{
						anneau = entite.getAnneau2();
						port = this.entite.getPortUDP1(); //car l'anneau 2 est utilisé
					}*/
					pw.println(Message.WELC+" "+entite.getAnneau1().getIpNext()+" "+entite.getAnneau1().getPortNextUDP()+" "+
							entite.getAnneau1().getIpDiff()+" "+entite.getAnneau1().getPortDiff());
					pw.flush();
					String mess = br.readLine();
					String[] tab = mess.split(" ");
					System.out.println("Message reçu : "+mess);
					//message reçu est "NEWC ip port\n"?
					if( (tab.length == 3) && (tab[0].equals(Message.NEWC.toString())) && this.entite.ipIsOk(tab[1]) 
							&&	this.entite.portIsOk(tab[2]) ){
						System.out.println("Traitement d'insertion ...");
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau principal ...");
						this.entite.getAnneau1().setIpNext(tab[1]);
						this.entite.getAnneau1().setPortNextUDP(tab[2]);
						pw.println(Message.ACKC);
						pw.flush();
					//Message reçu est "DUPL ip port ip-diff port-diff\n"?
					}else if ( (tab.length == 5) && (tab[0].equals(Message.DUPL.toString())) ){
						System.out.println("Traitement de duplication d'anneau ...");
						Anneau an ;
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau secondaire ...");
						an = new Anneau(tab[2],  tab[4], tab[1], tab[3], this.entite.getAnneau1().getIdm());
						this.entite.setAnneau2(an);
						pw.println(Message.ACKD+" "+this.entite.getPortUDP2());
						pw.flush();
					}else{
						System.out.println("Erreur : Message reçu incorrect");
					}
					socket.close();
					System.out.println("Déconnexion avec la machine "+socket.getInetAddress().getHostAddress());
					System.out.println(this.entite);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Erreur : Acceptation de la connexion échouée");
			}
		}

	public Entite getEntite() {
		return entite;
	}

	public void setEntite(Entite entite) {
		this.entite = entite;
	}
}
