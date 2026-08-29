package teste;

import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import simulacao.JornadaPokemon;
import simulacao.ObservadorJornada;

import java.util.ArrayList;
import java.util.List;

/** Testes executáveis da primeira etapa da jornada. */
public class TesteJornadaPokemon {
    public static void main(String[] args) {
        testarPlanejamentoNaoMovimentaJogador();
        testarViagemAtualizaPosicaoETempo();
        testarDestinoInalcancavelNaoAlteraEstado();
        testarControleDoPrazo();
        testarHistoricoDeLocaisVisitados();
        testarObservadorExecutadoAposCadaTrecho();

        System.out.println("Todos os testes da Jornada Pokémon passaram.");
    }

    private static void testarPlanejamentoNaoMovimentaJogador() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        ResultadoCaminho rota = jornada.planejarRota(grafo.getVertice("GIN"));

        verificar(rota.isAlcancavel(), "A rota planejada deveria ser alcançável.");
        verificar(rota.getTempoTotal() == 20L, "A rota planejada deveria custar 20.");
        verificar(jornada.getPosicaoAtual().getId().equals("LAB"),
                "Planejar uma rota não deveria movimentar o jogador.");
        verificar(jornada.getTempoDecorrido() == 0L,
                "Planejar uma rota não deveria alterar o relógio.");
    }

    private static void testarViagemAtualizaPosicaoETempo() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        ResultadoCaminho rota = jornada.viajarPara(grafo.getVertice("GIN"));

        verificar(rota.getCaminho().size() == 3,
                "A viagem deveria passar por três vértices.");
        verificar(jornada.getPosicaoAtual().getId().equals("GIN"),
                "O jogador deveria terminar no ginásio.");
        verificar(jornada.getTempoDecorrido() == 20L,
                "A viagem deveria acrescentar 20 unidades ao relógio.");
    }

    private static void testarDestinoInalcancavelNaoAlteraEstado() {
        Grafo grafo = criarGrafoBase();
        grafo.adicionarVertice("EST", "Estádio", TipoVertice.ESTADIO);
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        ResultadoCaminho rota = jornada.viajarPara(grafo.getVertice("EST"));

        verificar(!rota.isAlcancavel(), "O estádio deveria estar inalcançável.");
        verificar(jornada.getPosicaoAtual().getId().equals("LAB"),
                "Uma viagem impossível não deveria alterar a posição.");
        verificar(jornada.getTempoDecorrido() == 0L,
                "Uma viagem impossível não deveria alterar o relógio.");
    }

    private static void testarControleDoPrazo() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 30L);

        jornada.viajarPara(grafo.getVertice("GIN"));
        verificar(jornada.estaDentroDoPrazo(),
                "A jornada deveria estar dentro do prazo após 20 unidades.");
        verificar(jornada.getTempoRestante() == 10L,
                "Deveriam restar 10 unidades de tempo.");

        jornada.viajarPara(grafo.getVertice("LAB"));
        verificar(!jornada.estaDentroDoPrazo(),
                "A jornada deveria exceder o prazo após 40 unidades.");
        verificar(jornada.getTempoRestante() == 0L,
                "O tempo restante não deveria ser negativo.");
    }

    private static void testarHistoricoDeLocaisVisitados() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        verificar(jornada.getLocaisVisitados().size() == 1,
                "O histórico deveria começar com a posição inicial.");

        jornada.planejarRota(grafo.getVertice("GIN"));
        verificar(jornada.getLocaisVisitados().size() == 1,
                "Planejar uma rota não deveria alterar o histórico.");

        jornada.viajarPara(grafo.getVertice("GIN"));
        List<Vertice> visitados = jornada.getLocaisVisitados();

        verificar(visitados.size() == 3,
                "O histórico deveria registrar a posição inicial e duas chegadas.");
        verificar(visitados.get(0).getId().equals("LAB")
                        && visitados.get(1).getId().equals("CID")
                        && visitados.get(2).getId().equals("GIN"),
                "O histórico deveria seguir a ordem LAB, CID, GIN.");

        boolean alteracaoRejeitada = false;
        try {
            visitados.add(grafo.getVertice("LAB"));
        } catch (UnsupportedOperationException e) {
            alteracaoRejeitada = true;
        }
        verificar(alteracaoRejeitada,
                "O histórico retornado não deveria permitir alterações externas.");
    }

    private static void testarObservadorExecutadoAposCadaTrecho() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);
        final List<String> chegadas = new ArrayList<String>();

        jornada.adicionarObservador(new ObservadorJornada() {
            @Override
            public void aoChegar(
                    Vertice origem,
                    Vertice destino,
                    int tempoTrecho,
                    long tempoDecorrido) {
                chegadas.add(origem.getId() + "->" + destino.getId()
                        + ":" + tempoTrecho + ":" + tempoDecorrido);
            }
        });

        jornada.viajarPara(grafo.getVertice("GIN"));

        verificar(chegadas.size() == 2,
                "O observador deveria ser chamado uma vez por trecho.");
        verificar(chegadas.get(0).equals("LAB->CID:10:10")
                        && chegadas.get(1).equals("CID->GIN:10:20"),
                "O observador deveria receber trecho e relógio já atualizados.");
    }

    private static JornadaPokemon criarJornada(Grafo grafo, long prazo) {
        Vertice laboratorio = grafo.getVertice("LAB");
        return new JornadaPokemon(grafo, laboratorio, prazo);
    }

    private static Grafo criarGrafoBase() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("LAB", "Laboratório", TipoVertice.CARVALHO);
        grafo.adicionarVertice("CID", "Cidade A", TipoVertice.MCP);
        grafo.adicionarVertice("GIN", "Ginásio", TipoVertice.GINASIO);

        grafo.adicionarAresta("LAB", "CID", 10);
        grafo.adicionarAresta("CID", "GIN", 10);
        grafo.adicionarAresta("LAB", "GIN", 30);
        return grafo;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
