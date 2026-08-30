# Rumo à Liga Pokémon

Projeto final da disciplina de Algoritmos em Grafos. O mapa é representado por
um grafo não direcionado e ponderado, no qual os pesos indicam o tempo de
viagem.

## Requisitos

- JDK 17 ou superior;
- VS Code com o Extension Pack for Java, Eclipse ou terminal.

## Executar no VS Code

1. Abra a pasta do repositório.
2. Abra `src/Main.java`.
3. Clique em **Run** acima do método `main`.

O programa usa `mapa_regiao.txt` por padrão. Para carregar outro mapa ou
viajar diretamente para um vértice, informe os argumentos pela configuração de
execução ou pelo terminal.

## Compilar e executar pelo terminal

No Windows:

```cmd
mkdir out
javac -encoding UTF-8 -d out src\Main.java src\algoritmos\*.java src\grafo\*.java src\jogo\*.java src\modelo\*.java src\simulacao\*.java src\teste\*.java src\utilidades\*.java
java -cp out Main mapa_regiao.txt V2
```

O segundo argumento (`V2`) é opcional e representa o identificador do destino.

## Principais módulos

- `grafo`: representação por listas de adjacências;
- `algoritmos`: Dijkstra, BFS e DFS;
- `simulacao`: jornada e estado da Equipe Rocket;
- `jogo`: batalhas e controlador de integração;
- `utilidades`: leitura do mapa em arquivo texto;
- `teste`: testes executáveis sem dependência de JUnit.

## Testes

Depois da compilação, execute:

```cmd
java -cp out teste.TesteDijkstra
java -cp out teste.TesteJornadaPokemon
java -cp out teste.TesteEquipeRocket
java -cp out teste.TesteIntegracaoParte2
java -cp out teste.TesteIntegracaoFinal
java -cp out teste.TesteOvosIncubadora
```

O teste final valida conjuntamente jornada, uso de erva, batalha contra líder,
insígnia, batalha contra a Equipe Rocket e respawn pela BFS.

O teste de ovos valida a decisão de aceitar ou recusar, o uso exclusivo da
incubadora, o nascimento após 100 unidades e os limites de seis Pokémon ativos
e sete no total contando ovos.
