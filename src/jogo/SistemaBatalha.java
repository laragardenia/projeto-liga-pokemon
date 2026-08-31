package jogo;
import modelo.Pokemon;
import modelo.Treinador;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SistemaBatalha {
    private final Random random;

    public SistemaBatalha() {
        this(new Random());
    }

    public SistemaBatalha(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("A fonte de aleatoriedade é obrigatória.");
        }
        this.random = random;
    }

    /**
     * Executa um ataque em O(1) e informa se o defensor ficou inconsciente.
     */
    public boolean executarTurno(
            Treinador atacante,
            Treinador defensor,
            Pokemon pAtacante,
            Pokemon pDefensor) {
        validarParticipantes(atacante, defensor, pAtacante, pDefensor);

        // O bônus do treinador cresce de forma gradual. Somar o XP integral
        // tornaria líderes experientes invulneráveis para a equipe inicial.
        int apAtacante = pAtacante.getAp() + atacante.getXp() / 100;
        int dpDefensor = pDefensor.getDp() + defensor.getXp() / 100;

        int diferencaXp = Math.abs(pAtacante.getXp() - pDefensor.getXp());
        int chanceEspecial = Math.min(50, diferencaXp);

        // Esquiva: chance proporcional à diferença de XP
        if (random.nextInt(100) < chanceEspecial) {
            System.out.println("O Pokémon atacado esquivou!");
            return false;
        }

        // Dano base[cite: 4]
        int danoBase = Math.max(1, apAtacante - dpDefensor);

        // Crítico: chance proporcional à diferença de XP
        if (random.nextInt(100) < chanceEspecial) {
            System.out.println("Dano Crítico!");
            danoBase *= 2;
        }

        //aplicação do multiplicador de tipo (Elemento Extra) [adiconado por Lara]
        double multiplicadorTipo = modelo.TipoPokemon.obterMultiplicadorDano(
                pAtacante.getTipo(), pDefensor.getTipo());
        
        int danoFinal = (int) Math.max(1, Math.round(danoBase * multiplicadorTipo));

        if (multiplicadorTipo > 1.0) {
            System.out.println("Ataque super efetivo! (x" + multiplicadorTipo + ")");
        } else if (multiplicadorTipo < 1.0) {
            System.out.println("Ataque pouco efetivo... (x" + multiplicadorTipo + ")");
        }

        pDefensor.receberDano(danoFinal);
        System.out.println("Dano causado: " + danoFinal);

        return verificarFimDeCombate(pAtacante, pDefensor);
    }

    /**
     * Executa a batalha completa com três Pokémon conscientes de cada lado.
     * O treinador desafiado realiza o primeiro ataque, conforme o enunciado.
     */
    public ResultadoBatalha batalhar(Treinador desafiante, Treinador desafiado) {
        if (desafiante == null || desafiado == null) {
            throw new IllegalArgumentException("Os dois treinadores são obrigatórios.");
        }
        if (!desafiante.podeBatalhar() || !desafiado.podeBatalhar()) {
            throw new IllegalStateException(
                    "Cada treinador precisa possuir ao menos três Pokémon conscientes.");
        }

        List<Pokemon> equipeDesafiante = selecionarTresConscientes(desafiante);
        List<Pokemon> equipeDesafiado = selecionarTresConscientes(desafiado);
        int indiceDesafiante = 0;
        int indiceDesafiado = 0;
        boolean turnoDoDesafiado = true;
        int turnosSemConclusao = 0;

        while (indiceDesafiante < 3 && indiceDesafiado < 3) {
            Pokemon pokemonDesafiante = equipeDesafiante.get(indiceDesafiante);
            Pokemon pokemonDesafiado = equipeDesafiado.get(indiceDesafiado);
            boolean nocaute;

            if (turnoDoDesafiado) {
                nocaute = executarTurno(
                        desafiado, desafiante, pokemonDesafiado, pokemonDesafiante);
                if (nocaute) {
                    indiceDesafiante++;
                }
            } else {
                nocaute = executarTurno(
                        desafiante, desafiado, pokemonDesafiante, pokemonDesafiado);
                if (nocaute) {
                    indiceDesafiado++;
                }
            }

            turnoDoDesafiado = !turnoDoDesafiado;
            turnosSemConclusao++;
            if (turnosSemConclusao > 10_000) {
                throw new IllegalStateException(
                        "A batalha não pode terminar com os atributos atuais.");
            }
        }

        if (indiceDesafiado == 3) {
            premiarTreinador(desafiante, desafiado);
            return ResultadoBatalha.VITORIA_DESAFIANTE;
        }

        premiarTreinador(desafiado, desafiante);
        return ResultadoBatalha.VITORIA_DESAFIADO;
    }

    private boolean verificarFimDeCombate(Pokemon pAtacante, Pokemon pDefensor) {
        if (!pDefensor.isConsciente()) {
            int xpAtacanteAntes = pAtacante.getXp();
            int xpDefensorAntes = pDefensor.getXp();
            pAtacante.adicionarXp(10); // Vitória
            pDefensor.adicionarXp(3);  // Derrota

            if (xpDefensorAntes >= xpAtacanteAntes) {
                pAtacante.adicionarAtributos(1); // Ganho de atributo permanente
            }
            System.out.println("O Pokémon defensor desmaiou!");
            return true;
        }
        return false;
    }

    private List<Pokemon> selecionarTresConscientes(Treinador treinador) {
        return new ArrayList<>(treinador.getPokemonsConscientes().subList(0, 3));
    }

    private void premiarTreinador(Treinador vencedor, Treinador perdedor) {
        int ganho = perdedor.getXp() >= vencedor.getXp() ? 3 : 1;
        vencedor.adicionarXp(ganho);
    }

    private void validarParticipantes(
            Treinador atacante,
            Treinador defensor,
            Pokemon pAtacante,
            Pokemon pDefensor) {
        if (atacante == null || defensor == null || pAtacante == null || pDefensor == null) {
            throw new IllegalArgumentException("Treinadores e Pokémon são obrigatórios.");
        }
        if (!pAtacante.isConsciente() || !pDefensor.isConsciente()) {
            throw new IllegalStateException("Apenas Pokémon conscientes podem iniciar um turno.");
        }
    }
}
