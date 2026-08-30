package algoritmos;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class BFS {

    /**
     * Executa a Busca em Largura (BFS) em tempo O(n+m).
     * Utilizada para calcular a distância topológica (camadas) ao definir
     * o local aleatório e distante de "respawn" da Equipe Rocket após serem derrotados.
     */
    public Map<Vertice, Integer> calcularDistancias(Grafo grafo, Vertice origemRespawn) {
        validarEntrada(grafo, origemRespawn);

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

    /**
     * Escolhe aleatoriamente um dos vértices da maior camada alcançável pela
     * BFS. A busca e a seleção custam O(n+m).
     */
    public Vertice escolherRespawnDistante(Grafo grafo, Vertice origemRespawn) {
        return escolherRespawnDistante(grafo, origemRespawn, new Random());
    }

    /** Sobrecarga que permite controlar a aleatoriedade durante os testes. */
    public Vertice escolherRespawnDistante(
            Grafo grafo,
            Vertice origemRespawn,
            Random random) {
        if (random == null) {
            throw new IllegalArgumentException("A fonte de aleatoriedade é obrigatória.");
        }

        Map<Vertice, Integer> distancias = calcularDistancias(grafo, origemRespawn);
        int maiorDistancia = 0;

        for (int distancia : distancias.values()) {
            maiorDistancia = Math.max(maiorDistancia, distancia);
        }

        List<Vertice> candidatos = new ArrayList<>();
        for (Map.Entry<Vertice, Integer> entrada : distancias.entrySet()) {
            if (entrada.getValue() == maiorDistancia) {
                candidatos.add(entrada.getKey());
            }
        }

        return candidatos.get(random.nextInt(candidatos.size()));
    }

    private void validarEntrada(Grafo grafo, Vertice origem) {
        if (grafo == null || origem == null) {
            throw new IllegalArgumentException("Grafo e origem são obrigatórios.");
        }
        if (grafo.getVertice(origem.getId()) == null) {
            throw new IllegalArgumentException("A origem deve pertencer ao grafo.");
        }
    }
}
