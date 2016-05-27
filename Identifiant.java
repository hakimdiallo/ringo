import java.util.concurrent.atomic.AtomicInteger;


public class Identifiant {
	private String pref;
    private  static AtomicInteger compte =  new AtomicInteger(0) ;
   
    public Identifiant(String pref){
    	this.pref = pref;
    }
    
    public  String newId() {
    	compte.compareAndSet(10000, 0);
    	return pref+Integer.toString(compte.getAndIncrement()) ;
   }
    /*
    public static void main(String[] args) {
		Identifiant id = new Identifiant("zdd");
		Test1[] t = new Test1[5];
		for (int i = 0; i < t.length; i++) {
			t[i] = new Test1(id);
		}
		for (int i = 0; i < t.length; i++) {
			t[i].start();
		}
		for (int i = 0; i < t.length; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}

