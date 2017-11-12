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

        td1.setName("1");
        td1.start();

        try {
            td1.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        finalizaBuffer();
    }

    public static class Thread1 extends Thread{

        int[] assentos = {0, 0, 0};

        public void run(){

            int id = Integer.parseInt(getName());

            assentos[0] = random.nextInt(t_Assentos.size() - 3) + 1;
            assentos[1] = assentos[0] + 1;
            assentos[2] = assentos[1] + 1;

            visualizaAssentos();

            int alocado = alocaAssentoDado(assentos[0], id);
            if(alocado == 1)
                alocado = alocaAssentoDado(assentos[1], id);
            if(alocado == 1)
                alocado = alocaAssentoDado(assentos[2], id);

            visualizaAssentos();

            if(alocado == 1){
                System.out.println("Tres assentos alocados com sucesso");
            }

            liberaAssento(assentos[0], id);
            visualizaAssentos();
            liberaAssento(assentos[1]+2, id);
            visualizaAssentos();

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
        buffer(1,id_thread, 0);
    }

    public static int alocaAssentoDado(int assento, int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            boolean reservado = t_Assentos.replace(assento, 0, id);
            if (reservado) {
                System.out.println("Assento reservado!");
                int id_thread = Integer.parseInt(Thread.currentThread().getName());
                buffer(3, id_thread, assento);
                return 1;
            } else {
                System.out.println("Assento não reservado!");
                return 0;
            }
        } else {
            System.out.println("Assento não reservado - ID não compatível com o nome da thread.");
            return 0;
        }
    }

    public static int alocaAssentoLivre(int id){
        if(id == Integer.parseInt(Thread.currentThread().getName())) {
            // TODO Pegar numero aleatório.
            return 0;
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

    public static void inicializaBuffer(){
        try{
            bw = new BufferedWriter(new FileWriter(nome_arquivo, true));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void buffer(int codigo, int id_thread, int assento){
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
            bw.flush();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
