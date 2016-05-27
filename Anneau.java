import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;




public class Anneau extends Thread{
  	private String portNextUDP; //port d'écoute  UDP de l'entité suivante sur l'anneau
  	private String portDiff; //Port de multidiffusion
  	private String ipNext; //Adresse IP de la machine suivante sur l'anneau
  	private String ipDiff; //Adresse IPV4 de multi-diffusion
  	public Anneau(String portNextUDP, String portDiff, String ipNext, String ipDiff){
  		this.portDiff = portDiff;
  		this.portNextUDP = portNextUDP;
  		this.ipNext = ipNext;
  		this.ipDiff = ipDiff;
  	}



	public String toString(){
		String res = "Anneau :\n\tip next : "+this.ipNext+"\n\tip diff : "+this.ipDiff+
				"\n\tport UDP next : "+this.portNextUDP+"\n\tport diff : "+this.portDiff+"\n";
		return res;
	}


	public String getPortNextUDP() {
		return portNextUDP;
	}

	public void setPortNextUDP(String portNextUDP) {
		this.portNextUDP = portNextUDP;
	}

	public String getPortDiff() {
		return portDiff;
	}

	public void setPortDiff(String portDiff) {
		this.portDiff = portDiff;
	}

	public String getIpNext() {
		return ipNext;
	}

	public void setIpNext(String ipNext) {
		this.ipNext = ipNext;
	}

	public String getIpDiff() {
		return ipDiff;
	}

	public void setIpDiff(String ipDiff) {
		this.ipDiff = ipDiff;
	}

}
