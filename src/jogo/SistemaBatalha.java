package jogo;
import modelo.Pokemon;
import modelo.Treinador;
import java.util.Random;

public class SistemaBatalha {
    private Random random = new Random();

    public void executarTurno(Treinador atacante, Treinador defensor, Pokemon pAtacante, Pokemon pDefensor) {
        // Bônus temporário de AP/DP com base no XP do treinador
        int apAtacante = pAtacante.getAp() + atacante.getXp();
        int dpDefensor = pDefensor.getDp() + defensor.getXp();

        int diferencaXp = Math.abs(pAtacante.getXp() - pDefensor.getXp());

        // Esquiva: chance proporcional à diferença de XP
        if (random.nextInt(100) < diferencaXp) {
            System.out.println("O Pokémon atacado esquivou!");
            return;
        }

        // Dano base[cite: 4]
        int dano = apAtacante - dpDefensor;

        // Crítico: chance proporcional à diferença de XP
        if (random.nextInt(100) < diferencaXp) {
            System.out.println("Dano Crítico!");
            dano *= 2;
        }

        if (dano > 0) {
            pDefensor.receberDano(dano);
            System.out.println("Dano causado: " + dano);
        } else {
            System.out.println("O ataque não surtiu efeito.");
        }

        verificarFimDeCombate(atacante, defensor, pAtacante, pDefensor);
    }

    private void verificarFimDeCombate(Treinador atacante, Treinador defensor, Pokemon pAtacante, Pokemon pDefensor) {
        if (!pDefensor.isConsciente()) {
            pAtacante.adicionarXp(10); // Vitória
            pDefensor.adicionarXp(3);  // Derrota

            if (pDefensor.getXp() >= pAtacante.getXp()) {
                pAtacante.adicionarAtributos(1); // Ganho de atributo permanente
                atacante.adicionarXp(3); // Treinador derrota alvo mais forte
            } else {
                atacante.adicionarXp(1); // Treinador derrota alvo mais fraco
            }
            System.out.println("O Pokémon defensor desmaiou!");
        }
    }
}