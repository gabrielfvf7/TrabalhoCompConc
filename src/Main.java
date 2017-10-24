public class Main {

    public static int t_Assentos[];
    public static int t_Assento;

    public static estrutura x;

    public static void main(String args[]){

        String nome_arquivo = args[0];
        int qtd = Integer.parseInt(args[1]);

        x = new estrutura(qtd);

        thread1 td1 = new thread1(x);

        td1.start();
    }

    public static class estrutura{
        static int qtd;
        int i = 0;

        estrutura(int qtd){
            this.qtd = qtd;
            t_Assentos = new int[qtd];
            for (int i = 0; i < qtd; i++ ){
                t_Assentos[i] = 0;
            }
        }
    }

    public static class thread1 extends Thread{
        private estrutura x;

        thread1(estrutura x){
            this.x = x;
        }

        public void run(){
            visualizaAssentos();
            alocaAssentoDado(2,1);
            visualizaAssentos();
           // liberaAssento();
        }
    }

    public static void visualizaAssentos() {
        for(int i = 0; i < estrutura.qtd; i++){
            System.out.println("Assento "+i+" com valor: "+t_Assentos[i]);
        }
    }

    public static int alocaAssentoDado(int assento, int id){

        if(t_Assentos[assento] == 0){
            t_Assentos[assento] = 1;
            return 1;
        }
        else return 0;
    }
}
