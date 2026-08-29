package simulacao;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Controla somente a posição e o estado da Equipe Rocket no mapa. */
public class EquipeRocket {
    private final Grafo grafo;

    private Vertice posicaoAtual;
    private boolean ativa;

    public EquipeRocket(Grafo grafo, Vertice posicaoInicial) {
        if (grafo == null || posicaoInicial == null) {
            throw new IllegalArgumentException("Grafo e posição inicial são obrigatórios.");
        }

        this.grafo = grafo;
        this.posicaoAtual = obterVerticeDoGrafo(posicaoInicial);
        this.ativa = true;
    }

    /** Move a Rocket por uma única aresta do grafo. */
    public void moverPara(Vertice destino) {
        if (!ativa) {
            throw new IllegalStateException("A Equipe Rocket derrotada não pode se mover.");
        }

        Vertice destinoNoGrafo = obterVerticeDoGrafo(destino);
        if (!ehAdjacente(posicaoAtual, destinoNoGrafo)) {
            throw new IllegalArgumentException(
                    "O destino do movimento deve ser adjacente à posição atual.");
        }

        posicaoAtual = destinoNoGrafo;
    }

    public boolean podeMoverPara(Vertice destino) {
        if (!ativa || destino == null) {
            return false;
        }

        Vertice destinoNoGrafo = grafo.getVertice(destino.getId());
        return destinoNoGrafo != null && ehAdjacente(posicaoAtual, destinoNoGrafo);
    }

    /** Retorna os destinos de um único passo, sem duplicatas. */
    public List<Vertice> getDestinosPossiveis() {
        Set<Vertice> destinos = new LinkedHashSet<Vertice>();

        if (ativa) {
            for (Aresta aresta : grafo.getAdjacentes(posicaoAtual)) {
                destinos.add(aresta.getDestino());
            }
        }

        return Collections.unmodifiableList(new ArrayList<Vertice>(destinos));
    }

    /** Marca a Rocket como derrotada, mantendo sua última posição registrada. */
    public void derrotar() {
        ativa = false;
    }

    /**
     * Reativa a Rocket no vértice escolhido pelo módulo externo de respawn.
     * Este método não calcula o local e, portanto, não implementa a BFS.
     */
    public void reativarEm(Vertice destinoRespawn) {
        posicaoAtual = obterVerticeDoGrafo(destinoRespawn);
        ativa = true;
    }

    private Vertice obterVerticeDoGrafo(Vertice vertice) {
        if (vertice == null) {
            throw new IllegalArgumentException("O vértice é obrigatório.");
        }

        Vertice verticeNoGrafo = grafo.getVertice(vertice.getId());
        if (verticeNoGrafo == null) {
            throw new IllegalArgumentException("O vértice deve pertencer ao grafo.");
        }
        return verticeNoGrafo;
    }

    private boolean ehAdjacente(Vertice origem, Vertice destino) {
        for (Aresta aresta : grafo.getAdjacentes(origem)) {
            if (aresta.getDestino().equals(destino)) {
                return true;
            }
        }
        return false;
    }

    public Vertice getPosicaoAtual() {
        return posicaoAtual;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
