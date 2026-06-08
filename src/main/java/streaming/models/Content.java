package streaming.models;

import edu.princeton.cs.algs4.Bag;
import java.time.LocalDate;

/**
 * Modela qualquer conteúdo multimédia disponível no catálogo (Filme, Série, Documentário).
 */
public class Content extends Entity {
    private String name;
    private String genre;
    private String type; // Movie, Series, Documentary
    private LocalDate releaseDate;
    private int durationMinutes; // filtros
    private Bag<Integer> artistIds; //Artistas que participaram

    /**
     * Construtor completo (Com duração).
     */
    public Content(int id, String name, String genre, String type, LocalDate releaseDate, int durationMinutes) {
        super(id);
        this.name = name;
        this.genre = genre;
        this.type = type;
        this.releaseDate = releaseDate;
        this.durationMinutes = durationMinutes;
        this.artistIds = new Bag<>();
    }

    /**
     * CONSTRUTOR SOBRECARREGADO (Compatibilidade com Main e Parsers antigos).
     * Define automaticamente 120 minutos como padrão.
     */
    public Content(int id, String name, String genre, String type, LocalDate releaseDate) {
        this(id, name, genre, type, releaseDate, 120);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public void addArtistParticipation(int artistId) { this.artistIds.add(artistId); }
    public Iterable<Integer> getArtistIds() { return artistIds; }
}