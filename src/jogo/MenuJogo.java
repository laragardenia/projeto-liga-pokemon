package jogo;

import algoritmos.ResultadoCaminho;
import grafo.Grafo;
import grafo.TipoVertice;
import grafo.Vertice;
import modelo.Item;
import modelo.OvoPokemon;
import modelo.Pokemon;
import modelo.TipoItem;
import modelo.Treinador;
import simulacao.JornadaPokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Interface de terminal que transforma os módulos do projeto em um jogo. */
public class MenuJogo {
    private final Grafo grafo;
    private final JornadaPokemon jornada;
    private final Treinador jogador;
    private final ControladorJogo controlador;
    private final Map<String, String[]> regrasEvolucao;
    private final Scanner scanner;
    private boolean executando;

    public MenuJogo(
            Grafo grafo,
            JornadaPokemon jornada,
            Treinador jogador,
            ControladorJogo controlador,
            Map<String, String[]> regrasEvolucao,
            Scanner scanner) {
        if (grafo == null || jornada == null || jogador == null
                || controlador == null
                || regrasEvolucao == null || scanner == null) {
            throw new IllegalArgumentException("As dependências do menu são obrigatórias.");
        }
        this.grafo = grafo;
        this.jornada = jornada;
        this.jogador = jogador;
        this.controlador = controlador;
        this.regrasEvolucao = new HashMap<String, String[]>(regrasEvolucao);
        this.scanner = scanner;
        this.executando = true;
    }

    public void executar() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       RUMO À LIGA POKÉMON");
        System.out.println("========================================");
        System.out.println("Explore o mapa, conquiste 8 insígnias e chegue à Liga no prazo.");
        mostrarEncontrosDoLocal();

        while (executando) {
            mostrarMenuPrincipal();
            int opcao = lerInteiro("Escolha uma opção: ", 0, 12);
            if (!executando) {
                break;
            }

            try {
                executarOpcao(opcao);
            } catch (IllegalArgumentException | IllegalStateException erro) {
                System.out.println("Não foi possível concluir a ação: " + erro.getMessage());
            }
        }

