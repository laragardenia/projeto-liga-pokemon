package teste;

import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import simulacao.EquipeRocket;

import java.util.List;

/** Testes executáveis do estado e da movimentação da Equipe Rocket. */
public class TesteEquipeRocket {
    public static void main(String[] args) {
        testarEstadoInicialEMovimentoAdjacente();
        testarMovimentoNaoAdjacenteRejeitado();
        testarDerrotaImpedeMovimento();
        testarReativacaoUsaVerticeFornecidoExternamente();
        testarReativacaoRejeitaVerticeForaDoGrafo();
        testarDestinosPossiveisProtegidos();

        System.out.println("Todos os testes da Equipe Rocket passaram.");
    }

    private static void testarEstadoInicialEMovimentoAdjacente() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);

        verificar(rocket.isAtiva(), "A Rocket deveria começar ativa.");
        verificar(rocket.getPosicaoAtual().getId().equals("A"),
                "A Rocket deveria começar no vértice A.");
        verificar(rocket.podeMoverPara(grafo.getVertice("B")),
                "O vértice B deveria ser um destino válido.");

        rocket.moverPara(grafo.getVertice("B"));

        verificar(rocket.getPosicaoAtual().getId().equals("B"),
                "A Rocket deveria se mover para o vértice B.");
    }

    private static void testarMovimentoNaoAdjacenteRejeitado() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);
        boolean movimentoRejeitado = false;

        try {
            rocket.moverPara(grafo.getVertice("C"));
        } catch (IllegalArgumentException e) {
            movimentoRejeitado = true;
        }

        verificar(movimentoRejeitado,
                "A Rocket não deveria atravessar dois trechos em um movimento.");
        verificar(rocket.getPosicaoAtual().getId().equals("A"),
                "Um movimento inválido não deveria alterar a posição.");
    }

    private static void testarDerrotaImpedeMovimento() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);
        rocket.derrotar();
        boolean movimentoRejeitado = false;

        try {
            rocket.moverPara(grafo.getVertice("B"));
        } catch (IllegalStateException e) {
            movimentoRejeitado = true;
        }

        verificar(!rocket.isAtiva(), "A Rocket deveria permanecer derrotada.");
        verificar(movimentoRejeitado,
                "Uma Rocket derrotada não deveria conseguir se mover.");
        verificar(rocket.getDestinosPossiveis().isEmpty(),
                "Uma Rocket derrotada não deveria possuir movimentos possíveis.");
    }

    private static void testarReativacaoUsaVerticeFornecidoExternamente() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);
        rocket.derrotar();

        rocket.reativarEm(grafo.getVertice("C"));

        verificar(rocket.isAtiva(), "A Rocket deveria ser reativada.");
        verificar(rocket.getPosicaoAtual().getId().equals("C"),
                "A Rocket deveria reaparecer no vértice fornecido.");
    }

    private static void testarReativacaoRejeitaVerticeForaDoGrafo() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);
        rocket.derrotar();
        Vertice externo = new Vertice("X", "Fora do mapa", TipoVertice.MCP);
        boolean reativacaoRejeitada = false;

        try {
            rocket.reativarEm(externo);
        } catch (IllegalArgumentException e) {
            reativacaoRejeitada = true;
        }

        verificar(reativacaoRejeitada,
                "O respawn deveria rejeitar um vértice que não pertence ao grafo.");
        verificar(!rocket.isAtiva() && rocket.getPosicaoAtual().getId().equals("A"),
                "Um respawn inválido não deveria alterar o estado nem a posição.");
    }

    private static void testarDestinosPossiveisProtegidos() {
        Grafo grafo = criarGrafoBase();
        EquipeRocket rocket = criarRocket(grafo);
        rocket.moverPara(grafo.getVertice("B"));
        List<Vertice> destinos = rocket.getDestinosPossiveis();

        verificar(destinos.size() == 2
                        && destinos.contains(grafo.getVertice("A"))
                        && destinos.contains(grafo.getVertice("C")),
                "A partir de B, os destinos possíveis deveriam ser A e C.");

        boolean alteracaoRejeitada = false;
        try {
            destinos.add(grafo.getVertice("B"));
        } catch (UnsupportedOperationException e) {
            alteracaoRejeitada = true;
        }
        verificar(alteracaoRejeitada,
                "A lista de destinos não deveria permitir alterações externas.");
    }

    private static EquipeRocket criarRocket(Grafo grafo) {
        return new EquipeRocket(grafo, grafo.getVertice("A"));
    }

    private static Grafo criarGrafoBase() {
        Grafo grafo = new Grafo();
        grafo.adicionarVertice("A", "Cidade A", TipoVertice.MCP);
        grafo.adicionarVertice("B", "Cidade B", TipoVertice.MCP);
        grafo.adicionarVertice("C", "Cidade C", TipoVertice.MCP);
        grafo.adicionarAresta("A", "B", 10);
        grafo.adicionarAresta("B", "C", 10);
        return grafo;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
