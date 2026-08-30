package utilidades;

import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.Pokemon;
import modelo.Treinador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//classe responsável por ler o arquivo texto com a estrutura do mapa,
//construir a representação do grafo em memória, calcular o limite de tempo global
//e espalhar aleatoriamente as entidades (pokémons, treinadores e itens) nos vértices
public class LeitorArquivo {

    //recomendações do sonarLint: reutilizar uma única instância de Random para evitar desperdício de recursos
    // e adicionar construtor privado simples (não precisa ser instanciada)
    private static final Random random = new Random();
    
    private LeitorArquivo() {
    }

    //armazena o grafo carregado e o tempo limite da jornada
    //para retornar duas informações de uma vez só no método de leitura
    public static class ResultadoCarregamento {
        private final Grafo grafo;
        private final int tempoLimiteGlobal;
        private final Map<String, String[]> regrasEvolucao;

        public ResultadoCarregamento(Grafo grafo, int tempoLimiteGlobal, Map<String, String[]> regrasEvolucao) {
            this.grafo = grafo;
            this.tempoLimiteGlobal = tempoLimiteGlobal;
            this.regrasEvolucao = regrasEvolucao;
        }

        public Grafo getGrafo() { return grafo; }
        public int getTempoLimiteGlobal() { return tempoLimiteGlobal; }
        public Map<String, String[]> getRegrasEvolucao() { return regrasEvolucao; }
    }

    //inicializa toda a estrutura do grafo e suas entidades
    //complexidade: O(L + P + T + I), onde L é o número de linhas do arquivo,
    // e P, T, I são as quantidades de pokémons, treinadores e itens distribuídos
    public static ResultadoCarregamento carregarMapa(String caminhoArquivo) throws IOException {
        Grafo grafo = new Grafo(); 
        Map<String, String[]> regrasEvolucao = new HashMap<>();       

        //armazena o que foi lido da seção ENTIDADES
        int qtdPokemons = 0;
        int qtdTreinadores = 0;
        int qtdItens = 0;      
        int multiplicadorTempo = 15; //multiplicador de tempo definido como 15x (exigência: entre 10x e 15x)

        //abre o arquivo br e o fecha automaticamente no final
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            String secaoAtual = ""; 

            //para a leitura do arquivo
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                //pula linhas vazias ou comentários explicativos do arquivo .txt
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                if (linha.equals("MAPA") || linha.equals("ENTIDADES") || linha.equals("EVOLUCOES")) {
                    secaoAtual = linha;
                    continue;
                }

                String[] partes = linha.split("\\s+");

                //parte do mapa
                if (secaoAtual.equals("MAPA") && partes.length >= 7) {
                    //dados do vertice de origem
                    TipoVertice tipoOrigem = TipoVertice.valueOf(partes[0].toUpperCase());
                    String idOrigem = partes[1];
                    String nomeOrigem = partes[2].replace("_", " "); // Remove sublinhados do nome

                    //dados do vertice de destino
                    TipoVertice tipoDestino = TipoVertice.valueOf(partes[3].toUpperCase());
                    String idDestino = partes[4];
                    String nomeDestino = partes[5].replace("_", " ");

                    int tempoAresta = Integer.parseInt(partes[6]); //tempo de viagem

                    //adicona os vértices ao grafo
                    grafo.adicionarVertice(idOrigem, nomeOrigem, tipoOrigem);
                    grafo.adicionarVertice(idDestino, nomeDestino, tipoDestino);
                    //cria a conexão bidirecional de ida e volta entre eles
                    grafo.adicionarAresta(idOrigem, idDestino, tempoAresta);
                    
                } 
                //parte de entidades
                else if (secaoAtual.equals("ENTIDADES") && partes.length >= 2) {
                    String tipo = partes[0].toUpperCase();
                    int quantidade = Integer.parseInt(partes[1]);

                    if (tipo.equals("POKEMONS")) {
                        qtdPokemons = quantidade;
                    } else if (tipo.equals("TREINADORES")) {
                        qtdTreinadores = quantidade;
                    } else if (tipo.equals("ITENS")) {
                        qtdItens = quantidade;
                    }
                }  
                //parte de evoluções
                else if (secaoAtual.equals("EVOLUCOES") && partes.length >= 3) {
                    String especieBase = partes[0];
                    String especieEvoluida = partes[1];
                    String xpNecessario = partes[2];

                    regrasEvolucao.put(especieBase, new String[]{especieEvoluida, xpNecessario});
                    //System.out.println("Regra de evolução lida: " + especieBase + " evolui para " + especieEvoluida + " com " + xpNecessario + " XP.");
                }
            }
        }

        //distribui os pokémons, treinadores e itens aleatoriamente pelas cidades
        espalharEntidades(grafo, qtdPokemons, qtdTreinadores, qtdItens);

        //calcula o tempo global limite (15 vezes a soma dos pesos das arestas)
        int somaPesos = grafo.getSomaTotalPesos();
        int tempoLimiteGlobal = somaPesos * multiplicadorTempo;

        return new ResultadoCarregamento(grafo, tempoLimiteGlobal, regrasEvolucao);
    }

    //sorteia vértices e insere as entidades RPG dentro de suas respectivas listas internas
    //complexidade: O(P + T + I) - sorteio em tempo constante O(1)
    private static void espalharEntidades(Grafo grafo, int qtdPokemons, int qtdTreinadores, int qtdItens) {
        //transforma o conjunto de vértices em uma lista para sortear posições por índice
        List<Vertice> todosVertices = new ArrayList<>(grafo.getTodosVertices());

        if (todosVertices.isEmpty()) {
            return;
        }

        //distribui os pokémons selvagens
        for (int i = 0; i < qtdPokemons; i++) {
            //sorteia um vértice qualquer da lista em tempo O(1)
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));            
            //instancia o objeto e o coloca na lista interna do local
            Pokemon novoPokemon = new Pokemon();
            local.adicionarPokemon(novoPokemon);
        }

        //distribui os treinadores adversários
        for (int i = 0; i < qtdTreinadores; i++) {
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));            
            Treinador novoTreinador = new Treinador();
            local.adicionarTreinador(novoTreinador);
        }

        //distribui os itens de viagem
        for (int i = 0; i < qtdItens; i++) {
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));            
            Item novoItem = new Item();
            local.adicionarItem(novoItem);
        }
    }
}