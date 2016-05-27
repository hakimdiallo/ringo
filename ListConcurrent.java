import java.util.LinkedList;


public class ListConcurrent {
	private LinkedList<String> liste;

	public ListConcurrent(){
		liste = new LinkedList<String>();
	}
	
	public synchronized void add(String s){
		liste.add(s);
	}
	
	public LinkedList<String> getListe() {
		return liste;
	}

	public void setListe(LinkedList<String> liste) {
		this.liste = liste;
	}
}
