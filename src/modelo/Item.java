package modelo;

/** Representa um item coletável do mapa. */
public class Item {
    private final String nome;

    /** Mantido para compatibilidade com o stub inicial e leitores futuros. */
    public Item() {
        this("Item");
    }

    public Item(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item é obrigatório.");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
