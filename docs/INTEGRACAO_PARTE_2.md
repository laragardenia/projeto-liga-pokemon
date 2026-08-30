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
