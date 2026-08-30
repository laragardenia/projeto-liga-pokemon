package teste;

import algoritmos.BFS;
import algoritmos.DFS;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import jogo.ControladorJogo;
import jogo.ResultadoBatalha;
import jogo.SistemaBatalha;
import modelo.Item;
import modelo.Pokemon;
import modelo.TipoPokemon;
import modelo.Treinador;
import simulacao.EquipeRocket;
import simulacao.JornadaPokemon;

import java.util.Random;

/** Valida o fluxo real entre os módulos das três partes do projeto. */
public class TesteIntegracaoFinal {
    public static void main(String[] args) {
        Grafo grafo = criarMapa();
        verificar(new DFS().ehConexo(grafo), "O mapa de teste deveria ser conexo.");

        Treinador jogador = criarEquipe("Jogador", 120, 100);
        jogador.getEquipe().get(0).receberDano(10);

        Treinador lider = criarEquipe("Líder", 1, 0, "INSIGNIA-ROCHA");
        grafo.getVertice("GIN").adicionarTreinador(lider);
        grafo.getVertice("GIN").adicionarItem(new Item("Erva medicinal"));

        Treinador treinadorRocket = criarEquipe("Equipe Rocket", 1, 0);
        EquipeRocket rocket = new EquipeRocket(grafo, grafo.getVertice("GIN"));
        JornadaPokemon jornada = new JornadaPokemon(grafo, grafo.getVertice("LAB"), 100);

        ControladorJogo controlador = new ControladorJogo(
                grafo,
                jornada,
                jogador,
                rocket,
                treinadorRocket,
                new SistemaBatalha(new Random(0)),
                new BFS(),
                new Random(0));
        jornada.adicionarObservador(controlador);

        jornada.viajarPara(grafo.getVertice("GIN"));
        verificar(controlador.temEncontroComRocket(),
                "A chegada ao ginásio deveria detectar a Equipe Rocket.");
        verificar(controlador.getTreinadoresDisponiveis().contains(lider),
                "O líder deveria estar disponível no ginásio.");

        Item erva = jornada.getInventario().get(0);
        verificar(controlador.usarItem(erva), "A erva coletada deveria ser utilizada.");
        verificar(jogador.getEquipe().get(0).getHp() == 100,
                "A erva deveria recuperar 10 HP do Pokémon consciente.");

        ResultadoBatalha contraLider = controlador.batalharContra(lider);
        verificar(contraLider == ResultadoBatalha.VITORIA_DESAFIANTE,
                "O jogador deveria vencer o líder.");
        verificar(jornada.possuiInsignia("INSIGNIA-ROCHA"),
                "A vitória contra o líder deveria conceder a insígnia.");

        ResultadoBatalha contraRocket = controlador.batalharContraRocket();
        verificar(contraRocket == ResultadoBatalha.VITORIA_DESAFIANTE,
                "O jogador deveria vencer a Equipe Rocket.");
        verificar(rocket.getPosicaoAtual().equals(grafo.getVertice("EST")),
                "A Rocket deveria reaparecer na maior camada da BFS.");
        verificar(treinadorRocket.podeBatalhar(),
                "A equipe da Rocket deveria se recuperar para um encontro futuro.");
        verificar(jornada.getTempoDecorrido() == 3,
                "A viagem e as duas batalhas deveriam consumir três unidades de tempo.");

        System.out.println("Integração final entre jornada, batalha e BFS passou.");
    }

    private static Grafo criarMapa() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("LAB", "Laboratório", TipoVertice.CARVALHO);
        grafo.adicionarVertice("GIN", "Ginásio", TipoVertice.GINASIO);
        grafo.adicionarVertice("MCP", "Centro Médico", TipoVertice.MCP);
        grafo.adicionarVertice("EST", "Estádio", TipoVertice.ESTADIO);
        grafo.adicionarAresta("LAB", "GIN", 1);
        grafo.adicionarAresta("GIN", "MCP", 1);
        grafo.adicionarAresta("MCP", "EST", 1);
        return grafo;
    }

    private static Treinador criarEquipe(String nome, int ap, int dp) {
        return criarEquipe(nome, ap, dp, null);
    }

    private static Treinador criarEquipe(
            String nome,
            int ap,
            int dp,
            String codigoInsignia) {
        Treinador treinador = new Treinador(nome, codigoInsignia);
        for (int i = 1; i <= 3; i++) {
            treinador.adicionarPokemon(
                    new Pokemon(nome + "-" + i, TipoPokemon.NORMAL, ap, dp));
        }
        return treinador;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
