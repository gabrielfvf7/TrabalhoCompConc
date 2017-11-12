import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {

    public static ConcurrentMap<Integer, Integer> t_Assentos = new ConcurrentHashMap<>();
    public static Pair<Integer, Integer> t_Assento;

    public static String nome_arquivo = "teste.txt";

    public static void main(String args[]){

        nome_arquivo = args[0];

        int qtd = Integer.parseInt(args[1]);

        // Inicialização dos assentos com 0 - Livre
        // Numeracao do assento 1 até o assento = qtd
        for(int i = 1; i <= qtd; i++){
            t_Assentos.put(i, 0);
        }

        Thread1 td1 = new Thread1();

        td1.setName("1");
        td1.start();
    }

    public static class Thread1 extends Thread{
        public void run(){
            visualizaAssentos();
            alocaAssentoDado(2, 1);
            visualizaAssentos();
           // liberaAssento();
        }
    }

    public static class Thread2 extends Thread{
        public void run(){
            visualizaAssentos();
        }
    }

    public static void visualizaAssentos() {
        String listaAssentos = fazString();

        System.out.println(listaAssentos);

        int id_thread = Integer.parseInt(Thread.currentThread().getName());
        String teste = fazString();
        buffer(1,id_thread, 0, teste);
    }

    public static int alocaAssentoDado(int assento, int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            boolean reservado = t_Assentos.replace(assento, 0, id);
            if ( reservado ) {
                System.out.println("Assento reservado!");
                int id_thread = Integer.parseInt(Thread.currentThread().getName());
                String listaAssentos = fazString();
                buffer(3, id_thread, assento, listaAssentos);
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
    }cd tr

    public static int liberaAssento(int assento, int id){
        return 0;
    }

    public static String fazString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 1; i <= t_Assentos.size(); i++){
            if(i == t_Assentos.size()){
                sb.append(t_Assentos.get(i));
            }else {
                sb.append(t_Assentos.get(i));
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void buffer(int codigo, int id_thread, int assento, String assentos){
        //Escreve no buffer (1, id_thread, t_Assentos)

        if(assento == 0){
            try(BufferedWriter bw = new BufferedWriter((new FileWriter(nome_arquivo, true)))){
                String content = codigo+","+id_thread+","+assentos;
                bw.newLine();
                bw.write(content);
            } catch (IOException e){e.printStackTrace();}
        } else {
            try (BufferedWriter bw = new BufferedWriter((new FileWriter(nome_arquivo, true)))) {
                String content = codigo + "," + id_thread + "," + assento + "," + assentos;
                bw.newLine();
                bw.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
