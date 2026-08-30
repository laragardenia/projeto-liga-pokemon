package jogo;

import modelo.Pokemon;
import modelo.TipoPokemon;
import modelo.Treinador;

public class TesteBatalha {
    public static void main(String[] args) {
        // 1. Criando os Treinadores
        Treinador ash = new Treinador("Ash");
        Treinador equipeRocket = new Treinador("Rocket Grunt");

        // 2. Criando e adicionando os Pokémons (Nome, Tipo, AP, DP)
        Pokemon pikachu = new Pokemon("Pikachu", TipoPokemon.ELETRICO, 25, 10);
        ash.adicionarPokemon(pikachu);

        Pokemon koffing = new Pokemon("Koffing", TipoPokemon.NORMAL, 15, 12);
        equipeRocket.adicionarPokemon(koffing);

        // 3. Simulando 1 turno de ataque
        SistemaBatalha combate = new SistemaBatalha();
        System.out.println("Iniciando Batalha!");

        // Ash (Atacante) ataca Equipe Rocket (Defensor)
        combate.executarTurno(ash, equipeRocket, pikachu, koffing);

        System.out.println("Status após o ataque:");
        System.out.println("Pikachu Consciente? " + pikachu.isConsciente() + " | XP: " + pikachu.getXp());
        System.out.println("Koffing Consciente? " + koffing.isConsciente() + " | XP: " + koffing.getXp());
    }
}