package streaming.models;

import edu.princeton.cs.algs4.Bag;
import java.time.LocalDate;

/**
 * Modela os intervenientes criativos do ecossistema (Atores, Realizadores, Diretores).
 */
public class Artist extends Entity {
    private String name;
    private String nationality;
    private LocalDate birthDate;
    private Bag<Integer> contentIds; // R7: Conteúdos em que o artista participou

    public Artist(int id, String name, String nationality, LocalDate birthDate) {
        super(id);
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.contentIds = new Bag<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public void addContentParticipation(int contentId) { this.contentIds.add(contentId); }
    public Iterable<Integer> getContentIds() { return contentIds; }
}