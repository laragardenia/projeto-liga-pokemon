package simulacao;

import grafo.Vertice;
import modelo.Item;

import java.util.List;

/**
 * Ponto de integração executado após a chegada do jogador a cada vértice.
 *
 * Implementações futuras poderão verificar itens, encontros e batalhas sem
 * acoplar essas regras ao algoritmo de Dijkstra.
 */
@FunctionalInterface
public interface ObservadorJornada {
    void aoChegar(
            Vertice origem,
            Vertice destino,
            int tempoTrecho,
            long tempoDecorrido,
            List<Item> itensColetados);
}
