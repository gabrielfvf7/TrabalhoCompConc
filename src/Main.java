import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {

//    private static ConcurrentMap<Integer, Integer> mapaAssentos = new ConcurrentHashMap<>();

    public static int t_Assentos[];
    public static int t_Assento;
    public static String nome_arquivo = "teste.txt";
    public static Estrutura x;

    public static void main(String args[]){

//        mapaAssentos.put(1, 0);
//        mapaAssentos.replace()

        int qtd = Integer.parseInt(args[1]);

        x = new Estrutura(qtd);

        Thread1 td1 = new Thread1(x);

        td1.setName("1");
        td1.start();
    }

    public static class Estrutura{
        static int qtd;
        int i = 0;

        Estrutura(int qtd){
            this.qtd = qtd;
            t_Assentos = new int[qtd];
            for (int i = 0; i < qtd; i++ ){
                t_Assentos[i] = 0;
            }
        }
    }

    public static class Thread1 extends Thread{
        private Estrutura x;

        Thread1(Estrutura x){
            this.x = x;
        }

        public void run(){
            visualizaAssentos();
            alocaAssentoDado(2,1);
            visualizaAssentos();
           // liberaAssento();
        }
    }

    public static class Thread2 extends Thread{
        private Estrutura x;

        Thread2(Estrutura x) { this.x = x;}

        public void run(){
            visualizaAssentos();
        }
    }

    public static void visualizaAssentos() {
        for(int i = 0; i < Estrutura.qtd; i++){
            System.out.println("Assento "+i+" com valor: "+t_Assentos[i]);
        }

        int id_thread = Integer.parseInt(Thread.currentThread().getName());
        String teste = fazString();
        buffer(1,id_thread, 0, teste);
    }

    public static int alocaAssentoDado(int assento, int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            if (t_Assentos[assento] == 0) {
                t_Assentos[assento] = 1;
                System.out.println("Assento reservado!");
                int id_thread = Integer.parseInt(Thread.currentThread().getName());
                String teste = fazString();
                buffer(3,id_thread, assento, teste);
                return 1;
            } else {
                System.out.println("Assento não reservado!");
                return 0;
            }
        } else{
            System.out.println("Thread com id diferente do usuário");
            return 0;
        }
    }

    public static int alocaAssentoLivre(int id){
        return 0;
    }

    public static int liberaAssento(int assento, int id){
        return 0;
    }

    public static String fazString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < Estrutura.qtd; i++){
            if(i == Estrutura.qtd - 1){
                sb.append(t_Assentos[i]);
            }else {
                sb.append(t_Assentos[i]);
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void buffer(int codigo, int id_thread, int assento, String assentos){
        //Escreve no buffer (1, id_thread, t_Assentos)

        if(assento == 0){
            try(BufferedWriter bw = new BufferedWriter((new FileWriter(nome_arquivo, true)))){
                String content = codigo+", "+id_thread+", "+assentos;
                bw.newLine();
                bw.write(content);
            } catch (IOException e){e.printStackTrace();}
        } else {
            try (BufferedWriter bw = new BufferedWriter((new FileWriter(nome_arquivo, true)))) {
                String content = codigo + ", " + id_thread + ", " + assento + ", " + assentos;
                bw.newLine();
                bw.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
