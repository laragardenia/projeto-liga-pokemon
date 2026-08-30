package grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

//uso de coleções nativas do java para otimização de busca e inserção
//a complexidade de espaço linear em O(n + m)

public class Grafo {

    //tabela de dispersão para mapear ID (string) ao objeto Vertice em tempo O(1)
    private final Map<String, Vertice> vertices;

    //lista de adjacências: associa cada objeto Vertice a sua lista de arestas incidentes
    private final Map<Vertice, List<Aresta>> adjacencias;

    //contador de arestas únicas (bidirecionais) do grafo
    private int quantidadeArestas;

    //construtor para inicializar as estruturas de dados do grafo
    public Grafo() {
        this.vertices = new HashMap<>();
        this.adjacencias = new HashMap<>();
        this.quantidadeArestas = 0;
    }

    //adiciona um novo vértice ao grafo, caso ele ainda nao exista
    //complexidade: O(1)
    public void adicionarVertice(String id, String nome, TipoVertice tipo) {
        if (!vertices.containsKey(id)) {
            Vertice novo = new Vertice(id, nome, tipo);
            vertices.put(id, novo);
            adjacencias.put(novo, new ArrayList<>());
        }
    }

    //sobrecarga para permitir adicionar um objeto Vertice que já foi instanciado
    //útil durante o parsing no LeitorArquivo
    //complexidade: O(1)
    public void adicionarVertice(Vertice vertice) {
        if (vertice != null && !vertices.containsKey(vertice.getId())) {
            vertices.put(vertice.getId(), vertice);
            adjacencias.put(vertice, new ArrayList<>());
        }
    }

    //adiciona uma rota bidirecional ponderada (tempo de percurso) entre dois vértices
    //como o mapa do RPG é não-direcionado, adiciona a conexão de ida (u para v)
    //e a conexão de volta (v para u) nas respectivas listas de adjacência
    //complexidade: O(1)
    public void adicionarAresta(String idOrigem, String idDestino, int pesoTempo) {
        Vertice u = vertices.get(idOrigem);
        Vertice v = vertices.get(idDestino);

        if (u != null && v != null) {
            //criação das conexões direcionadas equivalentes a rota bidirecional
            Aresta ida = new Aresta(u, v, pesoTempo);
            Aresta volta = new Aresta(v, u, pesoTempo);

            //adiciona as arestas nas listas de adjacência de cada extremidade
            adjacencias.get(u).add(ida);
            adjacencias.get(v).add(volta);
            
            quantidadeArestas++;
        }
    }

    //calcula a soma total dos pesos (tempo de viagem) de todas as arestas unicas do grafo
    //uso para calcular o limite de tempo global da jornada (entre 10x e 15x a soma dos pesos)
    //complexidade: O(V + E)
    public int getSomaTotalPesos() {
        int somaTotal = 0;
        for (List<Aresta> lista : adjacencias.values()) {
            for (Aresta a : lista) {
                somaTotal += a.getPesoTempo();
            }
        }
        //como o grafo é não-direcionado, cada aresta foi inserida duas vezes (ida e volta)
        //é dividido por 2 para retornar apenas a soma das arestas unicas
        return somaTotal / 2;
    }

    //recuperar o objeto Vertice associado a um determinado ID
    //util para o motor de busca e para o leitor de arquivos
    //complexidade: O(1)
    public Vertice getVertice(String id) {
        return vertices.get(id);
    }

    //retorna a lista de arestas incidentes (vizinhos) de um determinado vértice,
    //sendo v o vertice que quer obter as adjcências
    //complexidade: O(1) para retornar a referência da lista
    public List<Aresta> getAdjacentes(Vertice v) {
        return adjacencias.getOrDefault(v, new ArrayList<>());
    }

    //retorna todos os vértices cadastrados no grafo
    //usado para os percursos (DFS/BFS) realizarem verificações e limpezas globais de marcação
    //complexidade: O(1) para ter a coleção interna
    public Collection<Vertice> getTodosVertices() {
        return vertices.values();
    }

    //retorna a quantidade total de vértices (n) no grafo
    //complexidade: O(1)
    public int getQuantidadeVertices() {
        return vertices.size();
    }

    //retorna a quantidade total de conexões bidirecionais (m) no grafo
    //complexidade: O(1)
    public int getQuantidadeArestas() {
        return quantidadeArestas;
    }
}