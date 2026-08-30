package grafo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//importações dos esboços (stubs) criados na pasta modelo
import modelo.Pokemon;
import modelo.Treinador;
import modelo.Item;

//representa um vértice no mapa da região, contendo os registros de entidades presentes

public class Vertice {
    private String id;
    private String nome;
    private TipoVertice tipo;

    //listas dinâmicas permitidas
    private List<Pokemon> pokemonsSelvagens;
    private List<Treinador> treinadoresPresentes;
    private List<Item> itensDisponiveis;

    //construtor para inicializar as estruturas do vértice
    public Vertice(String id, String nome, TipoVertice tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.pokemonsSelvagens = new ArrayList<>();
        this.treinadoresPresentes = new ArrayList<>();
        this.itensDisponiveis = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoVertice getTipo() { return tipo; }

    //getters para que os outros integrantes usem na jornada e combate
    public List<Pokemon> getPokemonsSelvagens() { return pokemonsSelvagens; }
    public List<Treinador> getTreinadoresPresentes() { return treinadoresPresentes; }
    public List<Item> getItensDisponiveis() { return itensDisponiveis; }

    //métodos utilitários para adicionar e remover entidades dinamicamente
    public void adicionarPokemon(Pokemon p) { this.pokemonsSelvagens.add(p); }
    public void removerPokemon(Pokemon p) { this.pokemonsSelvagens.remove(p); }

    public void adicionarTreinador(Treinador t) { this.treinadoresPresentes.add(t); }
    public void removerTreinador(Treinador t) { this.treinadoresPresentes.remove(t); }

    public void adicionarItem(Item i) { this.itensDisponiveis.add(i); }
    public void removerItem(Item i) { this.itensDisponiveis.remove(i); }

    //para o grafo (HashMap) funcionar corretamente ao comparar vértices
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        } 
        Vertice vertice = (Vertice) o;
        return Objects.equals(id, vertice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); //dois vértices com o mesmo identificador gerem o mesmo código hash
    }
}