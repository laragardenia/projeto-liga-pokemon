package utilidades;

import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.Pokemon;
import modelo.TipoPokemon;
import modelo.TipoItem;
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
//armazenar regras de evolução e espalhar aleatoriamente as entidades (pokémons, treinadores e itens) nos vértices
public class LeitorArquivo {

    private static final Random random = new Random();
    
    private LeitorArquivo() {
    }

    //armazena o grafo carregado e o tempo limite da jornada e o dicionário de regras de evolução
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

    //inicializa toda a estrutura do grafo e suas entidades a  partir do arquivo de texto
    public static ResultadoCarregamento carregarMapa(String caminhoArquivo) throws IOException {
        Grafo grafo = new Grafo(); 
        Map<String, String[]> regrasEvolucao = new HashMap<>();       

        //armazena o que foi lido da seção ENTIDADES: [0] Pokemons, [1] Treinadores, [2] Itens
        int[] qtdEntidades = new int[3]; 
        int multiplicadorTempo = 15; //multiplicador de tempo definido como 15x (exigência: entre 10x e 15x)

        //abre o arquivo br e o fecha automaticamente no final
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            String secaoAtual = "";

            //para a leitura do arquivo
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                //pula linhas vazias ou comentários explicativos do arquivo .txt
                if (!linha.isEmpty() && !linha.startsWith("#")) {
                    if (isCabecalhoSecao(linha)) {
                        secaoAtual = linha;
                    } else {
                        processarLinha(secaoAtual, linha, grafo, qtdEntidades, regrasEvolucao);
                    }
                }
            }
        }

        //distribui os pokémons, treinadores e itens aleatoriamente pelas cidades
        espalharEntidades(grafo, qtdEntidades[0], qtdEntidades[1], qtdEntidades[2]);
        
        //preenche os ginásios com seus respectivos líderes e equipes
        adicionarLideresDosGinasios(grafo);

        //calcula o tempo global limite (15 vezes a soma dos pesos das arestas)
        int tempoLimiteGlobal = grafo.getSomaTotalPesos() * multiplicadorTempo;
        
        return new ResultadoCarregamento(grafo, tempoLimiteGlobal, regrasEvolucao);
    }
        

    //verifica se a linha lida corresponde ao identificador de uma nova seção do arquivo
    private static boolean isCabecalhoSecao(String linha) {
        return linha.equals("MAPA") || linha.equals("ENTIDADES") || linha.equals("EVOLUCOES");
    }

    //processa os dados da linha de acordo com a seção ativa usando switch para manter baixa complexidade cognitiva
    private static void processarLinha(String secao, String linha, Grafo grafo, int[] qtd, Map<String, String[]> regras) {
        String[] partes = linha.split("\\s+");

        switch (secao) {
            case "MAPA":
                if (partes.length >= 7) {
                    //dados do vertice de origem e destino
                    TipoVertice tipoOrigem = TipoVertice.valueOf(partes[0].toUpperCase());
                    TipoVertice tipoDestino = TipoVertice.valueOf(partes[3].toUpperCase());
                    
                    //adiciona os vértices ao grafo removendo sublinhados dos nomes
                    grafo.adicionarVertice(partes[1], partes[2].replace("_", " "), tipoOrigem);
                    grafo.adicionarVertice(partes[4], partes[5].replace("_", " "), tipoDestino);
                    
                    //cria a conexão bidirecional de ida e volta entre eles com o tempo de viagem
                    grafo.adicionarAresta(partes[1], partes[4], Integer.parseInt(partes[6]));
                }
                break;
            case "ENTIDADES":
                if (partes.length >= 2) {
                    atualizarQuantidadeEntidade(partes[0], Integer.parseInt(partes[1]), qtd);
                }
                break;
            case "EVOLUCOES":
                if (partes.length >= 3) {
                    //salva a regra no dicionário nativo: especieBase -> [especieEvoluida, xpNecessario]
                    regras.put(partes[0], new String[]{partes[1], partes[2]});
                }
                break;
            default:
                break;
        }
    }

    //atualiza o vetor de quantidades com os valores lidos da seção ENTIDADES
    private static void atualizarQuantidadeEntidade(String tipo, int quantidade, int[] qtd) {
        if (tipo.equalsIgnoreCase("POKEMONS")) {
            qtd[0] = quantidade;
        } else if (tipo.equalsIgnoreCase("TREINADORES")) {
            qtd[1] = quantidade;
        } else if (tipo.equalsIgnoreCase("ITENS")) {
            qtd[2] = quantidade;
        }
    }

    //sorteia vértices e insere as entidades RPG selvagens dentro de suas respectivas listas internas
    //complexidade: O(P + T + I) - sorteio em tempo constante O(1)
    private static void espalharEntidades(Grafo grafo, int qtdPokemons, int qtdTreinadores, int qtdItens) {
        //transforma o conjunto de vértices em uma lista para sortear posições por índice
        List<Vertice> todosVertices = new ArrayList<>(grafo.getTodosVertices());
        
        if (todosVertices.isEmpty()) {
            return;
        }

        //distribui os pokémons selvagens utilizando o gerador centralizado
        for (int i = 0; i < qtdPokemons; i++) {
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));
            local.adicionarPokemon(criarPokemonAleatorio("Selvagem_" + i));
        }

        //distribui os treinadores adversários
        for (int i = 0; i < qtdTreinadores; i++) {
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));
            local.adicionarTreinador(new Treinador("Adversário_" + i));
        }

        //distribui os itens de viagem (Erva ou Ovo)
        for (int i = 0; i < qtdItens; i++) {
            Vertice local = todosVertices.get(random.nextInt(todosVertices.size()));
            TipoItem tipo = random.nextBoolean() ? TipoItem.ERVA : TipoItem.OVO;
            local.adicionarItem(new Item(tipo));
        }
    }

    //cada ginásio recebe um líder oficial e uma insígnia própria
    private static void adicionarLideresDosGinasios(Grafo grafo) {
        for (Vertice vertice : grafo.getTodosVertices()) {
            if (vertice.getTipo() == TipoVertice.GINASIO) {
                Treinador lider = new Treinador("Líder de " + vertice.getNome(), "INSIGNIA-" + vertice.getId());
                preencherEquipe(lider, "Pokémon do líder " + vertice.getId());
                lider.adicionarXp(50 + random.nextInt(151));
                vertice.adicionarTreinador(lider);
            }
        }
    }

    //preenche a equipe do líder de ginásio com 3 pokémons aleatórios
    private static void preencherEquipe(Treinador treinador, String prefixo) {
        for (int i = 1; i <= 3; i++) {
            treinador.adicionarPokemon(criarPokemonAleatorio(prefixo + "-" + i));
        }
    }

    //método utilitário para gerar um pokémon com tipo, pontos de ataque (AP), defesa (DP) e XP sorteados
    private static Pokemon criarPokemonAleatorio(String nome) {
        TipoPokemon[] tipos = TipoPokemon.values();
        TipoPokemon tipo = tipos[random.nextInt(tipos.length)];
        int ap = 15 + random.nextInt(21);
        int dp = 5 + random.nextInt(16);
        Pokemon pokemon = new Pokemon(nome, tipo, ap, dp);
        pokemon.adicionarXp(random.nextInt(101));
        return pokemon;
    }
}
