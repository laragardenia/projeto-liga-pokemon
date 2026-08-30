package algoritmos;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/** Valida a conexidade do mapa por meio de uma busca em profundidade. */
public class DFS {

    /**
     * Retorna verdadeiro quando todos os vértices pertencem ao mesmo
     * componente conexo. Complexidade O(n+m).
     */
    public boolean ehConexo(Grafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo é obrigatório.");
        }
        if (grafo.getQuantidadeVertices() == 0) {
            return false;
        }

        Vertice origem = grafo.getTodosVertices().iterator().next();
        ArrayDeque<Vertice> pilha = new ArrayDeque<>();
        Set<Vertice> visitados = new HashSet<>();
        pilha.push(origem);

        while (!pilha.isEmpty()) {
            Vertice atual = pilha.pop();
            if (!visitados.add(atual)) {
                continue;
            }

            for (Aresta aresta : grafo.getAdjacentes(atual)) {
                if (!visitados.contains(aresta.getDestino())) {
                    pilha.push(aresta.getDestino());
                }
            }
        }

        return visitados.size() == grafo.getQuantidadeVertices();
    }
}
