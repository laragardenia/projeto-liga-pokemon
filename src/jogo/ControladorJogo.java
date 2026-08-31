package jogo;

import algoritmos.BFS;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.OvoPokemon;
import modelo.Pokemon;
import modelo.TipoItem;
import modelo.TipoPokemon;
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
    private final List<Item> ovosAguardandoDecisao;
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
        this.ovosAguardandoDecisao = new ArrayList<>();
        registrarOvosEncontrados(jornada.getInventario());
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
        jogador.avancarIncubacao(tempoTrecho);
        registrarOvosEncontrados(itensColetados);
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
        jogador.avancarIncubacao(1);

        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE
                && adversario.isLiderGinasio()) {
            jornada.registrarInsignia(adversario.getCodigoInsignia());
        }
        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE) {
            jornada.getPosicaoAtual().removerTreinador(adversario);
            treinadoresDisponiveis.remove(adversario);
        } else {
            adversario.recuperarEquipe();
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
        jogador.avancarIncubacao(1);

        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE) {
            equipeRocket.derrotar();
            Vertice respawn = bfs.escolherRespawnDistante(grafo, pontoAtaque, random);
            treinadorRocket.recuperarEquipe();
            equipeRocket.reativarEm(respawn);
            encontroComRocket = false;
        } else {
            treinadorRocket.recuperarEquipe();
            
            //mecânica extra: roubo de insígnia em caso de derrota do jogador [adiconado por Lara]
            if (!jornada.getInsignias().isEmpty()) {
                String insigniaRoubada = jornada.getInsignias().iterator().next();
                jornada.removerInsignia(insigniaRoubada);
                System.out.println("A Equipe Rocket venceu e roubou sua insígnia: " + insigniaRoubada + "!");
            }  
        }
        return resultado;
    }

    public boolean usarItem(Item item) {
        return jornada.usarItem(item, jogador);
    }

    /**
     * Captura um Pokémon selvagem presente na posição atual. Uma Pokébola é
     * consumida somente quando a captura pode ser concluída e há espaço na
     * equipe do jogador.
     */
    public boolean capturarPokemon(Pokemon pokemon) {
        Vertice local = jornada.getPosicaoAtual();
        if (pokemon == null || !local.getPokemonsSelvagens().contains(pokemon)) {
            return false;
        }

        Item pokebola = localizarPrimeiroItem(TipoItem.POKEBOLA);
        if (pokebola == null || !jogador.adicionarPokemon(pokemon)) {
            return false;
        }

        jornada.descartarItem(pokebola);
        local.removerPokemon(pokemon);
        return true;
    }

    public int getQuantidadePokebolas() {
        int quantidade = 0;
        for (Item item : jornada.getInventario()) {
            if (item.getTipo() == TipoItem.POKEBOLA) {
                quantidade++;
            }
        }
        return quantidade;
    }

    /** Aceita um ovo encontrado sem revelar sua espécie antes de chocar. */
    public OvoPokemon aceitarOvo(Item itemOvo) {
        if (!ovosAguardandoDecisao.contains(itemOvo)) {
            return null;
        }

        OvoPokemon ovo = new OvoPokemon(criarEspecieInicialAleatoria());
        if (!jogador.aceitarOvo(ovo)) {
            return null;
        }

        ovosAguardandoDecisao.remove(itemOvo);
        jornada.descartarItem(itemOvo);
        return ovo;
    }

    /** Recusa o ovo e o remove dos itens pendentes da jornada. */
    public boolean recusarOvo(Item itemOvo) {
        if (!ovosAguardandoDecisao.remove(itemOvo)) {
            return false;
        }
        jornada.descartarItem(itemOvo);
        return true;
    }

    /** Inicia a incubação se o treinador possuir a incubadora inicial. */
    public boolean iniciarIncubacao(OvoPokemon ovo) {
        return jornada.possuiItem(TipoItem.INCUBADORA)
                && jogador.iniciarIncubacao(ovo);
    }

    public List<Item> getOvosAguardandoDecisao() {
        return Collections.unmodifiableList(new ArrayList<>(ovosAguardandoDecisao));
    }

    public List<OvoPokemon> getOvosAceitos() {
        return jogador.getOvos();
    }

    public Pokemon getRecemNascidoAguardandoEscolha() {
        return jogador.getPokemonAguardandoEscolha();
    }

    public boolean manterRecemNascidoNoLugarDe(Pokemon pokemonEnviadoAoProfessor) {
        return jogador.manterRecemNascidoNoLugarDe(pokemonEnviadoAoProfessor);
    }

    public boolean enviarRecemNascidoAoProfessor() {
        return jogador.enviarRecemNascidoAoProfessor();
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

    private void registrarOvosEncontrados(List<Item> itens) {
        for (Item item : itens) {
            if (item.getTipo() == TipoItem.OVO
                    && !ovosAguardandoDecisao.contains(item)) {
                ovosAguardandoDecisao.add(item);
            }
        }
    }

    private Pokemon criarEspecieInicialAleatoria() {
        TipoPokemon[] tipos = TipoPokemon.values();
        TipoPokemon tipo = tipos[random.nextInt(tipos.length)];
        String nome;

        switch (tipo) {
            case FOGO:
                nome = "Charmander";
                break;
            case AGUA:
                nome = "Squirtle";
                break;
            case PLANTA:
                nome = "Bulbasaur";
                break;
            case ELETRICO:
                nome = "Pikachu";
                break;
            case VENENOSO:
                nome = "Ekans";
                break;
            case FANTASMA:
                nome = "Gastly";
                break;
            default:
                nome = "Eevee";
        }

        int apInicial = 15 + random.nextInt(21);
        int dpInicial = 5 + random.nextInt(16);
        return new Pokemon(nome, tipo, apInicial, dpInicial);
    }

    private Item localizarPrimeiroItem(TipoItem tipo) {
        for (Item item : jornada.getInventario()) {
            if (item.getTipo() == tipo) {
                return item;
            }
        }
        return null;
    }

    private boolean batalhasProibidasNaPosicao() {
        return batalhasProibidas(jornada.getPosicaoAtual());
    }

    private boolean batalhasProibidas(Vertice local) {
        return local.getTipo() == TipoVertice.CARVALHO
                || local.getTipo() == TipoVertice.MCP;
    }
}
