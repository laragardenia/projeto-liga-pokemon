package teste;

import algoritmos.Dijkstra;
import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;

import java.util.List;

/** Testes executáveis sem dependências externas. */
public class TesteDijkstra {
    public static void main(String[] args) {
        testarMenorCaminho();
        testarOrigemIgualAoDestino();
        testarDestinoInalcancavel();
        testarRejeicaoDePesoNegativo();

        System.out.println("Todos os testes do Dijkstra passaram.");
    }

    private static void testarMenorCaminho() {
        Grafo grafo = criarGrafoBase();
        Dijkstra dijkstra = new Dijkstra();

        ResultadoCaminho resultado = dijkstra.calcularMenorCaminho(
                grafo,
                grafo.getVertice("LAB"),
                grafo.getVertice("GIN"));

        verificar(resultado.isAlcancavel(), "O ginásio deveria ser alcançável.");
        verificar(resultado.getTempoTotal() == 20L, "O menor tempo deveria ser 20.");
        verificarIds(resultado.getCaminho(), "LAB", "CID", "GIN");
    }

    private static void testarOrigemIgualAoDestino() {
        Grafo grafo = criarGrafoBase();
        Vertice laboratorio = grafo.getVertice("LAB");

        ResultadoCaminho resultado = new Dijkstra().calcularMenorCaminho(
                grafo, laboratorio, laboratorio);

        verificar(resultado.isAlcancavel(), "A origem deveria alcançar a si mesma.");
        verificar(resultado.getTempoTotal() == 0L, "O tempo deveria ser zero.");
        verificarIds(resultado.getCaminho(), "LAB");
    }

    private static void testarDestinoInalcancavel() {
        Grafo grafo = criarGrafoBase();
        grafo.adicionarVertice("EST", "Estádio", TipoVertice.ESTADIO);

        ResultadoCaminho resultado = new Dijkstra().calcularMenorCaminho(
                grafo,
                grafo.getVertice("LAB"),
                grafo.getVertice("EST"));

        verificar(!resultado.isAlcancavel(), "O estádio deveria estar inalcançável.");
        verificar(resultado.getCaminho().isEmpty(), "O caminho deveria estar vazio.");
        verificar(resultado.getTempoTotal() == -1L, "O tempo deveria ser -1.");
    }

    private static void testarRejeicaoDePesoNegativo() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("A", "A", TipoVertice.CARVALHO);
        grafo.adicionarVertice("B", "B", TipoVertice.GINASIO);
        grafo.adicionarAresta("A", "B", -1);

        boolean rejeitou = false;
        try {
            new Dijkstra().calcularMenorCaminho(
                    grafo,
                    grafo.getVertice("A"),
                    grafo.getVertice("B"));
        } catch (IllegalArgumentException excecao) {
            rejeitou = true;
        }

        verificar(rejeitou, "Uma aresta negativa deveria ser rejeitada.");
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

    private static void verificarIds(List<Vertice> caminho, String... idsEsperados) {
        verificar(caminho.size() == idsEsperados.length,
                "O caminho possui uma quantidade inesperada de vértices.");

        for (int i = 0; i < idsEsperados.length; i++) {
            verificar(caminho.get(i).getId().equals(idsEsperados[i]),
                    "Vértice inesperado na posição " + i + ".");
        }
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
