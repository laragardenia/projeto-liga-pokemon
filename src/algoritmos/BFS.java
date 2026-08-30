package algoritmos;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BFS {

    /**
     * Executa a Busca em Largura (BFS) em tempo O(n+m).
     * Utilizada para calcular a distância topológica (camadas) ao definir
     * o local aleatório e distante de "respawn" da Equipe Rocket após serem derrotados.
     */
    public Map<Vertice, Integer> calcularDistancias(Grafo grafo, Vertice origemRespawn) {
        Map<Vertice, Integer> distancias = new HashMap<>();
        Queue<Vertice> fila = new LinkedList<>();

        fila.add(origemRespawn);
        distancias.put(origemRespawn, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            int distAtual = distancias.get(atual);

            // Obtém as arestas adjacentes usando o método implementado na classe Grafo
            for (Aresta aresta : grafo.getAdjacentes(atual)) {
                Vertice vizinho = aresta.getDestino();

                if (!distancias.containsKey(vizinho)) {
                    distancias.put(vizinho, distAtual + 1);
                    fila.add(vizinho);
                }
            }
        }
        return distancias;
    }
}