import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.*;


public class TraitementMessageUDP extends Thread{
	DatagramSocket dso;
	Entite entite;
	AppliTransReceiveFile app;

	public TraitementMessageUDP(Entite entite, DatagramSocket dso){
		this.entite = entite;
		this.dso = dso;
	}

	public void run(){
		byte[]data=new byte[512];
		DatagramPacket paquet=new DatagramPacket(data,data.length);
		try {
			while(true){
				this.dso.receive(paquet);
				String mess=new
				String(paquet.getData(),0,paquet.getLength());
				System.out.println("Message reçu :"+mess);
				//On traite le message s' il vient d'un anneau
				if ( ((this.entite.getAnneau1()!=null) && (Integer.parseInt(this.entite.getPortUDP1())==dso.getLocalPort()))
						|| ((this.entite.getAnneau2()!=null) && (Integer.parseInt(this.entite.getPortUDP2())==dso.getLocalPort()))) {
					this.recWHOS(mess);
					this.recMEMB(mess);
					this.recGBYE(mess);
					this.recEYBG(mess);
					this.recTEST(mess);
					this.recAPPL(mess);
					this.recDIFFforClient(mess);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void attendreUDP(){
		this.start();
	}

	public void closeUDP(){
		this.dso.close();
	}

	public void recWHOS(String mess){
		String[] tab = mess.split(" ");
		//Si message est "WHOS idm"
		if( (tab.length == 2) && (tab[0].equals(Message.WHOS.toString())) && this.entite.idIsOk(tab[1]) ){
			//Si l'id du mess n'est pas dans la liste d'idm de l'entité
			this.transMess(mess);
			this.sendMEMB();
		}
	}

	public void recMEMB(String mess){
		String [] tab = mess.split(" ");
		//Si message est "MEMB idm id ip port"
		if( (tab.length == 5) && (tab[0].equals(Message.MEMB.toString())) && this.entite.idIsOk(tab[1]) &&
				this.entite.idIsOk(tab[2]) && this.entite.ipIsOk(tab[3]) && this.entite.portIsOk(tab[4])){
			transMess(mess);
		}
	}

	public void recGBYE(String mess){
		String [] tab = mess.split(" ");
		//Si message est "GBYE idm ip port ip-succ port-succ" (messsage de déconnexion
		if( (tab.length == 6) && (tab[0].equals(Message.GBYE.toString())) && this.entite.idIsOk(tab[1]) && this.entite.ipIsOk(tab[2])
				&& this.entite.portIsOk(tab[3]) && this.entite.ipIsOk(tab[4]) && this.entite.portIsOk(tab[5].split("\\n")[0]) ){
			//l'id du mess n'est pas dans la liste d'idm de l'entité

			if(!this.entite.idmList.contains(tab[1])){
				this.entite.idmList.add(tab[1]);
				//Si l'anneau existe
				if(entite.getAnneau1() != null){
					//si l'entité est le prédécesseur de l'entité souhaitant sortie de l'anneau
					if( (entite.getAnneau1().getIpNext().equals(tab[2])) && (entite.getAnneau1().getPortNextUDP().equals(tab[3])) ){
						String idm = UtilsAndController.makeUniqueId();
						//envoie "EYBG idm"
						mess = Message.EYBG.toString()+" "+idm;
						this.send(mess, this.entite.getAnneau1());
						entite.getAnneau1().setIpNext(tab[4]);
						entite.getAnneau1().setPortNextUDP(tab[5]);
					}else{
						this.send(mess, this.entite.getAnneau1());
					}
				}
				//Si l'anneau existe
				if(entite.getAnneau2() != null){
					//si l'entité est le prédécesseur de l'entité souhaitant sortie de l'anneau
					if( (entite.getAnneau2().getIpNext().equals(tab[2])) && (entite.getAnneau2().getPortNextUDP().equals(tab[3])) ){
						String idm = UtilsAndController.makeUniqueId();
						//envoie "EYBG idm"
						mess = Message.EYBG.toString()+" "+idm;
						this.send(mess, this.entite.getAnneau2());
						entite.getAnneau2().setIpNext(tab[4]);
						entite.getAnneau2().setPortNextUDP(tab[5]);
					}else{
						this.send(mess, this.entite.getAnneau2());
					}
				}
			}
		}
	}

	public void recEYBG(String mess){
		String [] tab = mess.split(" ");
		//Si le message est "EYBG"
		if( (tab.length == 2) && (tab[0].equals(Message.EYBG.toString())) && (this.entite.idIsOk(tab[1])) ){
			//
			if(this.dso.getLocalPort()==Integer.parseInt(this.entite.getPortUDP1()) ){
				this.entite.setAnneau1(null);
			}
			else{
				this.entite.setAnneau2(null);
			}
		}
	}

	public void recTEST(String mess){
		String [] tab = mess.split(" ");
		//Si le message est "TEST idm ip-diff port-diff"
		if( (tab.length == 4) && (tab[0].equals(Message.TEST.toString())) && (this.entite.idIsOk(tab[1])) &&
				(this.entite.ipIsOk(tab[2])) && (this.entite.portIsOk(tab[3])) ){
			//Si l'id du mess n'est pas dans la liste d'idm de l'entité
			if(!this.entite.idmList.contains(tab[1])){
				this.entite.idmList.add(tab[1]);
				if(this.dso.getLocalPort()==Integer.parseInt(this.entite.getPortUDP1()) ){
					this.send(mess, this.entite.getAnneau1());
				}else{
					this.send(mess, this.entite.getAnneau2());
				}
			}
		}
	}

	public void recAPPL(String mess){
		String [] tab = mess.split(" ");
		//Si le message est "APPL idm id-app message-app"
		if( (tab.length >= 4) && (tab[0].equals(Message.APPL.toString())) && (this.entite.idIsOk(tab[1])) &&
				(this.entite.idIsOk(tab[2])) ){
			//Si l'id du mess n'est pas dans la liste d'idm de l'entité
			if(!this.entite.idmList.contains(tab[1])){
				//Si un anneau existe
				if( (entite.getAnneau1() != null) || (entite.getAnneau2() != null)  ){
					this.recDIFF(mess);
					this.recTRANS(mess);
				}
			}
		}
	}

	public void send(String mess, Anneau anneau){
		try {
			DatagramSocket so=new DatagramSocket();
			byte[]data;
			data=mess.getBytes();
			DatagramPacket paquet=new
						DatagramPacket(data,data.length,InetAddress.getByName(anneau.getIpNext()),
						Integer.parseInt(anneau.getPortNextUDP()));
			so.send(paquet);
			so.close();
		}catch(Exception e){
			System.out.println("Erreur : message \""+mess+"\" non envoyé");
			e.printStackTrace();
		}
	}

	public void sendPortDIFF(String mess, Anneau anneau){
		try {
			DatagramSocket so=new DatagramSocket();
			byte[]data;
			data=mess.getBytes();
			DatagramPacket paquet=new
						DatagramPacket(data,data.length,InetAddress.getByName(anneau.getIpDiff()),
						Integer.parseInt(anneau.getPortDiff()));
			so.send(paquet);
			so.close();
		}catch(Exception e){
			System.out.println("Erreur : message \""+mess+"\" non envoyé");
			e.printStackTrace();
		}
	}

	public void sendWHOS(){
		String idm = UtilsAndController.makeUniqueId();
		this.entite.idmList.add(idm);
		if( entite.getAnneau1() != null )
			this.send("WHOS "+idm, this.entite.getAnneau1());
		if( entite.getAnneau2() != null )
			this.send("WHOS "+idm, this.entite.getAnneau2());
	}

	public void sendMEMB(){
		String idm = UtilsAndController.makeUniqueId();
		this.entite.idmList.add(idm);
		String mess = Message.MEMB.toString()+" "+idm+" "+this.entite.getId()+" "+this.entite.getIp()+" "+this.entite.getPortUDP1();
		if( entite.getAnneau1() != null )
			this.send(mess, this.entite.getAnneau1());
		if( entite.getAnneau2() != null )
			this.send(mess, this.entite.getAnneau2());
	}

	public void sendGBYE(){
		String idm = UtilsAndController.makeUniqueId();
		if( entite.getAnneau1() != null )
			this.send(Message.GBYE.toString()+" "+idm+" "+this.entite.getIp()+" "+this.entite.getPortUDP1()+" "+this.entite.getAnneau1().getIpNext()+
					" "+this.entite.getAnneau1().getPortNextUDP(), this.entite.getAnneau1());
		if( entite.getAnneau2() != null )
			this.send(Message.GBYE.toString()+" "+idm+" "+this.entite.getIp()+" "+this.entite.getPortUDP2()+" "+this.entite.getAnneau2().getIpNext()+
					" "+this.entite.getAnneau2().getPortNextUDP(), this.entite.getAnneau2());
	}

	public void sendTEST(boolean isAnneau1){
		MessTest messTest;
		if(isAnneau1){
			if( (entite.getAnneau1() != null) ){
				String idm = UtilsAndController.makeUniqueId();
				//envoie "TEST idm ip-diff port-diff"
				String mess = Message.TEST+" "+idm+" "+this.entite.getAnneau1().getIpDiff()+" "+this.entite.getAnneau1().getPortDiff();
				this.send(mess, this.entite.getAnneau1());
				messTest = new MessTest(this.entite, this.entite.getAnneau1(), idm);
				messTest.start();
			}
		}else{
			if( (entite.getAnneau2() != null) ){
				String idm = UtilsAndController.makeUniqueId();
				//envoie "TEST idm ip-diff port-diff"
				String mess = "Test "+idm+" "+this.entite.getAnneau2().getIpDiff()+" "+this.entite.getAnneau2().getPortDiff();
				this.send(mess, this.entite.getAnneau2());
				messTest = new MessTest(this.entite, this.entite.getAnneau2(), idm);
				messTest.start();
			}
		}

	}

	public void transMess(String mess){
		String[] tab = mess.split(" ");
		if(!this.entite.idmList.contains(tab[1])){
			this.entite.idmList.add(tab[1]);
			//Si l'anneau existe
			if( entite.getAnneau1() != null ){
				this.send(mess, this.entite.getAnneau1());
			}
			//Si l'anneau existe
			if( entite.getAnneau2() != null ){
				this.send(mess, this.entite.getAnneau2());
			}
		}
	}

	public void recDIFF(String mess){
		String[] tab = mess.split(" ");
		//On traite l'application de diffusion de messages
		if( (tab.length >=5) && (tab[3].length() ==3) && (tab[2].equals("DIFF####")) ){//
			try{
					Integer.parseInt(tab[3]);
				}catch (NumberFormatException e){
					System.out.println("Erreur : La taille du message reçu n'est pas un entier");
				}
				this.transMess(mess);
		}
	}

	public void sendTCHAT(String mess){
		String[] tab = mess.split("::::");
		String idm = UtilsAndController.makeUniqueId();
		this.entite.idmList.add(idm);
		String size = this.convertSize(Integer.toString(tab[1].length()-1), 3);
		String mess1 = Message.APPL.toString()+" "+idm+" TCHAT#### "+size+" "+tab[1]+"\tauteur:"+this.entite.getId();
		if( entite.getAnneau1() != null )
			this.sendPortDIFF(mess1, this.entite.getAnneau1());
		if( entite.getAnneau2() != null )
			this.sendPortDIFF(mess1, this.entite.getAnneau2());
	}

	public String convertSize(String st, int n){
		String res = "";
		int nb = st.length();
		for (int i = 0; i < n-nb; i++) {
			res+="0";
		}
		res += st;
		return res;
	}

	public void recDIFFforClient(String mess){
		String[] tab = mess.split("::::");
		if( (tab.length==2) && (tab[0].equals("DIFF"))){
			String idm = UtilsAndController.makeUniqueId();
			this.entite.idmList.add(idm);
			String size = this.convertSize(Integer.toString(tab[1].length()-1), 3);
			mess = Message.APPL.toString()+" "+idm+"DIFF####"+size+" "+tab[1];
			if( entite.getAnneau1() != null )
				this.send(mess, this.entite.getAnneau1());
			if( entite.getAnneau2() != null )
				this.send(mess, this.entite.getAnneau2());
		}
	}

	public void sendDIFF(String mess){
		String[] tab = mess.split("::::");
		String idm = UtilsAndController.makeUniqueId();
		this.entite.idmList.add(idm);
		String size = this.convertSize(Integer.toString(tab[1].length()-1), 3);
		String mess1 = Message.APPL.toString()+" "+idm+" DIFF#### "+size+" "+tab[1];
		if( entite.getAnneau1() != null )
			this.send(mess1, this.entite.getAnneau1());
		if( entite.getAnneau2() != null )
			this.send(mess1, this.entite.getAnneau2());
	}

	public void recTRANS(String mess){
		String[] tab = mess.split(" ");
		//On traite l'application de diffusion de messages
		if( (tab.length == 6) && (tab[4].length() ==2) && (tab[2].equals("TRANS###")) && (tab[3].equals("REQ")) ){
			try{
				int size = Integer.parseInt(tab[4]);
				//Si l'entité a le fichier
					//on transfère le fichier
				File fichier = new File(tab[5]);
				if( fichier.exists() && !fichier.isDirectory() ){
					this.app = new AppliTransReceiveFile(tab[5],tab[4], fichier);
					int nummess = app.getNumMess();
					String numero = UtilsAndController.convertSize(String.valueOf(nummess),8);
					String message = Message.APPL.toString()+" "+UtilsAndController.makeUniqueId()+" "+"TRANS###"+" "+"ROK"+" "+app.getIdTrans()+" "+tab[4]+" "+tab[5]+" "+numero;
					this.transMess(message);
					String[] messages = app.getMessagesOfFile();
					for (int i=0; i < messages.length ; i++) {
						this.transMess(messages[i]);
					}
				}
				else{
					System.out.println("Je n'ai pas le fichier je tranfere le message "+mess);
					this.transMess(mess);
				}

			}catch (NumberFormatException e){
				System.out.println("Erreur : La taille du fichier reçu "+tab[4]+" n'est pas un entier");
				e.printStackTrace();
			}
		}
		else if( (tab.length >= 8) &&
							(tab[1].length() == 8) &&
							(tab[2].length() == 8) &&
							(tab[3].length() == 3) &&
							(tab[4].length() == 8) &&
							(tab[2].equals("TRANS###"))	 ) {
			if ( tab[3].equals("ROK") ) {
				File f = new File(tab[6]);
				this.app = new AppliTransReceiveFile(tab[6],tab[5],f);
				this.app.setIdTrans(tab[4]);
				this.app.initReception(tab[7]);
			}
			else if(tab[3].equals("SEN")){
				if( this.app.getIdTrans().equals(tab[4]) ){
					this.app.receive(tab);
				}
				else{
					this.transMess(mess);
				}
			}
			else{
				System.out.println("Message de transfert de ficher de mauvais format");
			}
		}
		else{
			System.out.println("Message de mauvais format");
		}
	}

	public void sendReq(String mess){
		String [] tab = mess.split("::::");
		String idm = UtilsAndController.makeUniqueId();
		this.entite.idmList.add(idm);
		String size_nom = this.convertSize(Integer.toString(tab[1].length()),2);
		String message = Message.APPL.toString()+" "+idm+" TRANS### REQ "+size_nom+" "+tab[1];
		if(this.entite.getAnneau1() != null)
			this.send(message,this.entite.getAnneau1());
		if(this.entite.getAnneau2() != null)
			this.send(message,this.entite.getAnneau2());
	}

}