        System.out.println("Jogo encerrado. Até a próxima jornada!");
    }

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("--- MENU PRINCIPAL ---");
        System.out.println("Local: " + jornada.getPosicaoAtual().getNome()
                + " | Tempo: " + jornada.getTempoDecorrido()
                + "/" + jornada.getPrazoLiga()
                + " | Insígnias: " + jornada.getInsignias().size()
                + "/" + JornadaPokemon.INSIGNIAS_NECESSARIAS_PARA_LIGA);
        System.out.println("1  - Ver status da jornada");
        System.out.println("2  - Ver mapa e planejar rotas");
        System.out.println("3  - Viajar");
        System.out.println("4  - Ver equipe Pokémon");
        System.out.println("5  - Ver ou capturar Pokémon selvagem");
        System.out.println("6  - Batalhar contra treinador ou líder");
        System.out.println("7  - Enfrentar Equipe Rocket");
        System.out.println("8  - Ver inventário e usar item");
        System.out.println("9  - Gerenciar ovos e incubadora");
        System.out.println("10 - Usar Centro Médico Pokémon");
        System.out.println("11 - Ver insígnias e inscrição na Liga");
        System.out.println("12 - Evoluir Pokémon");
        System.out.println("0  - Sair");
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                mostrarStatus();
                break;
            case 2:
                mostrarMapaERotas();
                break;
            case 3:
                viajar();
                break;
            case 4:
                mostrarEquipe();
                break;
            case 5:
                capturarPokemon();
                break;
            case 6:
                batalharContraTreinador();
                break;
            case 7:
                batalharContraRocket();
                break;
            case 8:
                gerenciarInventario();
                break;
            case 9:
                gerenciarOvos();
                break;
            case 10:
                usarCentroMedico();
                break;
            case 11:
                verificarLiga();
                break;
            case 12:
                evoluirPokemon();
                break;
            case 0:
                executando = false;
                break;
            default:
                throw new IllegalArgumentException("Opção inexistente.");
        }
    }

    private void mostrarStatus() {
        System.out.println();
        System.out.println("--- STATUS DA JORNADA ---");
        System.out.println("Treinador: " + jogador.getNome());
        System.out.println("Posição: " + jornada.getPosicaoAtual().getNome()
                + " (" + jornada.getPosicaoAtual().getId() + ")");
        System.out.println("Tempo decorrido: " + jornada.getTempoDecorrido());
        System.out.println("Tempo restante: " + jornada.getTempoRestante());
        System.out.println("XP do treinador: " + jogador.getXp());
        System.out.println("Pokémon ativos: " + jogador.getEquipe().size()
                + "/" + Treinador.MAXIMO_POKEMONS_ATIVOS);
        System.out.println("Pokébolas: " + controlador.getQuantidadePokebolas());
        System.out.println("Insígnias: " + jornada.getInsignias().size()
                + "/" + JornadaPokemon.INSIGNIAS_NECESSARIAS_PARA_LIGA);
        System.out.println("Locais visitados: " + jornada.getLocaisVisitados().size());
        System.out.println("Dentro do prazo: " + simNao(jornada.estaDentroDoPrazo()));
        mostrarEncontrosDoLocal();
    }

    private void mostrarMapaERotas() {
        System.out.println();
        System.out.println("--- MAPA E MELHORES ROTAS ---");
        System.out.println("Origem atual: " + jornada.getPosicaoAtual().getNome());

        for (Vertice destino : verticesOrdenados()) {
            ResultadoCaminho rota = jornada.planejarRota(destino);
            String tempo = rota.isAlcancavel()
                    ? rota.getTempoTotal() + " unidades"
                    : "inalcançável";
            System.out.println(destino.getId() + " - " + destino.getNome()
                    + " [" + destino.getTipo() + "] | melhor tempo: " + tempo);
        }

        String id = lerTexto("Digite um ID para detalhar a rota (Enter para voltar): ");
        if (!executando || id.isEmpty()) {
            return;
        }
        Vertice destino = localizarVertice(id);
        if (destino == null) {
            System.out.println("Não existe local com o ID informado.");
            return;
        }
        exibirRota(jornada.planejarRota(destino));
    }

    private void viajar() {
        System.out.println();
        System.out.println("--- VIAJAR ---");
        for (Vertice vertice : verticesOrdenados()) {
            System.out.println(vertice.getId() + " - " + vertice.getNome()
                    + " [" + vertice.getTipo() + "]");
        }

        String id = lerTexto("ID do destino (Enter para cancelar): ");
        if (!executando || id.isEmpty()) {
            return;
        }
        Vertice destino = localizarVertice(id);
        if (destino == null) {
            System.out.println("Destino inexistente.");
            return;
        }

        ResultadoCaminho plano = jornada.planejarRota(destino);
        if (!plano.isAlcancavel()) {
            System.out.println("Não existe caminho até esse destino.");
            return;
        }
        exibirRota(plano);
        if (destino.equals(jornada.getPosicaoAtual())) {
            System.out.println("Você já está nesse local.");
            return;
        }
        if (!confirmar("Percorrer essa rota? (S/N): ")) {
            System.out.println("Viagem cancelada.");
            return;
        }

        int itensAntes = jornada.getInventario().size();
        int equipeAntes = jogador.getEquipe().size();
        ResultadoCaminho resultado = jornada.viajarPara(destino);
        System.out.println("Você chegou a " + destino.getNome() + ".");
        System.out.println("Tempo da viagem: " + resultado.getTempoTotal());
        System.out.println("Tempo restante: " + jornada.getTempoRestante());

        int itensColetados = jornada.getInventario().size() - itensAntes;
        if (itensColetados > 0) {
            System.out.println("Itens coletados no caminho: " + itensColetados + ".");
        }
        if (jogador.getEquipe().size() > equipeAntes) {
            System.out.println("Um ovo chocou e o novo Pokémon entrou na equipe!");
        }
        if (!jornada.estaDentroDoPrazo()) {
            System.out.println("ATENÇÃO: o prazo para entrar na Liga foi ultrapassado.");
        }
        mostrarEncontrosDoLocal();
    }

    private void mostrarEquipe() {
        System.out.println();
        System.out.println("--- EQUIPE POKÉMON ---");
        List<Pokemon> equipe = jogador.getEquipe();
        for (int i = 0; i < equipe.size(); i++) {
            System.out.println((i + 1) + " - " + descreverPokemon(equipe.get(i)));
        }
        if (equipe.isEmpty()) {
            System.out.println("A equipe está vazia.");
        }
        System.out.println("Pokémon e ovos sob responsabilidade: "
                + jogador.getTotalPokemonsEOvos()
                + "/" + Treinador.MAXIMO_TOTAL_COM_OVOS);
    }

    private void capturarPokemon() {
        System.out.println();
        System.out.println("--- POKÉMON SELVAGENS ---");
        List<Pokemon> selvagens = jornada.getPosicaoAtual().getPokemonsSelvagens();
        if (selvagens.isEmpty()) {
            System.out.println("Não há Pokémon selvagens neste local.");
            return;
        }

        for (int i = 0; i < selvagens.size(); i++) {
            System.out.println((i + 1) + " - " + descreverPokemon(selvagens.get(i)));
        }
        System.out.println("Pokébolas disponíveis: " + controlador.getQuantidadePokebolas());
        int escolha = lerInteiro("Escolha um Pokémon para capturar (0 para voltar): ",
                0, selvagens.size());
        if (!executando || escolha == 0) {
            return;
        }

        Pokemon escolhido = selvagens.get(escolha - 1);
        if (controlador.capturarPokemon(escolhido)) {
            System.out.println(escolhido.getNome() + " foi capturado e entrou na equipe!");
        } else if (controlador.getQuantidadePokebolas() == 0) {
            System.out.println("Você não possui Pokébolas.");
        } else {
            System.out.println("Não há espaço: são permitidos até 6 Pokémon ativos e 7"
                    + " Pokémon/ovos no total.");
        }
    }

    private void batalharContraTreinador() {
        System.out.println();
        System.out.println("--- BATALHA CONTRA TREINADOR ---");
        if (batalhasProibidasNoLocal()) {
            System.out.println("Batalhas são proibidas no laboratório e no Centro Médico.");
            return;
        }
        List<Treinador> adversarios = controlador.getTreinadoresDisponiveis();
        if (adversarios.isEmpty()) {
            System.out.println("Não há treinadores disponíveis neste local.");
            return;
        }
        if (!jogador.podeBatalhar()) {
            System.out.println("São necessários pelo menos 3 Pokémon conscientes para batalhar.");
            return;
        }

        for (int i = 0; i < adversarios.size(); i++) {
            Treinador adversario = adversarios.get(i);
            String categoria = adversario.isLiderGinasio() ? "Líder de Ginásio" : "Treinador";
            System.out.println((i + 1) + " - " + adversario.getNome()
                    + " [" + categoria + ", XP " + adversario.getXp() + "]");
        }
        int escolha = lerInteiro("Escolha o adversário (0 para voltar): ",
                0, adversarios.size());
        if (!executando || escolha == 0) {
            return;
        }
        if (!confirmar("Iniciar a batalha? (S/N): ")) {
            return;
        }

        Treinador adversario = adversarios.get(escolha - 1);
        boolean jaTinhaInsignia = adversario.isLiderGinasio()
                && jornada.possuiInsignia(adversario.getCodigoInsignia());
        ResultadoBatalha resultado = controlador.batalharContra(adversario);
        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE) {
            System.out.println("Vitória! " + adversario.getNome() + " foi derrotado.");
            if (adversario.isLiderGinasio() && !jaTinhaInsignia) {
                System.out.println("Nova insígnia conquistada: "
                        + adversario.getCodigoInsignia() + ".");
            }
        } else {
            System.out.println("Você perdeu a batalha. Visite um Centro Médico Pokémon.");
        }
        avisarSobreRecemNascido();
    }

    private void batalharContraRocket() {
        System.out.println();
        System.out.println("--- EQUIPE ROCKET ---");
        if (batalhasProibidasNoLocal()) {
            System.out.println("Batalhas são proibidas no laboratório e no Centro Médico.");
            return;
        }
        if (!controlador.temEncontroComRocket()) {
            System.out.println("A Equipe Rocket não está neste local.");
            return;
        }
        if (!jogador.podeBatalhar()) {
            System.out.println("São necessários pelo menos 3 Pokémon conscientes para batalhar.");
            return;
        }
        if (!confirmar("Enfrentar a Equipe Rocket? (S/N): ")) {
            return;
        }

        ResultadoBatalha resultado = controlador.batalharContraRocket();
        if (resultado == ResultadoBatalha.VITORIA_DESAFIANTE) {
            System.out.println("A Equipe Rocket foi derrotada e fugiu para outro local!");
        } else {
            System.out.println("A Equipe Rocket venceu. Recupere sua equipe antes da revanche.");
        }
        avisarSobreRecemNascido();
    }

    private void gerenciarInventario() {
        System.out.println();
        System.out.println("--- INVENTÁRIO ---");
        Map<TipoItem, Integer> quantidades = contarItens();
        for (TipoItem tipo : TipoItem.values()) {
            int quantidade = quantidades.containsKey(tipo) ? quantidades.get(tipo) : 0;
            if (quantidade > 0) {
                System.out.println(nomeTipoItem(tipo) + ": " + quantidade);
            }
        }
        if (jornada.getInventario().isEmpty()) {
            System.out.println("O inventário está vazio.");
            return;
        }

        System.out.println("1 - Usar uma erva medicinal");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Escolha: ", 0, 1);
        if (!executando || opcao == 0) {
            return;
        }
        Item erva = localizarItem(TipoItem.ERVA);
        if (erva == null) {
            System.out.println("Você não possui ervas medicinais.");
        } else if (controlador.usarItem(erva)) {
            System.out.println("Erva utilizada. Pokémon conscientes recuperaram 10 HP.");
        } else {
            System.out.println("O item não pôde ser utilizado.");
        }
    }

    private void gerenciarOvos() {
        boolean noSubmenu = true;
        while (noSubmenu && executando) {
            System.out.println();
            System.out.println("--- OVOS E INCUBADORA ---");
            System.out.println("Ovos aguardando decisão: "
                    + controlador.getOvosAguardandoDecisao().size());
            System.out.println("Ovos aceitos: " + controlador.getOvosAceitos().size());
            OvoPokemon incubando = jogador.getOvoEmIncubacao();
            if (incubando == null) {
                System.out.println("Incubação ativa: nenhuma");
            } else {
                System.out.println("Incubação ativa: " + incubando.getTempoIncubado()
                        + "/" + OvoPokemon.TEMPO_NECESSARIO_PARA_CHOCAR);
            }
            Pokemon recemNascido = controlador.getRecemNascidoAguardandoEscolha();
            if (recemNascido != null) {
                System.out.println("Recém-nascido aguardando vaga: " + recemNascido.getNome());
            }
            System.out.println("1 - Aceitar ou recusar ovo encontrado");
            System.out.println("2 - Listar ovos aceitos");
            System.out.println("3 - Iniciar incubação");
            System.out.println("4 - Resolver vaga de recém-nascido");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha: ", 0, 4);
            if (!executando) {
                return;
            }
            switch (opcao) {
                case 1:
                    decidirOvoEncontrado();
                    break;
                case 2:
                    listarOvosAceitos();
                    break;
                case 3:
                    iniciarIncubacao();
                    break;
                case 4:
                    resolverRecemNascido();
                    break;
                case 0:
                    noSubmenu = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void decidirOvoEncontrado() {
        List<Item> pendentes = controlador.getOvosAguardandoDecisao();
        if (pendentes.isEmpty()) {
            System.out.println("Não há ovos aguardando decisão.");
            return;
        }
        System.out.println("1 - Aceitar o próximo ovo");
        System.out.println("2 - Recusar o próximo ovo");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Escolha: ", 0, 2);
        if (!executando || opcao == 0) {
            return;
        }
        Item ovo = pendentes.get(0);
        if (opcao == 1) {
            OvoPokemon aceito = controlador.aceitarOvo(ovo);
            if (aceito == null) {
                System.out.println("Não há espaço para aceitar o ovo (limite total: 7).");
            } else {
                System.out.println("Ovo aceito. A espécie será revelada quando ele chocar.");
            }
        } else if (controlador.recusarOvo(ovo)) {
            System.out.println("Ovo recusado e removido do inventário.");
        }
    }

    private void listarOvosAceitos() {
        List<OvoPokemon> ovos = controlador.getOvosAceitos();
        if (ovos.isEmpty()) {
            System.out.println("Nenhum ovo foi aceito.");
            return;
        }
        for (int i = 0; i < ovos.size(); i++) {
            OvoPokemon ovo = ovos.get(i);
            String estado = ovo.isEmIncubacao() ? "incubando" : "aguardando incubação";
            System.out.println((i + 1) + " - " + estado + ", progresso "
                    + ovo.getTempoIncubado() + "/"
                    + OvoPokemon.TEMPO_NECESSARIO_PARA_CHOCAR);
        }
    }

    private void iniciarIncubacao() {
        if (!jornada.possuiItem(TipoItem.INCUBADORA)) {
            System.out.println("Você não possui uma incubadora.");
            return;
        }
        if (jogador.getOvoEmIncubacao() != null) {
            System.out.println("Já existe um ovo em incubação.");
            return;
        }
        List<OvoPokemon> ovosDisponiveis = new ArrayList<OvoPokemon>();
        for (OvoPokemon ovo : controlador.getOvosAceitos()) {
            if (!ovo.isEmIncubacao() && !ovo.isChocado()) {
                ovosDisponiveis.add(ovo);
            }
        }
        if (ovosDisponiveis.isEmpty()) {
            System.out.println("Não há ovos disponíveis para incubar.");
            return;
        }
        for (int i = 0; i < ovosDisponiveis.size(); i++) {
            OvoPokemon ovo = ovosDisponiveis.get(i);
            System.out.println((i + 1) + " - Ovo, progresso "
                    + ovo.getTempoIncubado() + "/"
                    + OvoPokemon.TEMPO_NECESSARIO_PARA_CHOCAR);
        }
        int escolha = lerInteiro("Escolha o ovo (0 para voltar): ",
                0, ovosDisponiveis.size());
        if (!executando || escolha == 0) {
            return;
        }
        if (controlador.iniciarIncubacao(ovosDisponiveis.get(escolha - 1))) {
            System.out.println("Incubação iniciada. Viaje para aumentar o progresso.");
        } else {
            System.out.println("Não foi possível iniciar a incubação.");
        }
    }

    private void resolverRecemNascido() {
        Pokemon recemNascido = controlador.getRecemNascidoAguardandoEscolha();
        if (recemNascido == null) {
            System.out.println("Não há recém-nascido aguardando uma decisão.");
            return;
        }
        System.out.println("O ovo revelou " + descreverPokemon(recemNascido) + ".");
        System.out.println("1 - Substituir um Pokémon da equipe");
        System.out.println("2 - Enviar o recém-nascido ao Professor Carvalho");
        System.out.println("0 - Decidir depois");
        int opcao = lerInteiro("Escolha: ", 0, 2);
        if (!executando || opcao == 0) {
            return;
        }
        if (opcao == 2) {
            controlador.enviarRecemNascidoAoProfessor();
            System.out.println(recemNascido.getNome() + " foi enviado ao Professor Carvalho.");
            return;
        }

        List<Pokemon> equipe = jogador.getEquipe();
        for (int i = 0; i < equipe.size(); i++) {
            System.out.println((i + 1) + " - " + descreverPokemon(equipe.get(i)));
        }
        int escolha = lerInteiro("Pokémon que será enviado (0 para cancelar): ",
                0, equipe.size());
        if (!executando || escolha == 0) {
            return;
        }
        Pokemon enviado = equipe.get(escolha - 1);
        if (controlador.manterRecemNascidoNoLugarDe(enviado)) {
            System.out.println(recemNascido.getNome() + " entrou na equipe e "
                    + enviado.getNome() + " foi enviado ao Professor.");
        }
    }

    private void usarCentroMedico() {
        System.out.println();
        System.out.println("--- CENTRO MÉDICO POKÉMON ---");
        if (jornada.getPosicaoAtual().getTipo() != TipoVertice.MCP) {
            System.out.println("Você precisa estar em um Centro Médico Pokémon.");
            return;
        }
        jogador.recuperarEquipe();
        System.out.println("Toda a equipe foi recuperada para 100 HP.");
    }

    private void verificarLiga() {
        System.out.println();
        System.out.println("--- LIGA POKÉMON ---");
        int quantidadeInsignias = jornada.getInsignias().size();
        boolean estaNoEstadio = jornada.getPosicaoAtual().getTipo() == TipoVertice.ESTADIO;
        System.out.println("No Estádio: " + simNao(estaNoEstadio));
        System.out.println("Insígnias: " + quantidadeInsignias + "/"
                + JornadaPokemon.INSIGNIAS_NECESSARIAS_PARA_LIGA);
        System.out.println("Dentro do prazo: " + simNao(jornada.estaDentroDoPrazo())
                + " (restam " + jornada.getTempoRestante() + " unidades)");

        if (!jornada.podeSeInscreverNaLiga()) {
            System.out.println("Você ainda não cumpre todos os requisitos para a inscrição.");
            return;
        }
        if (confirmar("Todos os requisitos foram cumpridos. Inscrever-se na Liga? (S/N): ")) {
            System.out.println();
            System.out.println("PARABÉNS! Você se inscreveu na Liga Pokémon e concluiu o jogo!");
            executando = false;
        }
    }

    private void evoluirPokemon() {
        System.out.println();
        System.out.println("--- EVOLUÇÃO ---");
        List<Pokemon> candidatos = new ArrayList<Pokemon>();
        for (Pokemon pokemon : jogador.getEquipe()) {
            String[] regra = regrasEvolucao.get(pokemon.getNome());
            if (regra != null && regra.length >= 2) {
                int xpNecessario = Integer.parseInt(regra[1]);
                System.out.println((candidatos.size() + 1) + " - " + pokemon.getNome()
                        + " -> " + regra[0] + " | XP " + pokemon.getXp()
                        + "/" + xpNecessario);
                candidatos.add(pokemon);
            }
        }
        if (candidatos.isEmpty()) {
            System.out.println("Nenhum Pokémon da equipe possui uma evolução cadastrada.");
            return;
        }
        int escolha = lerInteiro("Escolha um Pokémon (0 para voltar): ",
                0, candidatos.size());
        if (!executando || escolha == 0) {
            return;
        }
        Pokemon pokemon = candidatos.get(escolha - 1);
        String nomeAnterior = pokemon.getNome();
        String[] regra = regrasEvolucao.get(nomeAnterior);
        int xpNecessario = Integer.parseInt(regra[1]);
        if (pokemon.evoluir(regra[0], xpNecessario)) {
            System.out.println(nomeAnterior + " evoluiu para " + pokemon.getNome() + "!");
            System.out.println("AP e DP base aumentaram 30%.");
        } else {
            System.out.println("XP insuficiente. São necessários " + xpNecessario + " XP.");
        }
    }

    private void mostrarEncontrosDoLocal() {
        Vertice local = jornada.getPosicaoAtual();
        System.out.println();
        System.out.println("Neste local:");
        System.out.println("- Pokémon selvagens: " + local.getPokemonsSelvagens().size());
        System.out.println("- Treinadores disponíveis: "
                + controlador.getTreinadoresDisponiveis().size());
        System.out.println("- Equipe Rocket presente: "
                + simNao(controlador.temEncontroComRocket()));
        if (!controlador.getOvosAguardandoDecisao().isEmpty()) {
            System.out.println("- Há ovos encontrados aguardando sua decisão.");
        }
        avisarSobreRecemNascido();
    }

    private void avisarSobreRecemNascido() {
        Pokemon recemNascido = controlador.getRecemNascidoAguardandoEscolha();
        if (recemNascido != null) {
            System.out.println("- " + recemNascido.getNome()
                    + " nasceu e aguarda uma vaga na equipe (opção 9).");
        }
    }

    private boolean batalhasProibidasNoLocal() {
        TipoVertice tipo = jornada.getPosicaoAtual().getTipo();
        return tipo == TipoVertice.CARVALHO || tipo == TipoVertice.MCP;
    }

    private void exibirRota(ResultadoCaminho resultado) {
        if (!resultado.isAlcancavel()) {
            System.out.println("Rota inalcançável.");
            return;
        }
        StringBuilder descricao = new StringBuilder();
        for (Vertice vertice : resultado.getCaminho()) {
            if (descricao.length() > 0) {
                descricao.append(" -> ");
            }
            descricao.append(vertice.getNome()).append(" (").append(vertice.getId()).append(")");
        }
        System.out.println("Rota: " + descricao);
        System.out.println("Tempo total: " + resultado.getTempoTotal());
    }

    private List<Vertice> verticesOrdenados() {
        List<Vertice> vertices = new ArrayList<Vertice>(grafo.getTodosVertices());
        Collections.sort(vertices, new Comparator<Vertice>() {
            @Override
            public int compare(Vertice primeiro, Vertice segundo) {
                return extrairNumeroId(primeiro.getId()) - extrairNumeroId(segundo.getId());
            }
        });
        return vertices;
    }

    private int extrairNumeroId(String id) {
        try {
            return Integer.parseInt(id.replaceAll("\\D+", ""));
        } catch (NumberFormatException erro) {
            return Integer.MAX_VALUE;
        }
    }

    private Vertice localizarVertice(String id) {
        Vertice exato = grafo.getVertice(id.trim());
        if (exato != null) {
            return exato;
        }
        for (Vertice vertice : grafo.getTodosVertices()) {
            if (vertice.getId().equalsIgnoreCase(id.trim())) {
                return vertice;
            }
        }
        return null;
    }

    private String descreverPokemon(Pokemon pokemon) {
        String estado = pokemon.isConsciente() ? "consciente" : "inconsciente";
        return pokemon.getNome() + " [" + pokemon.getTipo()
                + ", HP " + pokemon.getHp()
                + ", AP " + pokemon.getAp()
                + ", DP " + pokemon.getDp()
                + ", XP " + pokemon.getXp()
                + ", " + estado + "]";
    }

    private Map<TipoItem, Integer> contarItens() {
        Map<TipoItem, Integer> quantidades = new EnumMap<TipoItem, Integer>(TipoItem.class);
        for (Item item : jornada.getInventario()) {
            TipoItem tipo = item.getTipo();
            int atual = quantidades.containsKey(tipo) ? quantidades.get(tipo) : 0;
            quantidades.put(tipo, atual + 1);
        }
        return quantidades;
    }

    private Item localizarItem(TipoItem tipo) {
        for (Item item : jornada.getInventario()) {
            if (item.getTipo() == tipo) {
                return item;
            }
        }
        return null;
    }

    private String nomeTipoItem(TipoItem tipo) {
        switch (tipo) {
            case ERVA:
                return "Ervas medicinais";
            case OVO:
                return "Ovos encontrados";
            case INCUBADORA:
                return "Incubadoras";
            case POKEBOLA:
                return "Pokébolas";
            default:
                return "Outros itens";
        }
    }

    private String simNao(boolean valor) {
        return valor ? "sim" : "não";
    }

    private boolean confirmar(String mensagem) {
        while (executando) {
            String resposta = lerTexto(mensagem);
            if (!executando) {
                return false;
            }
            if (resposta.equalsIgnoreCase("S") || resposta.equalsIgnoreCase("SIM")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("N") || resposta.equalsIgnoreCase("NAO")
                    || resposta.equalsIgnoreCase("NÃO")) {
                return false;
            }
            System.out.println("Digite S para sim ou N para não.");
        }
        return false;
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (executando) {
            String entrada = lerTexto(mensagem);
            if (!executando) {
                return minimo;
            }
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException erro) {
                // A mensagem abaixo também cobre entradas que não são números.
            }
            System.out.println("Digite um número entre " + minimo + " e " + maximo + ".");
        }
        return minimo;
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            executando = false;
            System.out.println();
            return "";
        }
        return scanner.nextLine().trim();
    }
}
