package teste;

import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import simulacao.JornadaPokemon;
import simulacao.ObservadorJornada;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Testes executáveis da primeira etapa da jornada. */
public class TesteJornadaPokemon {
    public static void main(String[] args) {
        testarPlanejamentoNaoMovimentaJogador();
        testarViagemAtualizaPosicaoETempo();
        testarDestinoInalcancavelNaoAlteraEstado();
        testarControleDoPrazo();
        testarHistoricoDeLocaisVisitados();
        testarObservadorExecutadoAposCadaTrecho();
        testarColetaItensDaPosicaoInicial();
        testarColetaItensDuranteViagem();
        testarRevisitaNaoDuplicaItens();
        testarInventarioNaoPermiteAlteracaoExterna();
        testarRegistroDeInsigniasSemDuplicidade();
        testarInscricaoNaLigaExigeLocalQuantidadeEPrazo();

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
                    long tempoDecorrido,
                    List<Item> itensColetados) {
                chegadas.add(origem.getId() + "->" + destino.getId()
                        + ":" + tempoTrecho + ":" + tempoDecorrido
                        + ":" + itensColetados.size());
            }
        });

        jornada.viajarPara(grafo.getVertice("GIN"));

        verificar(chegadas.size() == 2,
                "O observador deveria ser chamado uma vez por trecho.");
        verificar(chegadas.get(0).equals("LAB->CID:10:10:0")
                        && chegadas.get(1).equals("CID->GIN:10:20:0"),
                "O observador deveria receber trecho e relógio já atualizados.");
    }

    private static void testarColetaItensDaPosicaoInicial() {
        Grafo grafo = criarGrafoBase();
        Item mapa = new Item("Mapa da região");
        grafo.getVertice("LAB").adicionarItem(mapa);

        JornadaPokemon jornada = criarJornada(grafo, 100L);

        verificar(jornada.getInventario().size() == 1
                        && jornada.getInventario().contains(mapa),
                "O item da posição inicial deveria ser coletado.");
        verificar(grafo.getVertice("LAB").getItensDisponiveis().isEmpty(),
                "O item coletado deveria ser removido do local.");
    }

    private static void testarColetaItensDuranteViagem() {
        Grafo grafo = criarGrafoBase();
        Item erva = new Item("Erva medicinal");
        Item pedra = new Item("Pedra de evolução");
        grafo.getVertice("CID").adicionarItem(erva);
        grafo.getVertice("GIN").adicionarItem(pedra);
        JornadaPokemon jornada = criarJornada(grafo, 100L);
        final List<Integer> quantidadesColetadas = new ArrayList<Integer>();

        jornada.adicionarObservador(new ObservadorJornada() {
            @Override
            public void aoChegar(
                    Vertice origem,
                    Vertice destino,
                    int tempoTrecho,
                    long tempoDecorrido,
                    List<Item> itensColetados) {
                quantidadesColetadas.add(itensColetados.size());
            }
        });

        jornada.viajarPara(grafo.getVertice("GIN"));

        verificar(jornada.getInventario().size() == 2,
                "A jornada deveria coletar os itens dos dois locais visitados.");
        verificar(jornada.getInventario().contains(erva)
                        && jornada.getInventario().contains(pedra),
                "O inventário deveria conter os itens encontrados na rota.");
        verificar(grafo.getVertice("CID").getItensDisponiveis().isEmpty()
                        && grafo.getVertice("GIN").getItensDisponiveis().isEmpty(),
                "Os itens coletados não deveriam permanecer no mapa.");
        verificar(quantidadesColetadas.size() == 2
                        && quantidadesColetadas.get(0) == 1
                        && quantidadesColetadas.get(1) == 1,
                "O observador deveria informar os itens coletados em cada chegada.");
    }

    private static void testarRevisitaNaoDuplicaItens() {
        Grafo grafo = criarGrafoBase();
        grafo.getVertice("CID").adicionarItem(new Item("Pokébola"));
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        jornada.viajarPara(grafo.getVertice("GIN"));
        jornada.viajarPara(grafo.getVertice("LAB"));

        verificar(jornada.getInventario().size() == 1,
                "Revisitar um local não deveria duplicar itens já coletados.");
    }

    private static void testarInventarioNaoPermiteAlteracaoExterna() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);
        boolean alteracaoRejeitada = false;

        try {
            jornada.getInventario().add(new Item("Item externo"));
        } catch (UnsupportedOperationException e) {
            alteracaoRejeitada = true;
        }

        verificar(alteracaoRejeitada,
                "O inventário retornado não deveria permitir alterações externas.");
    }

    private static void testarRegistroDeInsigniasSemDuplicidade() {
        Grafo grafo = criarGrafoBase();
        JornadaPokemon jornada = criarJornada(grafo, 100L);

        verificar(jornada.registrarInsignia("ROCHA"),
                "A primeira insígnia deveria ser registrada.");
        verificar(!jornada.registrarInsignia("ROCHA"),
                "A mesma insígnia não deveria ser registrada duas vezes.");
        verificar(jornada.registrarInsignia("CASCATA"),
                "Uma insígnia diferente deveria ser registrada.");
        verificar(jornada.getInsignias().size() == 2,
                "A jornada deveria possuir duas insígnias distintas.");
        verificar(jornada.possuiInsignia("ROCHA"),
                "A consulta deveria encontrar a insígnia registrada.");

        Set<String> insignias = jornada.getInsignias();
        boolean alteracaoRejeitada = false;
        try {
            insignias.add("TROVAO");
        } catch (UnsupportedOperationException e) {
            alteracaoRejeitada = true;
        }
        verificar(alteracaoRejeitada,
                "O conjunto retornado não deveria permitir alterações externas.");
    }

    private static void testarInscricaoNaLigaExigeLocalQuantidadeEPrazo() {
        Grafo grafo = criarGrafoComEstadio();
        JornadaPokemon jornada = criarJornada(grafo, 100L);
        jornada.registrarInsignia("ROCHA");
        jornada.registrarInsignia("CASCATA");

        verificar(!jornada.podeSeInscreverNaLiga(2),
                "Fora do Estádio, a inscrição não deveria ser permitida.");

        jornada.viajarPara(grafo.getVertice("EST"));
        verificar(jornada.podeSeInscreverNaLiga(2),
                "No Estádio, com duas insígnias e dentro do prazo, a inscrição deveria ser permitida.");
        verificar(!jornada.podeSeInscreverNaLiga(3),
                "A inscrição não deveria ser permitida sem insígnias suficientes.");

        JornadaPokemon jornadaAtrasada = criarJornada(grafo, 20L);
        jornadaAtrasada.registrarInsignia("ROCHA");
        jornadaAtrasada.registrarInsignia("CASCATA");
        jornadaAtrasada.viajarPara(grafo.getVertice("EST"));

        verificar(!jornadaAtrasada.estaDentroDoPrazo(),
                "A viagem de 25 unidades deveria exceder o prazo de 20.");
        verificar(!jornadaAtrasada.podeSeInscreverNaLiga(2),
                "Fora do prazo, a inscrição não deveria ser permitida.");
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

    private static Grafo criarGrafoComEstadio() {
        Grafo grafo = criarGrafoBase();
        grafo.adicionarVertice("EST", "Estádio da Liga", TipoVertice.ESTADIO);
        grafo.adicionarAresta("GIN", "EST", 5);
        return grafo;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
