# Rumo à Liga Pokémon 🎮🏆

Projeto Final da Disciplina de Algoritmos em Grafos
Universidade Federal do Cariri (UFCA) — Semestre 2026.1
Professor: Carlos Vinícius G. C. Lima


## 📌 Link do Repositório
Repositório Oficial (main): github.com/laragardenia/projeto-liga-pokemon

## 📖 Sobre o Projeto
O Rumo à Liga Pokémon é um sistema interativo de simulação de RPG em ambiente de grafos, desenvolvido em Java.
O mapa da região do jogo é carregado a partir do arquivo de entrada padrão mapa_regiao.txt. Este mapa é modelado como um Grafo Não-Direcionado, Ponderado e Conexo, no qual:

Vértices (Nós): Representam os pontos físicos de interesse da região (Laboratório do Professor Carvalho, Cidades/Ginásios, Centro Médico Pokémon - MCP e o Estádio da Liga).
Arestas (Caminhos): Representam as rotas bidirecionais que conectam esses locais.
Pesos: Representam o tempo de viagem (em unidades de tempo) necessário para percorrer cada caminho.

## 📁 Estrutura do Projeto (Arquitetura de Pastas)
A árvore de diretórios do projeto segue a seguinte organização de pacotes e arquivos:

```text
projeto-liga-pokemon/
├── src/
│   ├── algoritmos/            # Algoritmos de busca e rotas (BFS, DFS, Dijkstra, etc.)
│   │   ├── BFS.java
│   │   ├── DFS.java
│   │   ├── Dijkstra.java
│   │   └── ResultadoCaminho.java
│   ├── grafo/                 # Estruturas do Grafo (Vértice, Aresta e Lista de Adjacência)
│   │   ├── Aresta.java
│   │   ├── Grafo.java
│   │   ├── TipoVertice.java
│   │   └── Vertice.java
│   ├── jogo/                  # Controle do fluxo interativo, batalhas e menu
│   │   ├── ControladorJogo.java
│   │   ├── MenuJogo.java
│   │   ├── ResultadoBatalha.java
│   │   └── SistemaBatalha.java
│   ├── modelo/                # Entidades do RPG (Pokemon, Treinador, Item, Ovos)
│   │   ├── Item.java
│   │   ├── OvoPokemon.java
│   │   ├── Pokemon.java
│   │   ├── TipoItem.java
│   │   ├── TipoPokemon.java
│   │   └── Treinador.java
│   ├── simulacao/             # Regras da jornada, tempo e inteligência da Equipe Rocket
│   │   ├── EquipeRocket.java
│   │   ├── JornadaPokemon.java
│   │   └── ObservadorJornada.java
│   ├── utilidades/            # Leitura e parsing do arquivo texto de entrada
│   │   └── LeitorArquivo.java
│   └── Main.java              # Ponto de entrada da aplicação
├── .gitignore                 # Configuração de arquivos ignorados pelo Git
├── LICENSE                    # Licença do repositório
├── mapa_regiao.txt            # Arquivo de entrada com a descrição do grafo e entidades
└── README.md                  # Documentação principal do projeto
```

## Requisitos

- JDK 17 ou superior instalado.
- VS Code com o Extension Pack for Java, Eclipse ou terminal de sua preferência.

## Executar no VS Code

1. Abra a pasta do repositório (projeto-liga-pokemon) no VS Code.
2. Abra o arquivo src/Main.java.
3. Clique em **Run** acima do método `main`.

O programa usa `mapa_regiao.txt` por padrão e abre um menu interativo completo.
Por ele é possível viajar, planejar rotas, capturar Pokémon, batalhar, coletar
insígnias, usar itens, incubar ovos, evoluir a equipe e se inscrever na Liga.

## Compilar e Executar pelo Terminal

No Windows (CMD/PowerShell):

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


## 📹 Vídeos de Demonstração 


[Assista ao vídeo de apresentação da Parte 2](https://youtu.be/dATEffkqFAw)
