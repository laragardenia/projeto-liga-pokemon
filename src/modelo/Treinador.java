package modelo;
import java.util.ArrayList;
import java.util.List;

public class Treinador {
    private String nome;
    private int xp;
    private List<Pokemon> equipe;

    public Treinador(String nome) {
        this.nome = nome;
        this.xp = 0;
        this.equipe = new ArrayList<>();
    }

    public boolean podeBatalhar() {
        int conscientes = 0;
        for (Pokemon p : equipe) {
            if (p.isConsciente()) conscientes++;
        }
        return conscientes >= 3; // Mínimo de 3 conscientes para batalha
    }

    public void adicionarPokemon(Pokemon p) {
        if (this.equipe.size() < 6) { // Limite de 6 ativos
            this.equipe.add(p);
        }
    }

    public int getXp() { return xp; }
    public void adicionarXp(int ganho) { this.xp += ganho; }
    public List<Pokemon> getEquipe() { return equipe; }
}