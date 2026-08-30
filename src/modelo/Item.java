package modelo;

/** Representa um item coletável do mapa. */
public class Item {
    private final String nome;
    private final TipoItem tipo;

    /** Mantido para compatibilidade com o stub inicial e leitores futuros. */
    public Item() {
        this("Item", TipoItem.OUTRO);
    }

    public Item(String nome) {
        this(nome, inferirTipo(nome));
    }

    public Item(TipoItem tipo) {
        this(nomePadrao(tipo), tipo);
    }

    public Item(String nome, TipoItem tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do item é obrigatório.");
        }
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    /**
     * Consome uma erva e recupera 10 HP de cada Pokémon consciente do
     * treinador, respeitando o limite de 100 HP.
     */
    public boolean aplicarEm(Treinador treinador) {
        if (treinador == null) {
            throw new IllegalArgumentException("O treinador é obrigatório.");
        }
        if (tipo != TipoItem.ERVA) {
            return false;
        }

        for (Pokemon pokemon : treinador.getEquipe()) {
            pokemon.recuperarHp(10);
        }
        return true;
    }

    private static TipoItem inferirTipo(String nome) {
        if (nome == null) {
            return TipoItem.OUTRO;
        }
        String normalizado = nome.trim().toLowerCase();
        if (normalizado.contains("erva")) {
            return TipoItem.ERVA;
        }
        if (normalizado.contains("ovo")) {
            return TipoItem.OVO;
        }
        if (normalizado.contains("incubadora") || normalizado.contains("encubadora")) {
            return TipoItem.INCUBADORA;
        }
        if (normalizado.contains("pokébola") || normalizado.contains("pokebola")) {
            return TipoItem.POKEBOLA;
        }
        return TipoItem.OUTRO;
    }

    private static String nomePadrao(TipoItem tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do item é obrigatório.");
        }
        switch (tipo) {
            case ERVA:
                return "Erva medicinal";
            case OVO:
                return "Ovo Pokémon";
            case INCUBADORA:
                return "Incubadora";
            case POKEBOLA:
                return "Pokébola";
            default:
                return "Item";
        }
    }

    @Override
    public String toString() {
        return nome;
    }
}
