package algoritmos;

import grafo.Grafo;
import grafo.Vertice;
import grafo.Aresta;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//responsável por executar a busca em profundidade (DFS) para verificar
//se o mapa do jogo (grafo) é totalmente conexo antes de iniciar a jornada
public class DFS {

    private static final String BRANCO = "BRANCO"; //vértice não visitado
    private static final String CINZA = "CINZA";   //vértice visitado, mas não percorrido
    private static final String PRETO = "PRETO";   //vértice completamente percorrido

    private final Map<Vertice, String> marca; //vetor "marca" para guardar as cores dos vértices
    private final Stack<Vertice> pilhaP;      //pilha "P" utilizada no percurso

    public DFS() { //inicializa as estruturas do algoritmo DFS
        this.marca = new HashMap<>();
        this.pilhaP = new Stack<>();
    }

    //verifica se os vértices do mapa são alcançáveis (validação de conexidade)
    //complexidade : O(n + m), onde n é o número de vértices 
    // e m é o número de arestas do grafo
    public boolean verificarConexidade(Grafo grafo, Vertice inicial) {
        if (grafo == null || inicial == null) {
            return false;
        }

        //marca todos os vertices de branco
        for (Vertice v : grafo.getTodosVertices()) {
            marca.put(v, BRANCO);
        }

        pilhaP.clear(); //inicia pilha vazia

        marca.put(inicial, CINZA); //marcar de cinza ao visitar o vertice pela primeira vez

        visitar(grafo, inicial);

        //se após executar a visita e restar algum vértice "BRANCO", o mapa é desconexo
        for (Vertice v : grafo.getTodosVertices()) {
            if (marca.get(v).equals(BRANCO)) {
                return false; //mapa possui pontos isolados (inválido para o jogo)
            }
        }

        return true; //mapa conexo
    }

    //algoritmo "Visita" que explora a vizinhança de um vértice
    //complexidade: O(d(v)) para cada chamada individual, onde d(v)
    //representa o grau (número de vizinhos) do vértice visitado
    private void visitar(Grafo grafo, Vertice v) {
       
        pilhaP.push(v);//insere v em P
        //percorrer a lista de adjacências
        for (Aresta aresta : grafo.getAdjacentes(v)) {
            Vertice u = aresta.getDestino(); //'u' é o vizinho atual de 'v'

            if (marca.get(u).equals(BRANCO)) {

                marca.put(u, CINZA);

                visitar(grafo, u);
            }
        }

        //emove v de P;
        if (!pilhaP.isEmpty()) {
            pilhaP.pop();
        }

        //v foi totalmente percorrido
        marca.put(v, PRETO);
    }
}
