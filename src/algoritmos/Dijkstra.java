package algoritmos;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Calcula caminhos de menor tempo em grafos com pesos não negativos. */
public class Dijkstra {
    private static final long INFINITO = Long.MAX_VALUE;

    /**
     * Calcula o caminho de menor tempo entre a origem e o destino.
     *
     * Complexidade: O((n + m) log n), usando lista de adjacências e uma fila
     * de prioridade, onde n é o número de vértices e m é o número de arestas.
     */
    public ResultadoCaminho calcularMenorCaminho(
            Grafo grafo,
            Vertice origem,
            Vertice destino) {

        validarEntrada(grafo, origem, destino);
        validarPesos(grafo);

        Map<Vertice, Long> distancias = new HashMap<Vertice, Long>();
        Map<Vertice, Vertice> predecessores = new HashMap<Vertice, Vertice>();
        PriorityQueue<NoFila> fila = new PriorityQueue<NoFila>();

        for (Vertice vertice : grafo.getTodosVertices()) {
            distancias.put(vertice, INFINITO);
        }

        distancias.put(origem, 0L);
        fila.add(new NoFila(origem, 0L));

        while (!fila.isEmpty()) {
            NoFila atual = fila.poll();

            if (atual.distancia != distancias.get(atual.vertice)) {
                continue;
            }

            if (atual.vertice.equals(destino)) {
                break;
            }

            for (Aresta aresta : grafo.getAdjacentes(atual.vertice)) {
                Vertice vizinho = aresta.getDestino();
                long novaDistancia = atual.distancia + aresta.getPesoTempo();

                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, atual.vertice);
                    fila.add(new NoFila(vizinho, novaDistancia));
                }
            }
        }

        if (distancias.get(destino) == INFINITO) {
            return ResultadoCaminho.inalcancavel();
        }

        List<Vertice> caminho = reconstruirCaminho(predecessores, destino);
        return ResultadoCaminho.alcancavel(caminho, distancias.get(destino));
    }

    private void validarEntrada(Grafo grafo, Vertice origem, Vertice destino) {
        if (grafo == null || origem == null || destino == null) {
            throw new IllegalArgumentException("Grafo, origem e destino são obrigatórios.");
        }

        if (grafo.getVertice(origem.getId()) == null
                || grafo.getVertice(destino.getId()) == null) {
            throw new IllegalArgumentException("Origem e destino devem pertencer ao grafo.");
        }
    }

    private void validarPesos(Grafo grafo) {
        for (Vertice vertice : grafo.getTodosVertices()) {
            for (Aresta aresta : grafo.getAdjacentes(vertice)) {
                if (aresta.getPesoTempo() < 0) {
                    throw new IllegalArgumentException(
                            "Dijkstra não aceita arestas com peso negativo.");
                }
            }
        }
    }

    private List<Vertice> reconstruirCaminho(
            Map<Vertice, Vertice> predecessores,
            Vertice destino) {

        List<Vertice> caminho = new ArrayList<Vertice>();
        Vertice atual = destino;

        while (atual != null) {
            caminho.add(atual);
            atual = predecessores.get(atual);
        }

        Collections.reverse(caminho);
        return caminho;
    }

    private static class NoFila implements Comparable<NoFila> {
        private final Vertice vertice;
        private final long distancia;

        private NoFila(Vertice vertice, long distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NoFila outro) {
            return Long.compare(this.distancia, outro.distancia);
        }
    }
}
