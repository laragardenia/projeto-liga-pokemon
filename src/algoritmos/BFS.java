package algoritmos;
import java.util.*;

public class BFS {

    /**
     * Executa a Busca em Largura (BFS) em tempo O(n+m).
     * Utilizada para calcular a distância topológica (camadas) e definir
     * o local distante de respawn da Equipe Rocket após serem derrotados.
     */
    public Map<Integer, Integer> calcularDistancias(Map<Integer, List<Integer>> grafoListaAdjacencia, int origemRespawn) {
        Map<Integer, Integer> distancias = new HashMap<>();
        Queue<Integer> fila = new LinkedList<>();

        fila.add(origemRespawn);
        distancias.put(origemRespawn, 0);

        while (!fila.isEmpty()) {
            int atual = fila.poll();
            int distAtual = distancias.get(atual);

            if (grafoListaAdjacencia.containsKey(atual)) {
                for (int vizinho : grafoListaAdjacencia.get(atual)) {
                    if (!distancias.containsKey(vizinho)) {
                        distancias.put(vizinho, distAtual + 1);
                        fila.add(vizinho);
                    }
                }
            }
        }
        return distancias;
    }
}