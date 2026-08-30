package simulacao;

import algoritmos.Dijkstra;
import algoritmos.ResultadoCaminho;
import grafo.Aresta;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.Treinador;
import modelo.TipoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Controla o deslocamento do jogador pelo mapa e o tempo da jornada.
 *
 * A jornada também coleta os itens disponíveis em cada local visitado. Regras
 * de uso dos itens, batalhas e Pokémon permanecem fora desta classe.
 */
public class JornadaPokemon {
    public static final int INSIGNIAS_NECESSARIAS_PARA_LIGA = 8;

    private final Grafo grafo;
    private final Dijkstra dijkstra;
    private final long prazoLiga;
    private final List<Vertice> locaisVisitados;
    private final List<Item> inventario;
    private final Set<String> insignias;
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
        this.insignias = new LinkedHashSet<String>();
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

    /**
     * Registra uma insígnia já conquistada por outro módulo. Retorna falso
     * quando ela já havia sido registrada.
     */
    public boolean registrarInsignia(String codigoInsignia) {
        if (codigoInsignia == null || codigoInsignia.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da insígnia é obrigatório.");
        }
        return insignias.add(codigoInsignia.trim());
    }

    public Set<String> getInsignias() {
        return Collections.unmodifiableSet(new LinkedHashSet<String>(insignias));
    }

    public boolean possuiInsignia(String codigoInsignia) {
        if (codigoInsignia == null) {
            return false;
        }
        return insignias.contains(codigoInsignia.trim());
    }

    /**
     * A inscrição exige presença no Estádio, insígnias suficientes e prazo
     * ainda válido. A quantidade exigida é configurada por quem inicia o jogo.
     */
    public boolean podeSeInscreverNaLiga(int quantidadeInsigniasNecessarias) {
        if (quantidadeInsigniasNecessarias <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de insígnias necessária deve ser positiva.");
        }

        return posicaoAtual.getTipo() == TipoVertice.ESTADIO
                && insignias.size() >= quantidadeInsigniasNecessarias
                && estaDentroDoPrazo();
    }

    /** Aplica diretamente a exigência oficial de oito insígnias distintas. */
    public boolean podeSeInscreverNaLiga() {
        return podeSeInscreverNaLiga(INSIGNIAS_NECESSARIAS_PARA_LIGA);
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

    /** Registra a unidade de tempo consumida por uma batalha. */
    public void registrarTempoBatalha() {
        tempoDecorrido++;
    }

    /**
     * Usa um item coletado. A erva é removida do inventário depois de
     * recuperar os Pokémon conscientes do treinador.
     */
    public boolean usarItem(Item item, Treinador treinador) {
        if (item == null || !inventario.contains(item)) {
            return false;
        }
        if (!item.aplicarEm(treinador)) {
            return false;
        }
        inventario.remove(item);
        return true;
    }

    public boolean possuiItem(TipoItem tipo) {
        if (tipo == null) {
            return false;
        }
        for (Item item : inventario) {
            if (item.getTipo() == tipo) {
                return true;
            }
        }
        return false;
    }

    /** Remove um item aceito, recusado ou descartado do inventário. */
    public boolean descartarItem(Item item) {
        return inventario.remove(item);
    }

    public long getTempoRestante() {
        return Math.max(0L, prazoLiga - tempoDecorrido);
    }

    /** O instante exato do prazo ainda é considerado válido. */
    public boolean estaDentroDoPrazo() {
        return tempoDecorrido <= prazoLiga;
    }
}
