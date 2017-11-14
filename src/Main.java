import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {

    private static ConcurrentMap<Integer, Integer> t_Assentos = new ConcurrentHashMap<>();
    private static int t_Assento;

    private static String nome_arquivo = "teste.txt";

    private static Random random = new Random();
    private static BufferedWriter bw;

    public static void main(String args[]){

        nome_arquivo = args[0];
        inicializaBuffer();

        int qtd = Integer.parseInt(args[1]);

        // Inicialização dos assentos com 0 - Livre
        // Numeracao do assento 1 até o assento = qtd
        for(int i = 1; i <= qtd; i++){
            t_Assentos.put(i, 0);
        }

        Thread1 td1 = new Thread1();
        Thread2 td2 = new Thread2();

        td1.setName("1");
        td2.setName("2");

        td1.start();
        td2.start();

        try {
            td1.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        finalizaBuffer();
    }

    /**
     * Thread que gerencia 3 assentos. Repete 5 vezes os passos:
     * 1 - Gera um primeiro assento aleatório.
     * 2 - Aloca três assentos consecutivos a partir do primeiro gerado a partir de um primeiro assento aleatório.
     * 3 - Desaloca os três assentos alocados no passo anterior.
     * 4 - Pausa por 1 segundo.
     * e em seguida desaloca os 3 assentos.
     */
    public static class Thread1 extends Thread{
        int[] assentos = {0, 0, 0};
        int[] alocado = {0, 0, 0};
        int n_execucoes = 5;

        public void run(){
            int id = Integer.parseInt(getName());

            while(n_execucoes-- > 0) {
                assentos[0] = random.nextInt(t_Assentos.size() - 2) + 1;
                assentos[1] = assentos[0] + 1;
                assentos[2] = assentos[1] + 1;

                visualizaAssentos();

                alocado[0] = alocaAssentoDado(assentos[0], id);
                alocado[1] = alocaAssentoDado(assentos[1], id);
                alocado[2] = alocaAssentoDado(assentos[2], id);

                try { sleep(80); } catch (InterruptedException e){}

                visualizaAssentos();

                int liberados = liberaAssento(assentos[0], id);
                liberados += liberaAssento(assentos[1], id);
                liberados += liberaAssento(assentos[2], id);
                //System.out.println(liberados + " assentos desalocados com sucesso");
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Thread que aloca um assento aleatório livre, e em seguida desaloca o mesmo.
     * Pausa uma vez após alocar o assento, e outra vez após desalocar o assento,
     * apenas para evitar um código extremamente rápido e manter assentos alocados por mais tempo.
     */
    public static class Thread2 extends Thread{
        int assento = 0;
        int r = random.nextInt(t_Assentos.size()+1);

        public void run(){
            int id = Integer.parseInt(getName());
            while(r-- > 0){
                assento = alocaAssentoLivre(id);
                visualizaAssentos();

                try { sleep(100); } catch (InterruptedException e){}

                if(assento != 0)
                    liberaAssento(assento, id);

                try { sleep(50); } catch (InterruptedException e){}
            }
        }
    }

    public static synchronized void visualizaAssentos() {
        String listaAssentos = fazString();

        System.out.println(listaAssentos);

        int id_thread = Integer.parseInt(Thread.currentThread().getName());
        buffer(1,id_thread, 0);
    }

    public static int alocaAssentoDado(int assento, int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            boolean reservado = t_Assentos.replace(assento, 0, id);
            if (reservado) {
                System.out.println("Assento "+ assento +" reservado!");
                int id_thread = Integer.parseInt(Thread.currentThread().getName());
                buffer(3, id_thread, assento);
                return 1;
            } else {
                System.out.println("Assento "+assento+" não reservado, pois já está ocupado!");
                return 0;
            }
        } else {
            System.out.println("Assento "+assento+ " não reservado - ID não compatível com o nome da thread.");
            return 0;
        }
    }

    public static int alocaAssentoLivre(int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            int assento = 0;
            int max_tentativas = 5;
            int reservado = 0;

            while(reservado == 0 && max_tentativas > 0) {
                assento = random.nextInt(t_Assentos.size()) + 1;
                reservado = alocaAssentoDado(assento, id);
                max_tentativas--;
            }

            if(reservado == 0){
                // Caso não tenha conseguido reservar um assento nas 5 tentativas aleatórias acima,
                // tenta todos os assentos da lista até encontrar um vazio ou chegar no final.
                for(int i = 1; i <= t_Assentos.size(); i++){
                    if(t_Assentos.get(i) == 0){
                        reservado = alocaAssentoDado(i, id);
                        assento = i;
                        if(reservado == 1)
                            break;
                    }
                }
            }

            if(reservado == 1) {
                System.out.println("Assento " + assento + " reservado!");
                buffer(2, id, assento);
                return assento;
            } else {
                System.out.println("Não há assentos disponíveis");
                return 0;
            }
        } else {
            System.out.println("Assento não reservado - ID não compatível com o nome da thread.");
            return 0;
        }
    }

    /**
     * Operação 4 - Libera um assento dado se o id for igual ao id da thread que o alocou.
     * @param assento Número do assento a ser desalocado
     * @param id ID da thread que alocou o assento
     * @return 0 - se o assento não for desalocado. <br> 1 - se o assento for desalocado.
     */
    public static int liberaAssento(int assento, int id){
        // Testa se o parâmetro id é mesmo da Thread que chamou a função
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            // Atualiza para 0 - Livre o estado do assento se o atual valor = id da thread.
            boolean liberou = t_Assentos.replace(assento, id, 0);
            if (liberou) {
                System.out.println("Assento "+assento+" liberado!");
                buffer(4, id, assento);
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
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

    private static void inicializaBuffer(){
        try{
            bw = new BufferedWriter(new FileWriter(nome_arquivo, true));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private synchronized static void buffer(int codigo, int id_thread, int assento){
        //Escreve no buffer (1, id_thread, t_Assentos)

        String assentos = fazString();

        if(assento == 0){
            try {
                String content = codigo + "," + id_thread + "," + assentos;
                bw.write(content);
                bw.newLine();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            try {
                String content = codigo + "," + id_thread + "," + assento + "," + assentos;
                bw.write(content);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void finalizaBuffer(){
        try {
            bw.write("-------");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
