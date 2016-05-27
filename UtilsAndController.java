import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.UUID;
import java.nio.*;

public abstract class UtilsAndController{

  public static String makeUniqueId(){
    UUID uuid = UUID.randomUUID();
    String id = uuid.toString().substring(0, 8);
    return id;
  }

  public static String convertTo3Octets(String val){
    if( val.length() == 1 ){
      return "00"+val;
    }
    else if( val.length() == 2 )
      return "0"+val;
    else
      return val;
  }

  public static String convertSize(String st, int n){
		String res = "";
		int nb = st.length();
		for (int i = 0; i < n-nb; i++) {
			res+="0";
		}
		res += st;
		return res;
	}

  public static String reConvert(String str){
    int n = str.length();
    for (int i=0; i < n; i++) {
      if(str.charAt(i) != '0'){
        return str.substring(i,str.length());
      }
    }
    return str;
  }

  public static String toLillteEndian(int val){
    byte[] b = ByteBuffer.allocate(8).putInt(val).order(ByteOrder.LITTLE_ENDIAN).array();
    System.out.println(b);
    return (UtilsAndController.convertSize((new String(b)),8));
  }

  public static int toBigEndian(String num){
    num  = UtilsAndController.reConvert(num);
    byte[] b = ByteBuffer.allocate(8).put(num.getBytes()).order(ByteOrder.BIG_ENDIAN).array();
    return Integer.valueOf(new String(b),10);
  }

  //Retourne l'adresse IPV4 de la machine locale
  public static String getMyIp(){
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
  public static boolean idIsOk(String id){
  	if( (id.length() > 8) || (id.length() == 0)) {
  	    System.out.println("Erreur : id "+id+" incorrect");
  	    return false;
  	}
  	return true;
  }

  //Vérifie si une adresse ip est correct
  public static boolean ipIsOk(String _ip){
    String ip = string2addressApp(_ip);
  	Pattern pattern = Pattern.compile("\\d{3}[.]\\d{3}[.]\\d{3}[.]\\d{3}");
  	if(!pattern.matcher(ip).matches()) {
  	    System.out.println("Erreur : format d'adresse ip: "+ip+" incorrect");
  	    return false;
  	}
  	return true;
  }

  //vérifie le format, la taille et l'intervalle dans lequel se trouve un port.
  public static boolean portIsOk(String port){
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

  //Converti une adresse au format d'adresse de l'application (127.0.0.1 to 127.000.000.001)
  public static String string2addressApp(String ip){
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

}
