package teste;

import grafo.Grafo;
import grafo.TipoVertice;
import jogo.ControladorJogo;
import jogo.SistemaBatalha;
import modelo.Item;
import modelo.OvoPokemon;
import modelo.Pokemon;
import modelo.TipoItem;
import modelo.TipoPokemon;
import modelo.Treinador;
import algoritmos.BFS;
import simulacao.EquipeRocket;
import simulacao.JornadaPokemon;

import java.util.Random;

/** Testa aceitação, recusa, incubação, nascimento e limites da equipe. */
public class TesteOvosIncubadora {
    public static void main(String[] args) {
        Grafo grafo = criarMapa();
        Item incubadora = new Item(TipoItem.INCUBADORA);
        Item primeiroItemOvo = new Item(TipoItem.OVO);
        Item segundoItemOvo = new Item(TipoItem.OVO);
        grafo.getVertice("LAB").adicionarItem(incubadora);
        grafo.getVertice("LAB").adicionarItem(primeiroItemOvo);
        grafo.getVertice("LAB").adicionarItem(segundoItemOvo);

        Treinador jogador = criarTreinadorComCincoPokemons();
        JornadaPokemon jornada = new JornadaPokemon(
                grafo, grafo.getVertice("LAB"), 500);
        EquipeRocket rocket = new EquipeRocket(grafo, grafo.getVertice("CID"));
        Treinador treinadorRocket = new Treinador("Equipe Rocket");

        ControladorJogo controlador = new ControladorJogo(
                grafo,
                jornada,
                jogador,
                rocket,
                treinadorRocket,
                new SistemaBatalha(new Random(0)),
                new BFS(),
                new Random(0));
        jornada.adicionarObservador(controlador);

        verificar(controlador.getOvosAguardandoDecisao().size() == 2,
                "Os dois ovos encontrados deveriam aguardar uma decisão.");

        OvoPokemon primeiroOvo = controlador.aceitarOvo(primeiroItemOvo);
        OvoPokemon segundoOvo = controlador.aceitarOvo(segundoItemOvo);
        verificar(primeiroOvo != null && segundoOvo != null,
                "O treinador deveria aceitar os dois ovos.");
        verificar(!jornada.getInventario().contains(primeiroItemOvo)
                        && !jornada.getInventario().contains(segundoItemOvo),
                "Os ovos aceitos deveriam sair do inventário de itens.");
        verificar(jogador.getTotalPokemonsEOvos() == 7,
                "Cinco Pokémon e dois ovos deveriam atingir o limite total de sete.");
        verificar(controlador.iniciarIncubacao(primeiroOvo),
                "A incubadora deveria iniciar o primeiro ovo.");
        verificar(!controlador.iniciarIncubacao(segundoOvo),
                "Apenas um ovo pode usar a incubadora por vez.");

        Item terceiroItemOvo = new Item(TipoItem.OVO);
        grafo.getVertice("CID").adicionarItem(terceiroItemOvo);
        jornada.viajarPara(grafo.getVertice("CID"));

        verificar(primeiroOvo.isChocado() && primeiroOvo.getTempoIncubado() == 100,
                "O primeiro ovo deveria chocar após 100 unidades.");
        verificar(jogador.getEquipe().size() == 6,
                "O recém-nascido deveria ocupar a sexta vaga da equipe.");
        verificar(jogador.getEquipe().get(5).getXp() == 0,
                "O Pokémon nascido deveria começar com zero XP.");
        verificar(controlador.aceitarOvo(terceiroItemOvo) == null,
                "Um novo ovo não poderia ultrapassar o total de sete.");
        verificar(controlador.recusarOvo(terceiroItemOvo),
                "O treinador deveria conseguir recusar o ovo excedente.");
        verificar(!jornada.getInventario().contains(terceiroItemOvo),
                "O ovo recusado deveria sair do inventário.");
        verificar(jornada.possuiItem(TipoItem.INCUBADORA),
                "A incubadora não deveria ser consumida pelo uso.");

        verificar(controlador.iniciarIncubacao(segundoOvo),
                "A incubadora deveria ficar disponível após o primeiro nascimento.");
        jornada.viajarPara(grafo.getVertice("LAB"));

        Pokemon recemNascido = controlador.getRecemNascidoAguardandoEscolha();
        verificar(recemNascido != null && recemNascido.getXp() == 0,
                "Com seis ativos, o recém-nascido deveria aguardar uma escolha.");
        verificar(jogador.getEquipe().size() == 6
                        && jogador.getTotalPokemonsEOvos() == 7,
                "O limite deveria continuar respeitado enquanto a escolha estiver pendente.");

        Pokemon enviadoAoProfessor = jogador.getEquipe().get(0);
        verificar(controlador.manterRecemNascidoNoLugarDe(enviadoAoProfessor),
                "Deveria ser possível trocar um ativo pelo recém-nascido.");
        verificar(jogador.getEquipe().size() == 6
                        && !jogador.getEquipe().contains(enviadoAoProfessor)
                        && jogador.getPokemonAguardandoEscolha() == null,
                "A substituição deveria manter exatamente seis Pokémon ativos.");

        System.out.println("Todos os testes de ovos e incubadora passaram.");
    }

    private static Grafo criarMapa() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("LAB", "Laboratório", TipoVertice.CARVALHO);
        grafo.adicionarVertice("CID", "Cidade", TipoVertice.GINASIO);
        grafo.adicionarAresta("LAB", "CID", 100);
        return grafo;
    }

    private static Treinador criarTreinadorComCincoPokemons() {
        Treinador treinador = new Treinador("Jogador");
        for (int i = 1; i <= 5; i++) {
            treinador.adicionarPokemon(
                    new Pokemon("Inicial-" + i, TipoPokemon.NORMAL, 20, 10));
        }
        return treinador;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
