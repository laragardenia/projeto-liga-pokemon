package modelo;

public enum TipoPokemon {
    FOGO, AGUA, PLANTA, NORMAL, ELETRICO, VENENOSO, FANTASMA; //tipos originais citados

    //retorna o multiplicador de dano com base nas vantagens de tipo:
    //super efetivo: 2.0
    //pouco efetivo: 0.5
    //neutro: 1.0       [adiconado por Lara]
    public static double obterMultiplicadorDano(TipoPokemon atacante, TipoPokemon defensor) {
        if (atacante == null || defensor == null) {
            return 1.0;
        }

        switch (atacante) {
            case FOGO:
                if (defensor == PLANTA) return 2.0;
                if (defensor == AGUA || defensor == FOGO) return 0.5;
                break;
            case AGUA:
                if (defensor == FOGO) return 2.0;
                if (defensor == PLANTA || defensor == AGUA) return 0.5;
                break;
            case PLANTA:
                if (defensor == AGUA) return 2.0;
                if (defensor == FOGO || defensor == PLANTA || defensor == VENENOSO) return 0.5;
                break;
            case ELETRICO:
                if (defensor == AGUA) return 2.0;
                if (defensor == ELETRICO || defensor == PLANTA) return 0.5;
                break;
            case VENENOSO:
                if (defensor == PLANTA) return 2.0;
                if (defensor == VENENOSO || defensor == FANTASMA) return 0.5;
                break;
            case FANTASMA:
                if (defensor == FANTASMA) return 2.0;
                if (defensor == NORMAL) return 0.5;
                break;
            default:
                break;
        }

        return 1.0;
    }
}