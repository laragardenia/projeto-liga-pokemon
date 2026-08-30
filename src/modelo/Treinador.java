package modelo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Treinador {
    private final String nome;
    private int xp;
    private final List<Pokemon> equipe;
    private final String codigoInsignia;

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
        this.codigoInsignia = normalizarCodigo(codigoInsignia);
    }

    public boolean podeBatalhar() {
        int conscientes = 0;
        for (Pokemon p : equipe) {
            if (p.isConsciente()) conscientes++;
        }
        return conscientes >= 3; // Mínimo de 3 conscientes para batalha
    }

    public boolean adicionarPokemon(Pokemon p) {
        if (p == null || equipe.size() >= 6) {
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
