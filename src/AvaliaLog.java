import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class AvaliaLog {

    public static boolean avaliar(String nome_arquivo){
        try {

            System.out.println("Iniciando conferência do log.");
            BufferedReader arquivo = new BufferedReader(new FileReader(nome_arquivo));

            Linha linha_anterior, linha_proxima;
            String linha = arquivo.readLine();
            linha_anterior = new Linha(linha);

            linha = arquivo.readLine();
            while(linha != null){
                linha_proxima = new Linha(linha);
                linha_anterior = linha_anterior.comparaMudancasRetornaCorreta(linha_proxima);
                if(linha_anterior == null){
                    System.out.println("Erro no arquivo de log. Linha: " + linha_proxima.toString());
                    return false;
                }
                linha = arquivo.readLine();
            }
            System.out.println("Arquivo de log conferido com sucesso!");
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private static class Linha {
        int operacao;
        int id_thread;
        int assento;
        HashMap<Integer, Integer> mapa;

        public Linha(String linha){
            if(linha != null && !linha.isEmpty()) {
                String[] splited = linha.split("\\[");

                String arrayMapa = splited[1].replace("]", "");

                mapa = new HashMap<>();

                String[] s = arrayMapa.split(",");
                for(int i = 0; i < s.length; i++){
                    mapa.put(i + 1, Integer.parseInt(s[i]));
                }

                splited = splited[0].split(",");
                operacao = Integer.parseInt(splited[0]);
                id_thread = Integer.parseInt(splited[1]);
                if(splited.length > 2){
                    assento = Integer.parseInt(splited[2]);
                } else {
                    assento = 0;
                }
            }
        }

        @Override
        public String toString() {
            String linha = operacao + "," + id_thread;

            if(assento != 0){
                linha += "," + assento;
            }

            linha += ",[";
            for(int i = 1; i < mapa.size(); i++){
                linha += mapa.get(i) + ",";
            }

            linha += mapa.get(mapa.size()) + "]";

            return linha;
        }

        /**
         * Compara o this com a próxima linha, simulando a operação e a id da thread envolvida.
         * Retorna null caso as linhas sejam incompatíveis, retorna proxLinha caso tudo esteja correto.
         * @param proxLinha
         * @return
         */
        public Linha comparaMudancasRetornaCorreta(Linha proxLinha){
            if(proxLinha.operacao == 1){
                // Se a operação for apenas Visualizar Mapa, o mapa novo deve ser igual ao anterior.
                for(int i = 1; i <= mapa.size(); i++) {
                    if(mapa.get(i) != proxLinha.mapa.get(i)){
                        return null;
                    }
                }
                return proxLinha;
            } else if(proxLinha.operacao == 2 || proxLinha.operacao == 3){ // aloca assento
                for(int i = 1; i <= mapa.size(); i++){
                    if( i == proxLinha.assento){
                        if(this.mapa.get(i) !=  0 || proxLinha.mapa.get(i) != proxLinha.id_thread){
                            System.out.println("Null op 2.01");
                            return null;
                        }
                    } else if (this.mapa.get(i) != proxLinha.mapa.get(i)){
                        System.out.println("Null op 2.02");
                        return null;
                    }
                }
                return proxLinha;
            } else if(proxLinha.operacao == 4){
                for(int i = 1; i <= mapa.size(); i++){
                    if( i == proxLinha.assento){
                        if(this.mapa.get(i) !=  proxLinha.id_thread || proxLinha.mapa.get(i) != 0){
                            System.out.println("Null op 4.01");
                            return null;
                        }
                    } else if (this.mapa.get(i) != proxLinha.mapa.get(i)){
                        System.out.println("Null op 4.02");
                        return null;
                    }
                }
                return proxLinha;
            }
            System.out.println("Null op 0000");
            return null;
        }
    }
}
