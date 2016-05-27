import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Pattern;


public class Entite {
  private String id;
  private String portTCP;
  private String portUDP1;//port d'écoute pour recevoir les messages de l'entité précédente sur l'anneau 1
  private String portUDP2;//port d'écoute pour recevoir les messages de l'entité précédente sur l'anneau 2
  private String ip;
  private Anneau [] anneau;
  LinkedList<String> idmList;
  LinkedList<String> idmApp;
  
  public Entite(String id, String portTCP, String portUDP1, String portUDP2, String portDiff, String ipDiff){
	String ip = this.getMyIp();
	anneau = new Anneau[2];
    if( this.idIsOk(id) && this.portIsOk(portTCP) && this.ipIsOk(this.string2addressApp(ipDiff)) && this.portIsOk(portUDP1)
    		&& this.portIsOk(portUDP2) && this.portIsOk(portDiff) && this.ipIsOk(this.string2addressApp(ip))){
    	this.portUDP1 = portUDP1;
    	this.portUDP2 = portUDP2;
    	this.portTCP = portTCP;
    	this.id = id;
    	this.ip = this.string2addressApp(ip);
    	Anneau anneau = new Anneau(portUDP1, portDiff, this.string2addressApp(ip),
    					this.string2addressApp(ipDiff), new Identifiant(this.id));
    	this.anneau[0] = anneau;
    	this.anneau[1] = null;
    	this.idmList =new LinkedList<String>();
    	this.idmApp = new LinkedList<String>();
    }
  }
  
  //Converti une adresse au format d'adresse de l'application (127.0.0.1 to 127.000.000.001)
  public String string2addressApp(String ip){
	  String []tab = ip.split("[.]");
	  String res = ""; 
	  for (int i=0; i<=3; i++) {
		if(tab[i].length() == 1)
			res += "00"+tab[i];
		if(tab[i].length() == 2)
			res += "0"+tab[i];
		if(tab[i].length() == 3)
			res += tab[i];
		if(i!=3)
			res+=".";
	  }
	  return res;
  }

  //Retourne l'adresse IPV4 de la machine locale
  public String getMyIp(){
	    String ipAddress = "";
	    Enumeration<NetworkInterface> net = null;
	    try {
	        net = NetworkInterface.getNetworkInterfaces();
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }

	    while(net.hasMoreElements()){
	        NetworkInterface element = net.nextElement();
	        Enumeration<InetAddress> addresses = element.getInetAddresses();
	        while (addresses.hasMoreElements()){
	            InetAddress ip = addresses.nextElement();
	            if (ip instanceof Inet4Address){

	                if (ip.isSiteLocalAddress()){

	                    ipAddress = ip.getHostAddress();
	                }

	            }

	        }
	    }
	    return ipAddress;
	}

  //Vérifie si un identifiant est codé sur 8octects 
  public boolean idIsOk(String id){
  	if( (id.length() > 8) || (id.length() == 0)) {
  	    System.out.println("Erreur : id "+id+" incorrect");
  	    return false;
  	}
  	return true;
  }
  
 //Vérifie si une adresse ip est correct 
public boolean ipIsOk(String ip){
	Pattern pattern = Pattern.compile("\\d{3}[.]\\d{3}[.]\\d{3}[.]\\d{3}");
	if(!pattern.matcher(ip).matches()) {
	    System.out.println("Erreur : format d'adresse ip: "+ip+" incorrect");
	    return false;
	}
	return true;
}

//vérifie le format, la taille et l'intervalle dans lequel se trouve un port. 
public boolean portIsOk(String port){
	try{
		int n = Integer.parseInt(port);
		if( (port.length() > 4) || (port.length() == 0)  ){
			System.out.println("Erreur : Port "+n+" n'est pas codé sur 4 octets");
			return false;
		}
		if( n>9999 ){
			System.out.println("Erreur : Port "+n+" n'est pas inférieur à 9999");
			return false;
		}
	}catch(NumberFormatException e){
		System.out.println("Erreur : Port "+port+" n'est pas un entier");
		return false;
	}
	return true;
}

//vérifie le format, la taille et l'intervalle dans lequel se trouve un port. 
public boolean messIsOk(String mess){
	if( mess.length() > 512 ){
		System.out.println("Erreur : Le Message n'est pas codé sur 512 octets");
		return false;
	}
	return true;
}

public String toString(){
	String res = "identifiant : "+this.id+"\nip : "+this.ip+"\nport TCP : "+this.portTCP+"\nport UDP1:"+this.portUDP1+"\nport UDP2:"+this.portUDP2+"\n";
	if(anneau[0]==null)
		res += "Aucun anneau principal\n";
	else
		res += "Premier "+anneau[0].toString();
	if(anneau[1]==null)
		res += "Aucun anneau doubleur\n";
	else
		res += "Deuxième :"+anneau[1].toString();
	return res;
}

//Recupère l'anneau principal
public Anneau getAnneau1() {
	return anneau[0];
}

//Modifie l'anneau doubleur
public void setAnneau1(Anneau anneau) {
	//if( portIsOk(anneau.getPortDiff()) && portIsOk(anneau.getPortNextUDP()) && ipIsOk(anneau.getIpDiff())&& ipIsOk(anneau.getIpNext()) )
		this.anneau[0] = anneau;
}

//Recupère l'anneau doubleur
public Anneau getAnneau2() {
	return anneau[1];
}

//modifie l'anneau doubleur
public void setAnneau2(Anneau anneau) {
	//if( portIsOk(anneau.getPortDiff()) && portIsOk(anneau.getPortNextUDP()) && ipIsOk(anneau.getIpDiff())&& ipIsOk(anneau.getIpNext()) )
		this.anneau[1] = anneau;
}

public void addApp(String st){
	if(this.idIsOk(st))
		this.idmApp.add(st);
}

public String getIp() {
	return ip;
}

public void setIp(String ip) {
	this.ip = ip;
}

public String getPortTCP() {
	return portTCP;
}

public void setPortTCP(String portTCP) {
	this.portTCP = portTCP;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}


public String getPortUDP1() {
	return portUDP1;
}

public void setPortUDP1(String portUDP1) {
	this.portUDP1 = portUDP1;
}

public String getPortUDP2() {
	return portUDP2;
}

public void setPortUDP2(String portUDP2) {
	this.portUDP2 = portUDP2;
}
}
