import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static int t_Assentos[];
    public static int t_Assento;
    public static String nome_arquivo;
    public static estrutura x;

    public static void main(String args[]){

        nome_arquivo = "teste.txt";
        int qtd = Integer.parseInt(args[1]);

        x = new estrutura(qtd);

        thread1 td1 = new thread1(x);

        td1.setName("1");
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

    public static class thread2 extends Thread{
        private estrutura x;

        thread2(estrutura x) { this.x = x;}

        public void run(){
            visualizaAssentos();
        }
    }

    public static void visualizaAssentos() {
        for(int i = 0; i < estrutura.qtd; i++){
            System.out.println("Assento "+i+" com valor: "+t_Assentos[i]);
        }

        String id_thread = Thread.currentThread().getName();
        System.out.println("id da thread é: "+id_thread);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i<estrutura.qtd; i++){
            if(i == estrutura.qtd - 1){
                sb.append(t_Assentos[i]);
            }else {
                sb.append(t_Assentos[i]);
                sb.append(", ");
            }
        }
        sb.append("]");

        String teste = sb.toString();



        //Escreve no buffer (1, id_thread, t_Assentos)
        try(BufferedWriter bw = new BufferedWriter((new FileWriter(nome_arquivo, true)))){
            String content = "1, "+id_thread+", "+teste;
            bw.newLine();
            bw.write(content);
        } catch (IOException e){e.printStackTrace();}
    }

    public static int alocaAssentoDado(int assento, int id){

        if(t_Assentos[assento] == 0){
            t_Assentos[assento] = 1;
            return 1;
        }
        else return 0;
    }
}
