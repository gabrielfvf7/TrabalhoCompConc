import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static ConcurrentMap<Integer, Integer> t_Assentos = new ConcurrentHashMap<>();
    private static int t_Assento;

    private static String nome_arquivo = "teste.txt";
    private static ConcurrentLinkedQueue<String> textoBuffer = new ConcurrentLinkedQueue<>();
    private static ReentrantLock lock = new ReentrantLock();
    private static int qtd = 0;
    private static boolean finaliza = false;

    private static Random random = new Random();
    private static BufferedWriter bw;

    public static void main(String args[]){

        nome_arquivo = args[0];

        int qtd = Integer.parseInt(args[1]);

        // Inicialização dos assentos com 0 - Livre
        // Numeracao do assento 1 até o assento = qtd
        for(int i = 1; i <= qtd; i++){
            t_Assentos.put(i, 0);
        }

        Thread1 td1 = new Thread1();
        Thread2 td2 = new Thread2();
        Thread3 td3 = new Thread3(qtd/2);
        Thread3 td3_2 = new Thread3(qtd/2); // Propositalmente igual ao td3 para fins de teste
        Thread3 td3_3 = new Thread3(qtd/3 + 1);
        T_Log TL = new T_Log();

        TL.setName("0");
        td1.setName("1");
        td2.setName("2");
        td3.setName("3");
        td3_2.setName("4");
        td3_3.setName("5");

        TL.start();
        td1.start();
        td2.start();
        td3.start();
        td3_2.start();
        td3_3.start();

        try {
            td1.join();
            td2.join();
            td3.join();
            td3_2.join();
            td3_3.join();
            finaliza = true;
            TL.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }


        System.out.println("\nTESTE PRA VER SE O BUFFER TÁ VAZIO:");
        for(String s : textoBuffer){
            if(s != null && !s.isEmpty())
                System.out.println(s);
        }
    }

    /**
     * Thread que gerencia 3 assentos. Repete 3 vezes os passos:
     * 1 - Gera um primeiro assento aleatório.
     * 2 - Aloca três assentos consecutivos a partir do primeiro gerado a partir de um primeiro assento aleatório.
     * 3 - Desaloca os três assentos alocados no passo anterior.
     * 4 - Pausa por 1 segundo.
     * e em seguida desaloca os 3 assentos.
     */
    public static class Thread1 extends Thread{
        int[] assentos = {0, 0, 0};
        int[] alocado = {0, 0, 0};
        int n_execucoes = 3;

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

                try { sleep(21); } catch (InterruptedException e){}

                visualizaAssentos();

                int liberados = liberaAssento(assentos[0], id);
                liberados += liberaAssento(assentos[1], id);
                liberados += liberaAssento(assentos[2], id);

                try { sleep(83); } catch (InterruptedException e) {}
            }
        }
    }

    /**
     * Thread que aloca um assento aleatório livre, e em seguida desaloca o mesmo.
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

    /**
     * Thread 3 aloca dois assentos: um livre e um fixo, que é passado como parâmetro no construtor.
     */
    public static class Thread3 extends Thread{

        int reservado_normal = 0;
        int assento_livre = 0;
        final int assento_normal;

        public Thread3 (int assento1){
            this.assento_normal = assento1;
        }

        public void run(){
            int id = Integer.parseInt(getName());

            assento_livre = alocaAssentoLivre(id);
            visualizaAssentos();

            reservado_normal = alocaAssentoDado(assento_normal, id);
            visualizaAssentos();

            try { sleep(30); } catch (InterruptedException e){}

            if(assento_livre != 0) {
                liberaAssento(assento_livre, id);
            }

            try { sleep(10); } catch (InterruptedException e){}

            if(reservado_normal == 1) {
                liberaAssento(assento_normal, id);
            }

            try { sleep(35); } catch (InterruptedException e){}
        }
    }

    public static class T_Log extends Thread{
        public void run(){
            inicializaBuffer();

            try {
                while (!finaliza) {
                    lock.lock();
                    while(!textoBuffer.isEmpty()){
                        bw.write(textoBuffer.remove());
                        bw.newLine();
                    }
                    lock.unlock();
                }
            } catch(IOException e) {}

            finalizaBuffer();
        }
    }

    public static synchronized void visualizaAssentos() {
        String listaAssentos = fazString();

        System.out.println("Visualização do mapa: "+listaAssentos);

        int id_thread = Integer.parseInt(Thread.currentThread().getName());
        buffer(1,id_thread, 0);
    }

    public static synchronized int alocaAssentoDado(int assento, int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            boolean reservado = t_Assentos.replace(assento, 0, id);
            if (reservado) {
                int id_thread = Integer.parseInt(Thread.currentThread().getName());
                buffer(3, id_thread, assento);
                System.out.println("(" + id + ")Alocado o assento "+assento);
                return 1;
            } else {
                System.out.println("(" + id + ")Assento ja ocupado " + assento);
                return 0;
            }
        } else {
                System.out.println("(" + id + ")Numero de id diferente!");
            return 0;
        }
    }

    /**
     * O método aloca um assento aleatório disponível seguindo os seguintes passos:
     * 1 - Faz um loop por no máximo 5 vezes onde ele gera um número aleatório e tenta alocar esse lugar.
     * 2 - Caso não consiga, faz um loop por todos os assentos, do primeiro até o último, buscando um assento vazio.
     * 3 - Caso encontre um assento vazio, o aloca. Se não, retorna.
     * @param id ID da Thread que chamou a função.
     * @return 0, se não conseguir reservar um assento, ou o número do assento alocado caso consiga.
     */
    public static int alocaAssentoLivre(int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            int assento = 0;
            int max_tentativas = 5;
            boolean reservado = false;

            while(!reservado && max_tentativas > 0) {
                assento = random.nextInt(t_Assentos.size()) + 1;
                reservado = t_Assentos.replace(assento, 0, id);
                max_tentativas--;
            }

            if(!reservado){
                assento = 0;
                // Caso não tenha conseguido reservar um assento nas 5 tentativas aleatórias acima,
                // tenta todos os assentos da lista até encontrar um vazio ou chegar no final.
                for(int i = 1; i <= t_Assentos.size(); i++){
                    if(t_Assentos.get(i) == 0){
                        reservado = t_Assentos.replace(i, 0, id);
                        if(reservado) {
                            assento = i;
                            break;
                        }
                    }
                }
            }

            if(reservado) {
                buffer(2, id, assento);
                System.out.println("(" + id + ")Alocado o assento livre "+assento);
                return assento;
            } else {
                System.out.println("(" + id + ")Não há assentos livres");
                return 0;
            }
        } else {
            System.out.println("Id diferente!");
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
        if(id == Integer.parseInt(Thread.currentThread().getName()) && assento > 0) {
            // Atualiza para 0 - Livre o estado do assento se o atual valor = id da thread.
            boolean liberou = t_Assentos.replace(assento, id, 0);
            if (liberou) {
                buffer(4, id, assento);
                System.out.println("(" + id + ")Liberado o assento "+assento);
                return 1;
            } else {
                System.out.println("(" + id + ")Erro ao liberar assento!");
                return 0;
            }
        } else {
                System.out.println("Id diferente!");
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
                String assentos = fazString();
                if(qtd > 10){ qtd = 0;}
                if (assento == 0) {
                    //try {
                        String content = codigo + "," + id_thread + "," + assentos;
                        lock.lock();
                        textoBuffer.add(content);
                        lock.unlock();
                        //bw.write(content);
                        //bw.newLine();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                } else {
                   // try {
                    String content = codigo + "," + id_thread + "," + assento + "," + assentos;
                    lock.lock();
                    textoBuffer.add(content);
                    lock.unlock();
                       // bw.write(content);
                       // bw.newLine();
                   // } catch (IOException e) {
                   //     e.printStackTrace();
                   // }
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
