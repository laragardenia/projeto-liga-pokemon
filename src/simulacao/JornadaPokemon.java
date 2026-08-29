package simulacao;

import algoritmos.Dijkstra;
import algoritmos.ResultadoCaminho;
import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;
import modelo.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controla o deslocamento do jogador pelo mapa e o tempo da jornada.
 *
 * A jornada também coleta os itens disponíveis em cada local visitado. Regras
 * de uso dos itens, batalhas e Pokémon permanecem fora desta classe.
 */
public class JornadaPokemon {
    private final Grafo grafo;
    private final Dijkstra dijkstra;
    private final long prazoLiga;
    private final List<Vertice> locaisVisitados;
    private final List<Item> inventario;
    private final List<ObservadorJornada> observadores;

    private Vertice posicaoAtual;
    private long tempoDecorrido;

    public JornadaPokemon(Grafo grafo, Vertice posicaoInicial, long prazoLiga) {
        if (grafo == null || posicaoInicial == null) {
            throw new IllegalArgumentException("Grafo e posição inicial são obrigatórios.");
        }

        Vertice posicaoNoGrafo = grafo.getVertice(posicaoInicial.getId());
        if (posicaoNoGrafo == null) {
            throw new IllegalArgumentException("A posição inicial deve pertencer ao grafo.");
        }

        if (prazoLiga < 0) {
            throw new IllegalArgumentException("O prazo da Liga não pode ser negativo.");
        }

        this.grafo = grafo;
        this.dijkstra = new Dijkstra();
        this.posicaoAtual = posicaoNoGrafo;
        this.prazoLiga = prazoLiga;
        this.tempoDecorrido = 0L;
        this.locaisVisitados = new ArrayList<Vertice>();
        this.locaisVisitados.add(posicaoNoGrafo);
        this.inventario = new ArrayList<Item>();
        this.observadores = new ArrayList<ObservadorJornada>();
        coletarItensDisponiveis(posicaoNoGrafo);
    }

    /** Calcula a melhor rota sem movimentar o jogador ou alterar o relógio. */
    public ResultadoCaminho planejarRota(Vertice destino) {
        return dijkstra.calcularMenorCaminho(grafo, posicaoAtual, destino);
    }

    /**
     * Calcula e percorre a melhor rota até o destino.
     *
     * O deslocamento ocorre aresta por aresta para permitir que encontros e
     * outros eventos sejam incorporados posteriormente em percorrerTrecho.
     */
    public ResultadoCaminho viajarPara(Vertice destino) {
        ResultadoCaminho resultado = planejarRota(destino);

        if (!resultado.isAlcancavel()) {
            return resultado;
        }

        for (int i = 1; i < resultado.getCaminho().size(); i++) {
            Vertice origemTrecho = resultado.getCaminho().get(i - 1);
            Vertice destinoTrecho = resultado.getCaminho().get(i);
            percorrerTrecho(origemTrecho, destinoTrecho);
        }

        return resultado;
    }

    private void percorrerTrecho(Vertice origem, Vertice destino) {
        int pesoTrecho = buscarMenorPeso(origem, destino);
        tempoDecorrido += pesoTrecho;
        posicaoAtual = destino;
        locaisVisitados.add(destino);
        List<Item> itensColetados = coletarItensDisponiveis(destino);
        notificarChegada(origem, destino, pesoTrecho, itensColetados);
    }

    private List<Item> coletarItensDisponiveis(Vertice local) {
        List<Item> itensColetados = new ArrayList<Item>(local.getItensDisponiveis());

        for (Item item : itensColetados) {
            inventario.add(item);
            local.removerItem(item);
        }

        return Collections.unmodifiableList(itensColetados);
    }

    private void notificarChegada(
            Vertice origem,
            Vertice destino,
            int tempoTrecho,
            List<Item> itensColetados) {
        for (ObservadorJornada observador : observadores) {
            observador.aoChegar(
                    origem,
                    destino,
                    tempoTrecho,
                    tempoDecorrido,
                    itensColetados);
        }
    }

    private int buscarMenorPeso(Vertice origem, Vertice destino) {
        int menorPeso = Integer.MAX_VALUE;

        for (Aresta aresta : grafo.getAdjacentes(origem)) {
            if (aresta.getDestino().equals(destino)
                    && aresta.getPesoTempo() < menorPeso) {
                menorPeso = aresta.getPesoTempo();
            }
        }

        if (menorPeso == Integer.MAX_VALUE) {
            throw new IllegalStateException("A rota contém um trecho inexistente no grafo.");
        }

        return menorPeso;
    }

    public Vertice getPosicaoAtual() {
        return posicaoAtual;
    }

    public long getTempoDecorrido() {
        return tempoDecorrido;
    }

    public long getPrazoLiga() {
        return prazoLiga;
    }

    /**
     * Retorna uma cópia imutável do histórico, incluindo a posição inicial e
     * todas as chegadas posteriores. Revisitas são mantidas na lista.
     */
    public List<Vertice> getLocaisVisitados() {
        return Collections.unmodifiableList(new ArrayList<Vertice>(locaisVisitados));
    }

    /** Retorna uma cópia imutável dos itens coletados durante a jornada. */
    public List<Item> getInventario() {
        return Collections.unmodifiableList(new ArrayList<Item>(inventario));
    }

    public void adicionarObservador(ObservadorJornada observador) {
        if (observador == null) {
            throw new IllegalArgumentException("O observador da jornada não pode ser nulo.");
        }
        observadores.add(observador);
    }

    public boolean removerObservador(ObservadorJornada observador) {
        return observadores.remove(observador);
    }

    public long getTempoRestante() {
        return Math.max(0L, prazoLiga - tempoDecorrido);
    }

    /** O instante exato do prazo ainda é considerado válido. */
    public boolean estaDentroDoPrazo() {
        return tempoDecorrido <= prazoLiga;
    }
}
