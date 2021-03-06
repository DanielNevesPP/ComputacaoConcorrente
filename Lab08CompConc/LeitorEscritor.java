// Monitor que implementa a logica do padrao leitores/escritores
class LE {
  private int leit, escr;  
  
  // Construtor
  LE() { 
     this.leit = 0; //leitores lendo (0 ou mais)
     this.escr = 0; //escritor escrevendo (0 ou 1)
  } 
  
  // Entrada para leitores
  public synchronized void EntraLeitor (String thread, int id) {
    try { 
      while (this.escr > 0) {
      //if (this.escr > 0) {
         System.out.println ("le.leitorBloqueado("+thread+": "+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.leit++;  //registra que ha mais um leitor lendo
      System.out.println ("le.leitorLendo("+thread+": "+id+")");
    } catch (InterruptedException e) { }
  }
  
  // Saida para leitores
  public synchronized void SaiLeitor (String thread, int id) {
     this.leit--; //registra que um leitor saiu
     if (this.leit == 0) 
           this.notify(); //libera escritor (caso exista escritor bloqueado)
     System.out.println ("le.leitorSaindo("+thread+": "+id+")");
  }
  
  // Entrada para escritores
  public synchronized void EntraEscritor (String thread, int id) {
    try { 
      while ((this.leit > 0) || (this.escr > 0)) {
      //if ((this.leit > 0) || (this.escr > 0)) {
         System.out.println ("le.escritorBloqueado("+thread+": "+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.escr++; //registra que ha um escritor escrevendo
      System.out.println ("le.escritorEscrevendo("+thread+": "+id+")");
    } catch (InterruptedException e) { }
  }
  
  // Saida para escritores
  public synchronized void SaiEscritor (String thread, int id) {
     this.escr--; //registra que o escritor saiu
     notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
     System.out.println ("le.escritorSaindo("+thread+": "+id+")");
  }
}

//classe da variavel x
class Variavel{
    private int x;

    public synchronized void setX(int x){
        this.x = x;
    }

    public synchronized int getX(){
        return this.x;
    }
}

//thread que acrescenta 1 a variavel x
class T1 extends Thread{

    int id, delay;
    LE monitor;
    Variavel variavel;

    //construtor
    T1(int id, LE m, Variavel v, int delayTime){
        this.id = id;
        this.monitor = m;
        this.variavel = v;
        this.delay = delayTime;
    }

    //Metodo executado pela thread
    public void run(){
        try{
            for(;;){
                this.monitor.EntraEscritor("T1", this.id);
                variavel.setX(variavel.getX() + 1);
                this.monitor.SaiEscritor("T1", this.id);
                sleep(this.delay);
            }  
        }
        catch (InterruptedException e){
            return;
        }   
    }

}

//thread que verifica se a variavel x esta com valor par ou impar
class T2 extends Thread{

    int id, delay;
    LE monitor;
    Variavel variavel;

    //construtor
    T2(int id, LE m, Variavel v, int delayTime){
        this.id = id;
        this.monitor = m;
        this.variavel = v;
        this.delay = delayTime;
    }

    //Metodo executado pela thread
    public void run(){    
        try{  
            for(;;){
                this.monitor.EntraLeitor("t2", this.id);
                if((variavel.getX() % 2)== 0){
                    System.out.println("Variavel com valor par");
                }
                else{
                    System.out.println("Variavel com valor impar");
                }
                this.monitor.SaiLeitor("T2", this.id);
                sleep(this.delay);
            }
        }
        catch (InterruptedException e){
            return;
        } 
    }

}

//thread que imprime a variavel e depois escreve o id da thread nela
class T3 extends Thread{

    int id, delay;
    LE monitor;
    Variavel variavel;

    //construtor
    T3(int id, LE m, Variavel v, int delayTime){
        this.id = id;
        this.monitor = m;
        this.variavel = v;
        this.delay = delayTime;
    }

    //Metodo executado pela thread
    public void run(){
        double j = 777777777.7;
        int i;     
        try{  
            for(;;){
                this.monitor.EntraLeitor("T3", this.id);
                System.out.println("Valor da variavel: " + variavel.getX());
                this.monitor.SaiLeitor("T3", this.id);

                //processamento bobo qualquer
                for (i  = 0; i < 100000000; i++){
                    j = j / 2;
                }

                this.monitor.EntraEscritor("T3", this.id);
                variavel.setX(this.id);
                this.monitor.SaiEscritor("T3", this.id);
                sleep(this.delay);
            }  
        }
        catch (InterruptedException e){
            return;
        }     
    }

}

//classe principal
class LeitorEscritor{

    static final int N1 = 2; //numero de threads T1
    static final int N2 = 2; //numero de threads T2
    static final int N3 = 2; //numero de threads T3

    public static void main(String[] args){

        int i;
        LE monitor = new LE();
        Thread[] threads1 = new Thread[N1];
        Thread[] threads2 = new Thread[N2];
        Thread[] threads3 = new Thread[N3];
        Variavel variavel = new Variavel();

        variavel.setX(0); //inicializa a variavel x compartilhada entre as threads como 0

        for(i = 0; i < N1; i++){
            threads1[i] = new T1(i + 1, monitor, variavel, (i+1)*500);
            threads1[i].start();
        }

        for(i = 0; i < N2; i++){
            threads2[i] = new T2(i + 1, monitor, variavel, (i+1)*500);
            threads2[i].start();
        }

        for(i = 0; i < N3; i++){
            threads3[i] = new T3(i + 1, monitor, variavel, (i+1)*500);
            threads3[i].start();
        }

    }

}
