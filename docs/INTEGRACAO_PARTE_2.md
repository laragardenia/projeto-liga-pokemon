# Integração da Parte 2

Este documento registra o contrato mínimo entre a Parte 2 e os demais módulos.

## Entrada do mapa

O leitor de arquivos deve entregar um `Grafo` já preenchido. A Parte 2 utiliza
somente os métodos públicos `getVertice`, `getTodosVertices` e
`getAdjacentes`.

Com o grafo pronto, o programa principal pode criar a jornada:

```java
JornadaPokemon jornada = new JornadaPokemon(grafo, posicaoInicial, prazoLiga);
```

## Eventos e batalhas

`ObservadorJornada` é chamado após cada chegada. Ele recebe o trecho, o tempo
decorrido e os itens coletados. `ControladorJogo` implementa esse contrato para
detectar os treinadores e a Equipe Rocket presentes, mantendo as regras de
combate fora da jornada.

Depois de uma vitória em ginásio, o módulo responsável informa a conquista:

```java
jornada.registrarInsignia("CODIGO_DA_INSIGNIA");
```

## Equipe Rocket e BFS

`EquipeRocket` movimenta-se por um único vértice adjacente com `moverPara`.
Depois de uma derrota, a BFS calcula as camadas do grafo. O controlador sorteia
um vértice dentre os que estão na maior camada e entrega o resultado:

```java
rocket.derrotar();
Vertice respawn = bfs.escolherRespawnDistante(grafo, origem);
rocket.reativarEm(respawn);
```

`EquipeRocket` não implementa a BFS e não escolhe aleatoriamente um destino.

## Inscrição na Liga

O programa principal usa a exigência oficial de oito insígnias distintas:

```java
boolean permitido = jornada.podeSeInscreverNaLiga();
```

A jornada verifica posição no Estádio, quantidade de insígnias e prazo.

## Ovos e incubadora

Os itens do tipo `OVO` encontrados ficam aguardando a decisão do jogador no
`ControladorJogo`. Ao aceitar, o item é convertido em `OvoPokemon`; ao recusar,
é removido do inventário.

O treinador pode manter vários ovos, desde que a soma de Pokémon ativos, ovos
e recém-nascidos aguardando escolha não ultrapasse sete. Apenas um ovo utiliza
a incubadora por vez. O `ObservadorJornada` repassa o tempo de cada trecho e,
ao acumular 100 unidades, o ovo choca com zero XP.

Com menos de seis Pokémon ativos, o recém-nascido entra diretamente na equipe.
Com seis, ele aguarda a escolha entre substituir um Pokémon atual ou ser enviado
ao Professor Carvalho.
