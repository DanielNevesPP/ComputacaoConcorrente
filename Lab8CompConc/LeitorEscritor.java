// Monitor que implementa a logica do padrao leitores/escritores
class LE {
  private int leit, escr;  
  
  // Construtor
  LE() { 
     this.leit = 0; //leitores lendo (0 ou mais)
     this.escr = 0; //escritor escrevendo (0 ou 1)
  } 
  
  // Entrada para leitores
  public synchronized void EntraLeitor (int id) {
    try { 
      while (this.escr > 0) {
      //if (this.escr > 0) {
         System.out.println ("le.leitorBloqueado("+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.leit++;  //registra que ha mais um leitor lendo
      System.out.println ("le.leitorLendo("+id+")");
    } catch (InterruptedException e) { }
  }
  
  // Saida para leitores
  public synchronized void SaiLeitor (int id) {
     this.leit--; //registra que um leitor saiu
     if (this.leit == 0) 
           this.notify(); //libera escritor (caso exista escritor bloqueado)
     System.out.println ("le.leitorSaindo("+id+")");
  }
  
  // Entrada para escritores
  public synchronized void EntraEscritor (int id) {
    try { 
      while ((this.leit > 0) || (this.escr > 0)) {
      //if ((this.leit > 0) || (this.escr > 0)) {
         System.out.println ("le.escritorBloqueado("+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.escr++; //registra que ha um escritor escrevendo
      System.out.println ("le.escritorEscrevendo("+id+")");
    } catch (InterruptedException e) { }
  }
  
  // Saida para escritores
  public synchronized void SaiEscritor (int id) {
     this.escr--; //registra que o escritor saiu
     notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
     System.out.println ("le.escritorSaindo("+id+")");
  }
}

//thread que acrescenta 1 a variavel x
class T1 extends Thread{

    int id;
    LE monitor;

    T1(int id, LE m){
        this.id = id;
        this.monitor = m;
    }

    public synchronized void run(){
        try{
            this.monitor.EntraEscritor(this.id);
            LeitorEscritor.x++;
            this.monitor.SaiEscritor(this.id);
        }
        catch InterruptedException e{
            return;
        }
    }

}

//thread que verifica se a variavel x esta com valor par ou impar
class T2 extends Thread{

    int id;
    LE monitor;

    T2(int id, LE m){
        this.id = id;
        this.monitor = m;
    }

    public void run(){
        try{
            this.monitor.EntraLeitor(this.id);
            if(x % 2 == 0){
                System.out.println("Variavel com valor par");
            }
            else{
                System.out.println("Variavel com valor impar");
            }
            this.monitor.SaiLeitor(this.id);
        }
    }

}

class T3 extends Thread{

    int id;
    LE monitor;

}

class LeitorEscritor{

    static final int N1 = 1; //numero de threads T1
    static final int N2 = 1; //numero de threads T2
    static final int N3 = 1; //numero de threads T3

    public int x = 0;
}