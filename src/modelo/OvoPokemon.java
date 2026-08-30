package modelo;

/** Representa um ovo aceito pelo treinador e seu progresso de incubação. */
public class OvoPokemon {
    public static final int TEMPO_NECESSARIO_PARA_CHOCAR = 100;

    private final Pokemon especieBase;
    private int tempoIncubado;
    private boolean emIncubacao;
    private boolean chocado;

    public OvoPokemon(Pokemon especieBase) {
        if (especieBase == null) {
            throw new IllegalArgumentException("A espécie base do ovo é obrigatória.");
        }
        this.especieBase = especieBase;
        this.tempoIncubado = 0;
        this.emIncubacao = false;
        this.chocado = false;
    }

    public void iniciarIncubacao() {
        if (chocado) {
            throw new IllegalStateException("Um ovo chocado não pode ser incubado novamente.");
        }
        emIncubacao = true;
    }

    /**
     * Soma o tempo percorrido e devolve o recém-nascido quando alcançar 100
     * unidades. Antes disso, retorna nulo.
     */
    public Pokemon avancarTempo(int unidades) {
        if (unidades < 0) {
            throw new IllegalArgumentException("O tempo percorrido não pode ser negativo.");
        }
        if (!emIncubacao || chocado) {
            return null;
        }

        tempoIncubado = Math.min(
                TEMPO_NECESSARIO_PARA_CHOCAR,
                tempoIncubado + unidades);

        if (tempoIncubado < TEMPO_NECESSARIO_PARA_CHOCAR) {
            return null;
        }

        emIncubacao = false;
        chocado = true;
        return new Pokemon(
                especieBase.getNome(),
                especieBase.getTipo(),
                especieBase);
    }

    public int getTempoIncubado() {
        return tempoIncubado;
    }

    public int getTempoRestante() {
        return TEMPO_NECESSARIO_PARA_CHOCAR - tempoIncubado;
    }

    public boolean isEmIncubacao() {
        return emIncubacao;
    }

    public boolean isChocado() {
        return chocado;
    }
}
