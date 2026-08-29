package teste;

import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import simulacao.EquipeRocket;
import simulacao.JornadaPokemon;
import simulacao.ObservadorJornada;

import java.util.ArrayList;
import java.util.List;

/** Cenário integrado do núcleo independente da Parte 2. */
public class TesteIntegracaoParte2 {
    public static void main(String[] args) {
        Grafo grafo = criarMapa();
        Item mapa = new Item("Mapa da região");
        Item erva = new Item("Erva medicinal");
        grafo.getVertice("LAB").adicionarItem(mapa);
        grafo.getVertice("CID").adicionarItem(erva);

        JornadaPokemon jornada = new JornadaPokemon(
                grafo,
                grafo.getVertice("LAB"),
                100L);
        EquipeRocket rocket = new EquipeRocket(grafo, grafo.getVertice("GIN"));
        final List<String> eventos = new ArrayList<String>();

        jornada.adicionarObservador(new ObservadorJornada() {
            @Override
            public void aoChegar(
                    Vertice origem,
                    Vertice destino,
                    int tempoTrecho,
                    long tempoDecorrido,
                    List<Item> itensColetados) {
                eventos.add(origem.getId() + "->" + destino.getId()
                        + "@" + tempoDecorrido
                        + ":itens=" + itensColetados.size());
            }
        });

        ResultadoCaminho rotaGinasio = jornada.viajarPara(grafo.getVertice("GIN"));

        verificar(rotaGinasio.getTempoTotal() == 20L,
                "A melhor rota até o Ginásio deveria custar 20.");
        verificar(jornada.getPosicaoAtual().equals(rocket.getPosicaoAtual()),
                "Jogador e Rocket deveriam se encontrar no Ginásio.");
        verificar(jornada.getInventario().size() == 2,
                "Os itens do Laboratório e da Cidade deveriam ser coletados.");

        // Simula somente os resultados que serão fornecidos por outros módulos.
        rocket.derrotar();
        jornada.registrarInsignia("ROCHA");
        rocket.reativarEm(grafo.getVertice("CID"));

        jornada.viajarPara(grafo.getVertice("EST"));

        verificar(jornada.podeSeInscreverNaLiga(1),
                "A inscrição deveria ser permitida no Estádio.");
        verificar(rocket.isAtiva() && rocket.getPosicaoAtual().getId().equals("CID"),
                "A Rocket deveria estar ativa no respawn fornecido.");
        verificar(jornada.getTempoDecorrido() == 25L,
                "A jornada completa deveria acumular 25 unidades de tempo.");
        verificar(eventos.size() == 3,
                "O observador deveria registrar CID, GIN e EST.");

        System.out.println("Cenário integrado da Parte 2 passou.");
    }

    private static Grafo criarMapa() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("LAB", "Laboratório", TipoVertice.CARVALHO);
        grafo.adicionarVertice("CID", "Cidade", TipoVertice.MCP);
        grafo.adicionarVertice("GIN", "Ginásio", TipoVertice.GINASIO);
        grafo.adicionarVertice("EST", "Estádio da Liga", TipoVertice.ESTADIO);
        grafo.adicionarAresta("LAB", "CID", 10);
        grafo.adicionarAresta("CID", "GIN", 10);
        grafo.adicionarAresta("LAB", "GIN", 30);
        grafo.adicionarAresta("GIN", "EST", 5);
        return grafo;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
