package grafo;

//armazena a origem, o destino e o tempo de viagem (peso não-negativo)

public class Aresta {
    private final Vertice origem;
    private final Vertice destino;
    private final int pesoTempo; //representa o tempo de percurso

    //construtor para inicializar uma aresta ponderada
    public Aresta(Vertice origem, Vertice destino, int pesoTempo) {
        this.origem = origem;
        this.destino = destino;
        this.pesoTempo = pesoTempo; //tempo necessário para percorrer o caminho (não-negativo)
    }

    public Vertice getOrigem() { return origem; }
    public Vertice getDestino() { return destino; }
    public int getPesoTempo() { return pesoTempo; }
}