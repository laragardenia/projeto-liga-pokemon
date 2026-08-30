package modelo;

public class Pokemon {
    private String nome;
    private TipoPokemon tipo;
    private int hp;
    private int ap;
    private int dp;
    private int xp;

    // Construtor Padrão (Professor Carvalho / Selvagens)
    public Pokemon(String nome, TipoPokemon tipo, int ap, int dp) {
        this.nome = nome;
        this.tipo = tipo;
        this.hp = 100; // HP vai de 1 a 100
        this.ap = ap;
        this.dp = dp;
        this.xp = 0;
    }

    // Construtor Secundário: Lógica de Chocar Ovo
    // Nasce com 0 XP, mas copia os atributos base da espécie em fase inicial
    public Pokemon(String nome, TipoPokemon tipo, Pokemon baseEspecie) {
        this.nome = nome;
        this.tipo = tipo;
        this.hp = 100;
        this.ap = baseEspecie.getAp();
        this.dp = baseEspecie.getDp();
        this.xp = 0;
    }

    public boolean isConsciente() {
        return this.hp >= 20; // Inconsciente se HP menor que 20
    }

    public void receberDano(int dano) {
        this.hp -= dano;
        if (this.hp < 0) this.hp = 0;
    }

    public void evoluir() {
        if (this.xp >= 1000) { // Evolui ao acumular 1000 XP
            this.ap += (int) (this.ap * 0.3); // Acréscimo de 30% em AP
            this.dp += (int) (this.dp * 0.3); // Acréscimo de 30% em DP
            this.xp -= 1000;
        }
    }

    // Getters e Setters básicos
    public int getAp() { return ap; }
    public int getDp() { return dp; }
    public int getXp() { return xp; }
    public int getHp() { return hp; }
    public void adicionarXp(int ganho) { this.xp += ganho; }
    public void adicionarAtributos(int ganho) { this.ap += ganho; this.dp += ganho; }
    public void recuperarHp() { if(this.hp < 100) this.hp++; } // Recuperação passiva
}