package algoritmos;

import grafo.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado produzido pelo Dijkstra.
 *
 * Guarda tanto a sequência de vértices percorridos quanto o tempo total da
 * rota. Para um destino inalcançável, o caminho é vazio e o tempo é -1.
 */
public class ResultadoCaminho {
    private final boolean alcancavel;
    private final List<Vertice> caminho;
    private final long tempoTotal;

    private ResultadoCaminho(boolean alcancavel, List<Vertice> caminho, long tempoTotal) {
        this.alcancavel = alcancavel;
        this.caminho = Collections.unmodifiableList(new ArrayList<Vertice>(caminho));
        this.tempoTotal = tempoTotal;
    }

    public static ResultadoCaminho alcancavel(List<Vertice> caminho, long tempoTotal) {
        return new ResultadoCaminho(true, caminho, tempoTotal);
    }

    public static ResultadoCaminho inalcancavel() {
        return new ResultadoCaminho(false, Collections.<Vertice>emptyList(), -1L);
    }

    public boolean isAlcancavel() {
        return alcancavel;
    }

    public List<Vertice> getCaminho() {
        return caminho;
    }

    public long getTempoTotal() {
        return tempoTotal;
    }
}
