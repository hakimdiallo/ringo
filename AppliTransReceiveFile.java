import java.io.*;

public class AppliTransReceiveFile {
  private String nom_fichier;
  private String size_nom;
  private String idTrans;
  private File file;
  private int[] ordre_de_reception;
  private int count_mess;
  private int nummess;
  private int offset;

  public AppliTransReceiveFile(String _nom, String _size, File f){
    this.nom_fichier = _nom;
    this.size_nom = _size;
    this.idTrans = UtilsAndController.makeUniqueId();
    this.file = f;
  }

  public int getNumMess(){
    int taille = (int)this.file.length();
    this.nummess = (taille / (512 - 4 - 8 - 3 - 8 - 8 - 3 ))+1;
    return this.nummess;
  }

  public String getIdTrans(){
    return this.idTrans;
  }

  public String[] getMessagesOfFile(){
    try {
      int taille = (int)this.file.length();
      String[] messages = new String[this.nummess];
      FileInputStream fis = new FileInputStream(this.file);
      for (int i=0; i < this.nummess; i++) {
        byte[] b = new byte[taille/this.nummess];
        fis.read(b);
        String content = new String(b);
        String content_size = UtilsAndController.convertSize(String.valueOf(content.length()),3);
        String no_mess = UtilsAndController.convertSize(String.valueOf(i),8);
        String message = Message.APPL.toString()+" "+UtilsAndController.makeUniqueId()+" "+"TRANS###"+" "+"SEN"+" "+this.idTrans+" "+no_mess+" "+content_size+" "+content;
        messages[i] = message;
      }
      return messages;
    }
    catch(Exception e){
      System.out.println("Erreur ");
      e.printStackTrace();
    }
    return null;
  }

  public void setIdTrans(String id){
    this.idTrans = id;
  }

  public void initReception(String num){
    try{
      this.file.createNewFile();
      this.nummess = Integer.parseInt(num);
      this.count_mess = 0;
      this.offset = 0;
      this.ordre_de_reception = new int[this.nummess];
    }
    catch(Exception e){
      System.out.println("Erreur ");
      e.printStackTrace();
    }
  }

  public void receive(String[] tab){
    try{
      if(this.count_mess < this.nummess){
        FileOutputStream fos = new FileOutputStream(this.file);
        this.ordre_de_reception[this.count_mess] = Integer.parseInt(tab[5]);
        this.count_mess++;
        byte[] b = tab[7].getBytes();
        System.out.println("--------------------------------------------Writing into file---------------------------------------------------------------------");
        fos.write(b);
        this.offset += b.length;
        if(this.count_mess == (this.nummess - 1)){
          for (int i=0; i < this.ordre_de_reception.length-1 ; i++) {
            if( this.ordre_de_reception[i] > this.ordre_de_reception[i+1] ){
              this.file.delete();
              System.out.println("paquets du fichier reçu dans le désordre fichier supprimé");
              break;
            }
          }
        }
      }
      /*else{
        System.out.println("OUUUU LA LA LA LA... probleme");
      }*/
    }
    catch(Exception e){
      System.out.println("Erreur ");
      e.printStackTrace();
    }
  }
}
