package streaming.models;

import edu.princeton.cs.algs4.Bag;
import java.time.LocalDate;

/**
 * Representa um utilizador/cliente na plataforma de streaming.
 * Armazena metadados de perfil, preferências e relações sociais internas (seguidores).
 */
public class User extends Entity {
    private String name;
    private String email;
    private String region;
    private LocalDate registrationDate;
    private Bag<Integer> followingUserIds; //Relação Follower -> User

    /**
     * Construtor completo para a instância de um Utilizador.
     * @param id Identificador único.
     * @param name Nome completo do cliente.
     * @param email Endereço de correio eletrónico institucional.
     * @param region Região geográfica para segmentação.
     * @param registrationDate Data de adesão ao sistema.
     */
    public User(int id, String name, String email, String region, LocalDate registrationDate) {
        super(id);
        this.name = name;
        this.email = email;
        this.region = region;
        this.registrationDate = registrationDate;
        this.followingUserIds = new Bag<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    /**
     * Regista que este utilizador passou a seguir outro utilizador.
     * @param userId ID do utilizador a ser seguido.
     */
    public void followUser(int userId) { followingUserIds.add(userId); }

    /**
     * Obtém a coleção de IDs dos utilizadores seguidos por este perfil.
     * @return Iterable com os IDs configurados.
     */
    public Iterable<Integer> getFollowingUserIds() { return followingUserIds; }
}