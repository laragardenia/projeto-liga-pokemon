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
decorrido e os itens coletados. O módulo de batalha pode usar esse ponto para
detectar encontros, mas as regras de combate não pertencem à jornada.

Depois de uma vitória em ginásio, o módulo responsável informa a conquista:

```java
jornada.registrarInsignia("CODIGO_DA_INSIGNIA");
```

## Equipe Rocket e BFS

`EquipeRocket` movimenta-se por um único vértice adjacente com `moverPara`.
Depois de uma derrota, outro módulo calcula o respawn e entrega o resultado:

```java
rocket.derrotar();
Vertice respawn = calcularRespawnComBfs(grafo, origem);
rocket.reativarEm(respawn);
```

`EquipeRocket` não implementa a BFS e não escolhe aleatoriamente um destino.

## Inscrição na Liga

O programa principal informa a quantidade de insígnias exigida:

```java
boolean permitido = jornada.podeSeInscreverNaLiga(quantidadeNecessaria);
```

A jornada verifica posição no Estádio, quantidade de insígnias e prazo.
