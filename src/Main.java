import algoritmos.BFS;
import algoritmos.DFS;
import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import jogo.ControladorJogo;
import jogo.MenuJogo;
import modelo.Pokemon;
import modelo.Item;
import modelo.TipoItem;
import modelo.TipoPokemon;
import modelo.Treinador;
import simulacao.EquipeRocket;
import simulacao.JornadaPokemon;
import utilidades.LeitorArquivo;

import java.util.Scanner;

/** Ponto de entrada que conecta o leitor, o grafo e a jornada. */
public class Main {
    public static void main(String[] args) {
        String caminhoMapa = args.length > 0 ? args[0] : "mapa_regiao.txt";

        try {
            LeitorArquivo.ResultadoCarregamento carregamento =
                    LeitorArquivo.carregarMapa(caminhoMapa);
            Grafo grafo = carregamento.getGrafo();

            if (!new DFS().ehConexo(grafo)) {
                System.out.println("O mapa não é conexo. O jogo não pode ser iniciado.");
                return;
            }

            Vertice inicio = localizarVerticeDoTipo(grafo, TipoVertice.CARVALHO);
            adicionarItensIniciais(inicio);
            Treinador jogador = criarTreinadorJogador();
            Treinador treinadorRocket = criarTreinadorRocket();
            Vertice posicaoRocket = new BFS().escolherRespawnDistante(grafo, inicio);

            JornadaPokemon jornada = new JornadaPokemon(
                    grafo,
                    inicio,
                    carregamento.getTempoLimiteGlobal());
            EquipeRocket equipeRocket = new EquipeRocket(grafo, posicaoRocket);
            ControladorJogo controlador = new ControladorJogo(
                    grafo, jornada, jogador, equipeRocket, treinadorRocket);
            jornada.adicionarObservador(controlador);

            System.out.println("Mapa carregado e validado com sucesso.");
            System.out.println("Vértices: " + grafo.getQuantidadeVertices());
            System.out.println("Arestas: " + grafo.getQuantidadeArestas());
            System.out.println("Prazo da Liga: " + carregamento.getTempoLimiteGlobal());
            System.out.println("Posição inicial: " + inicio.getNome());
            System.out.println("Equipe Rocket: " + posicaoRocket.getNome());

            if (args.length > 1) {
                Vertice destino = grafo.getVertice(args[1]);
                if (destino == null) {
                    throw new IllegalArgumentException("Destino inexistente: " + args[1]);
                }
                ResultadoCaminho resultado = jornada.viajarPara(destino);
                if (resultado.isAlcancavel()) {
                    System.out.println("Destino alcançado: " + destino.getNome());
                    System.out.println("Tempo da rota: " + resultado.getTempoTotal());
                    System.out.println("Tempo decorrido: " + jornada.getTempoDecorrido());
                }
            } else {
                MenuJogo menu = new MenuJogo(
                        grafo,
                        jornada,
                        jogador,
                        controlador,
                        carregamento.getRegrasEvolucao(),
                        new Scanner(System.in));
                menu.executar();
            }
        } catch (Exception erro) {
            System.out.println("Não foi possível iniciar o jogo: " + erro.getMessage());
        }
    }

    private static Vertice localizarVerticeDoTipo(Grafo grafo, TipoVertice tipo) {
        for (Vertice vertice : grafo.getTodosVertices()) {
            if (vertice.getTipo() == tipo) {
                return vertice;
            }
        }
        throw new IllegalStateException("O mapa não possui um vértice do tipo " + tipo + ".");
    }

    private static Treinador criarTreinadorJogador() {
        Treinador jogador = new Treinador("Jogador");
        jogador.adicionarPokemon(new Pokemon("Charmander", TipoPokemon.FOGO, 50, 35));
        jogador.adicionarPokemon(new Pokemon("Squirtle", TipoPokemon.AGUA, 48, 37));
        jogador.adicionarPokemon(new Pokemon("Bulbasaur", TipoPokemon.PLANTA, 49, 36));
        return jogador;
    }

    private static Treinador criarTreinadorRocket() {
        Treinador rocket = new Treinador("Equipe Rocket");
        rocket.adicionarPokemon(new Pokemon("Koffing", TipoPokemon.VENENOSO, 25, 15));
        rocket.adicionarPokemon(new Pokemon("Gastly", TipoPokemon.FANTASMA, 25, 15));
        rocket.adicionarPokemon(new Pokemon("Meowth", TipoPokemon.NORMAL, 25, 15));
        return rocket;
    }

    private static void adicionarItensIniciais(Vertice laboratorio) {
        laboratorio.adicionarItem(new Item(TipoItem.INCUBADORA));
        for (int i = 0; i < 7; i++) {
            laboratorio.adicionarItem(new Item(TipoItem.POKEBOLA));
        }
    }
}
