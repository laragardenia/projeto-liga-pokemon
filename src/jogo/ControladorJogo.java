package jogo;

import algoritmos.BFS;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.Treinador;
import simulacao.EquipeRocket;
import simulacao.JornadaPokemon;
import simulacao.ObservadorJornada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Conecta a jornada aos encontros, às batalhas e à Equipe Rocket. */
public class ControladorJogo implements ObservadorJornada {
    private final Grafo grafo;
    private final JornadaPokemon jornada;
    private final Treinador jogador;
    private final EquipeRocket equipeRocket;
    private final Treinador treinadorRocket;
    private final SistemaBatalha sistemaBatalha;
    private final BFS bfs;
    private final Random random;

    private List<Treinador> treinadoresDisponiveis;
    private boolean encontroComRocket;

    public ControladorJogo(
            Grafo grafo,
            JornadaPokemon jornada,
            Treinador jogador,
            EquipeRocket equipeRocket,
            Treinador treinadorRocket) {
        this(
                grafo,
                jornada,
                jogador,
                equipeRocket,
                treinadorRocket,
                new SistemaBatalha(),
                new BFS(),
                new Random());
    }

    /** Construtor com dependências controláveis para testes determinísticos. */
    public ControladorJogo(
            Grafo grafo,
            JornadaPokemon jornada,
            Treinador jogador,
            EquipeRocket equipeRocket,
            Treinador treinadorRocket,
            SistemaBatalha sistemaBatalha,
            BFS bfs,
            Random random) {
        if (grafo == null || jornada == null || jogador == null
                || equipeRocket == null || treinadorRocket == null
                || sistemaBatalha == null || bfs == null || random == null) {
            throw new IllegalArgumentException("As dependências do controlador são obrigatórias.");
        }
        this.grafo = grafo;
        this.jornada = jornada;
        this.jogador = jogador;
        this.equipeRocket = equipeRocket;
        this.treinadorRocket = treinadorRocket;
        this.sistemaBatalha = sistemaBatalha;
        this.bfs = bfs;
        this.random = random;
        this.treinadoresDisponiveis = new ArrayList<>();
        atualizarEncontros(jornada.getPosicaoAtual());
    }

    /** Registra os encontros possíveis depois de cada trecho percorrido. */
    @Override
    public void aoChegar(
            Vertice origem,
            Vertice destino,
            int tempoTrecho,
            long tempoDecorrido,
            List<Item> itensColetados) {
        atualizarEncontros(destino);
    }

    /**
     * Batalha contra um treinador presente no mesmo vértice. Em caso de
     * vitória contra líder, registra permanentemente a insígnia.
     */
    public ResultadoBatalha batalharContra(Treinador adversario) {
        if (batalhasProibidasNaPosicao()) {
            throw new IllegalStateException("Batalhas são proibidas no MCP e no laboratório.");
        }
        if (!treinadoresDisponiveis.contains(adversario)) {
            throw new IllegalArgumentException("O treinador não está na posição atual.");
        }

        ResultadoBatalha resultado = sistemaBatalha.batalhar(jogador, adversario);
        jornada.registrarTempoBatalha();

        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE
                && adversario.isLiderGinasio()) {
            jornada.registrarInsignia(adversario.getCodigoInsignia());
        }
        return resultado;
    }

    /**
     * Resolve o duelo com a Rocket. Quando ela perde, a BFS sorteia um dos
     * vértices da camada mais distante e a reativa nesse ponto.
     */
    public ResultadoBatalha batalharContraRocket() {
        if (batalhasProibidasNaPosicao()) {
            throw new IllegalStateException("Batalhas são proibidas no MCP e no laboratório.");
        }
        if (!encontroComRocket) {
            throw new IllegalStateException("A Equipe Rocket não está na posição atual.");
        }

        Vertice pontoAtaque = jornada.getPosicaoAtual();
        ResultadoBatalha resultado = sistemaBatalha.batalhar(jogador, treinadorRocket);
        jornada.registrarTempoBatalha();

        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE) {
            equipeRocket.derrotar();
            Vertice respawn = bfs.escolherRespawnDistante(grafo, pontoAtaque, random);
            treinadorRocket.recuperarEquipe();
            equipeRocket.reativarEm(respawn);
            encontroComRocket = false;
        }
        return resultado;
    }

    public boolean usarItem(Item item) {
        return jornada.usarItem(item, jogador);
    }

    public List<Treinador> getTreinadoresDisponiveis() {
        return Collections.unmodifiableList(new ArrayList<>(treinadoresDisponiveis));
    }

    public boolean temEncontroComRocket() {
        return encontroComRocket;
    }

    private void atualizarEncontros(Vertice local) {
        if (batalhasProibidas(local)) {
            treinadoresDisponiveis = new ArrayList<>();
            encontroComRocket = false;
            return;
        }

        treinadoresDisponiveis = new ArrayList<>(local.getTreinadoresPresentes());
        encontroComRocket = equipeRocket.isAtiva()
                && equipeRocket.getPosicaoAtual().equals(local);
    }

    private boolean batalhasProibidasNaPosicao() {
        return batalhasProibidas(jornada.getPosicaoAtual());
    }

    private boolean batalhasProibidas(Vertice local) {
        return local.getTipo() == TipoVertice.CARVALHO
                || local.getTipo() == TipoVertice.MCP;
    }
}
