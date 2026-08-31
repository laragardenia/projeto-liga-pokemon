# Rumo à Liga Pokémon

Projeto final da disciplina de Algoritmos em Grafos. O mapa é representado por
um grafo não direcionado e ponderado, no qual os pesos indicam o tempo de
viagem.

## Requisitos

- JDK 17 ou superior;
- VS Code com o Extension Pack for Java, Eclipse ou terminal.

## Executar no VS Code

1. Abra a pasta do repositório;
2. Abra src/Main.java;
3. Clique em **Run** acima do método `main`.

O programa usa `mapa_regiao.txt` por padrão e abre um menu interativo completo.
Por ele é possível viajar, planejar rotas, capturar Pokémon, batalhar, coletar
insígnias, usar itens, incubar ovos, evoluir a equipe e se inscrever na Liga.

## Compilar e executar pelo terminal

No Windows:

```cmd
mkdir out
javac -encoding UTF-8 -d out src\Main.java src\algoritmos\*.java src\grafo\*.java src\jogo\*.java src\modelo\*.java src\simulacao\*.java src\utilidades\*.java
java -cp out Main mapa_regiao.txt
```

Digite o número de uma opção e pressione Enter. Para encerrar, escolha `0`.

Também é possível executar uma viagem direta, sem abrir o menu, informando o
ID de um destino como segundo argumento:

```cmd
java -cp out Main mapa_regiao.txt V2
```

## Principais módulos

- `grafo`: representação por listas de adjacências;
- `algoritmos`: Dijkstra, BFS e DFS;
- `simulacao`: jornada e estado da Equipe Rocket;
- `jogo`: batalhas e controlador de integração;
- `utilidades`: leitura do mapa em arquivo texto;

## Vídeo de apresentação — Parte 2

[Assista ao vídeo de apresentação da Parte 2](https://youtu.be/dATEffkqFAw)
