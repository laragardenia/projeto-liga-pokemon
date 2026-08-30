package modelo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Treinador {
    public static final int MAXIMO_POKEMONS_ATIVOS = 6;
    public static final int MAXIMO_TOTAL_COM_OVOS = 7;

    private final String nome;
    private int xp;
    private final List<Pokemon> equipe;
    private final List<OvoPokemon> ovos;
    private final String codigoInsignia;
    private OvoPokemon ovoEmIncubacao;
    private Pokemon pokemonAguardandoEscolha;

    public Treinador(String nome) {
        this(nome, null);
    }

    public Treinador(String nome, String codigoInsignia) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do treinador é obrigatório.");
        }
        this.nome = nome;
        this.xp = 0;
        this.equipe = new ArrayList<>();
        this.ovos = new ArrayList<>();
        this.codigoInsignia = normalizarCodigo(codigoInsignia);
        this.ovoEmIncubacao = null;
        this.pokemonAguardandoEscolha = null;
    }

    public boolean podeBatalhar() {
        int conscientes = 0;
        for (Pokemon p : equipe) {
            if (p.isConsciente()) conscientes++;
        }
        return conscientes >= 3; // Mínimo de 3 conscientes para batalha
    }

    public boolean adicionarPokemon(Pokemon p) {
        if (p == null
                || equipe.size() >= MAXIMO_POKEMONS_ATIVOS
                || getTotalPokemonsEOvos() >= MAXIMO_TOTAL_COM_OVOS) {
            return false;
        }
        equipe.add(p);
        return true;
    }

    public String getNome() { return nome; }
    public int getXp() { return xp; }
    public void adicionarXp(int ganho) {
        if (ganho < 0) {
            throw new IllegalArgumentException("O ganho de XP não pode ser negativo.");
        }
        this.xp += ganho;
    }
    public List<Pokemon> getEquipe() {
        return Collections.unmodifiableList(new ArrayList<>(equipe));
    }

    public List<Pokemon> getPokemonsConscientes() {
        List<Pokemon> conscientes = new ArrayList<>();
        for (Pokemon pokemon : equipe) {
            if (pokemon.isConsciente()) {
                conscientes.add(pokemon);
            }
        }
        return conscientes;
    }

    /** Aceita um ovo enquanto o total de Pokémon e ovos for menor que sete. */
    public boolean aceitarOvo(OvoPokemon ovo) {
        if (ovo == null || ovo.isChocado()
                || getTotalPokemonsEOvos() >= MAXIMO_TOTAL_COM_OVOS) {
            return false;
        }
        ovos.add(ovo);
        return true;
    }

    /** Inicia a incubação de um ovo; apenas um pode progredir por vez. */
    public boolean iniciarIncubacao(OvoPokemon ovo) {
        if (ovo == null || !ovos.contains(ovo) || ovoEmIncubacao != null) {
            return false;
        }
        ovo.iniciarIncubacao();
        ovoEmIncubacao = ovo;
        return true;
    }

    /**
     * Atualiza a incubação. Se houver vaga, o recém-nascido entra diretamente
     * na equipe; caso contrário, aguarda uma escolha do treinador.
     */
    public Pokemon avancarIncubacao(int unidades) {
        if (unidades < 0) {
            throw new IllegalArgumentException("O tempo percorrido não pode ser negativo.");
        }
        if (ovoEmIncubacao == null) {
            return null;
        }

        Pokemon recemNascido = ovoEmIncubacao.avancarTempo(unidades);
        if (recemNascido == null) {
            return null;
        }

        ovos.remove(ovoEmIncubacao);
        ovoEmIncubacao = null;

        if (equipe.size() < MAXIMO_POKEMONS_ATIVOS) {
            equipe.add(recemNascido);
        } else {
            pokemonAguardandoEscolha = recemNascido;
        }
        return recemNascido;
    }

    /** Substitui um Pokémon ativo pelo recém-nascido que aguardava uma vaga. */
    public boolean manterRecemNascidoNoLugarDe(Pokemon pokemonEnviadoAoProfessor) {
        if (pokemonAguardandoEscolha == null
                || pokemonEnviadoAoProfessor == null
                || !equipe.remove(pokemonEnviadoAoProfessor)) {
            return false;
        }
        equipe.add(pokemonAguardandoEscolha);
        pokemonAguardandoEscolha = null;
        return true;
    }

    /** Envia o recém-nascido ao Professor Carvalho e mantém a equipe atual. */
    public boolean enviarRecemNascidoAoProfessor() {
        if (pokemonAguardandoEscolha == null) {
            return false;
        }
        pokemonAguardandoEscolha = null;
        return true;
    }

    public List<OvoPokemon> getOvos() {
        return Collections.unmodifiableList(new ArrayList<>(ovos));
    }

    public OvoPokemon getOvoEmIncubacao() {
        return ovoEmIncubacao;
    }

    public Pokemon getPokemonAguardandoEscolha() {
        return pokemonAguardandoEscolha;
    }

    public int getTotalPokemonsEOvos() {
        return equipe.size() + ovos.size() + (pokemonAguardandoEscolha == null ? 0 : 1);
    }

    public boolean isLiderGinasio() {
        return codigoInsignia != null;
    }

    public String getCodigoInsignia() {
        return codigoInsignia;
    }

    /** Recupera toda a equipe para permitir um novo encontro futuro. */
    public void recuperarEquipe() {
        for (Pokemon pokemon : equipe) {
            pokemon.recuperarNoCentroMedico();
        }
    }

    private String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return codigo.trim();
    }
}
