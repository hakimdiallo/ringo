import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;


public class Insertion extends Thread{
	private Entite entite;
	public Insertion(Entite entite){
		this.entite = entite;
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
				}
				pw.println(Message.WELC+" "+anneau.getIpNext()+" "+anneau.getPortNextUDP()+" "+
							anneau.getIpDiff()+" "+anneau.getPortDiff());
				pw.flush();
				String mess = br.readLine();
				String[] tab = mess.split(" ");
				System.out.println("Message reçu : "+mess);
				//message reçu est "NEWC ip port\n"?
				if( (tab.length == 3) && (tab[0].equals(Message.NEWC.toString())) && this.entite.ipIsOk(tab[1])
						&&	this.entite.portIsOk(tab[2]) ){
					System.out.println("Traitement d'insertion ...");
					if(isAnneau1){
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau principal ...");
						this.entite.getAnneau1().setIpNext(tab[1]);
						this.entite.getAnneau1().setPortNextUDP(tab[2]);
					}else{
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau secondaire ...");
						this.entite.getAnneau2().setIpNext(tab[1]);
						this.entite.getAnneau2().setPortNextUDP(tab[2]);
					}
					pw.println(Message.ACKC);
					pw.flush();
				//Message reçu est "DUPL ip port ip-diff port-diff\n"?
				}else if ( (tab.length == 5) && (tab[0].equals(Message.DUPL.toString())) ){
					System.out.println("Traitement de duplication d'anneau ...");
					Anneau an ;
					if(!isAnneau1){
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau principal ...");
						an = new Anneau(tab[2],  tab[4], tab[1], tab[3]);
						this.entite.setAnneau1(an);
					}else{
						System.out.println("Modification de l'entité "+this.entite.getId()+" Dans son anneau secondaire ...");
						an = new Anneau(tab[2],  tab[4], tab[1], tab[3]);
						this.entite.setAnneau2(an);
					}
					pw.println(Message.ACKD+" "+port);
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

	public void recInsertion(){
		this.start();
	}

	//Demande d'insertion dans un anneau
	public void askInsertion(String ip, String port, boolean isDuplication){
		try {
			if( !this.entite.ipIsOk(ip) || !this.entite.portIsOk(port))
				return ;
			Socket socket=new Socket(ip,Integer.parseInt(port));
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			String mess = br.readLine();
			System.out.println("Message reçu : "+mess);
			String []tab = mess.split(" ");
			boolean isAnneau1 = true; //Vérifie si l'anneau1  est utilisé
			Anneau anneau ;//L'anneau de l'entité courante
			if( (this.entite.getAnneau1() != null) && (this.entite.getAnneau2() != null)){
				System.out.println("Erreur : L'entité d'id "+this.entite.getId()+" est doubleur");
				return ;
			}else if( this.entite.getAnneau1() != null ){
				anneau = this.entite.getAnneau1();
			}else{
				anneau = this.entite.getAnneau1();
				isAnneau1 = false;//L'anneau 2 est utilisé
			}
			if(tab.length==1 && tab[0].equals(Message.NOTC.toString())){
				System.out.println("L'entité est doubleur");
			//Vérifie si le message reçu est "WELC ip port ip-diff port-diff"
			}else if( (tab.length==5) && tab[0].equals(Message.WELC.toString()) && this.entite.ipIsOk(tab[1]) && this.entite.portIsOk(tab[2]) && this.entite.ipIsOk(tab[3]) &&
					this.entite.portIsOk(tab[4]) ){
				//C'est une insertion dans un anneau
				if(!isDuplication){
					String portUDP = this.entite.getPortUDP2();
					if(isAnneau1)
						portUDP = this.entite.getPortUDP1();
					pw.println(Message.NEWC+" "+this.entite.getIp()+" "+portUDP);
					pw.flush();
					mess = br.readLine();
					System.out.println("Message reçu : "+mess);
					//On vérifie que le message est "ACKC\n" avant d'entrer dans l'anneau
					if(mess.equals(Message.ACKC.toString())){
						anneau.setIpNext(tab[1]);
						anneau.setPortNextUDP(tab[2]);
						anneau.setIpDiff(tab[3]);
						anneau.setPortDiff(tab[4]);
						if(isAnneau1)
							this.entite.setAnneau1(anneau);
						else
							this.entite.setAnneau2(anneau);
						System.out.println("Insertion dans l'anneau réussie avec la machine :"+socket.getInetAddress().getHostAddress()+" !!");
						ReceiveMultidiff reMul= new ReceiveMultidiff(this.entite);
						reMul.attendreMultidiff();
					}else
						System.out.println("Erreur : Message reçu incorrect");
				}else{//C'est une duplication
					pw.println(Message.DUPL+" "+anneau.getIpNext()+" "+anneau.getPortNextUDP()+" "+
							anneau.getIpDiff()+" "+anneau.getPortDiff());
					pw.flush();
					mess = br.readLine();
					tab = mess.split(" ");
					System.out.println("Message reçu : "+mess);
					if( tab[0].equals(Message.ACKD.toString()) && this.entite.portIsOk(tab[1]) ){
						anneau.setPortNextUDP(tab[1]);
						if(isAnneau1)
							this.entite.setAnneau1(anneau);
						else
							this.entite.setAnneau2(anneau);
						System.out.println("Duplication réussie avec la machine "+socket.getInetAddress().getHostAddress()+" !!");
					}else{
						System.out.println("Erreur : Message reçu incorrect");
					}
				}
			}else{
				System.out.println("Erreur : le message reçu est incorrect!");
			}
			System.out.println(this.entite);
			socket.close();
		} catch (Exception e) {
			System.out.println("Erreur : demande d'insertion ou de duplication a échouée");
			e.printStackTrace();
		}
	}

}
