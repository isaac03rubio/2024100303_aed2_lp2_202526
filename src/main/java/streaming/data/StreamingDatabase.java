package streaming.data;

import edu.princeton.cs.algs4.RedBlackBST;
import streaming.models.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Base de dados centralizada usando Árvores Binárias de Procura Equilibradas (RedBlackBST).
 * (Consultas Avançadas) (Arquivamento) e (Historial).
 */
public class StreamingDatabase {

    private final RedBlackBST<Integer, User> usersTable;
    private final RedBlackBST<Integer, Artist> artistsTable;
    private final RedBlackBST<Integer, Content> contentsTable;

    public StreamingDatabase() {
            this.usersTable = new RedBlackBST<>();
            this.artistsTable = new RedBlackBST<>();
            this.contentsTable = new RedBlackBST<>();
    }
//guarda, recibe y da los id
    public void addUser(User u) { usersTable.put(u.getId(), u); }
    public User getUser(int id) { return usersTable.get(id); }
    public Iterable<Integer> getAllUserIds() { return usersTable.keys(); }

    public void addArtist(Artist a) { artistsTable.put(a.getId(), a); }
    public Artist getArtist(int id) { return artistsTable.get(id); }
    public Iterable<Integer> getAllArtistIds() { return artistsTable.keys(); }

    public void addContent(Content c) { contentsTable.put(c.getId(), c); }
    public Content getContent(int id) { return contentsTable.get(id); }
    public Iterable<Integer> getAllContentIds() { return contentsTable.keys(); }

    /**
     * Remoção em Cascata com Historial de Arquivamento de Segurança
     */
    public void removeUser(int id) {
        User u = usersTable.get(id);
        if (u != null) {
            // Guardar no ficheiro de lixeira/arquivo de segurança
            try (PrintWriter out = new PrintWriter(new FileWriter("data/archivado_eliminados.txt", true))) {
                out.println(String.format("USER;%d;%s;%s;%s;%s", u.getId(), u.getName(), u.getEmail(), u.getRegion(), LocalDate.now()));
            } catch (IOException e) {
                System.err.println("Erro ao arquivar utilizador removido: " + e.getMessage());
            }
            usersTable.delete(id);
        }
    }

    /**
     * Registo centralizado do Historial de Pesquisas efetuadas na Interface
     */
    public void registerSearchHistory(String queryCriteria) {
        try (PrintWriter out = new PrintWriter(new FileWriter("data/búsquedas_historial.txt", true))) {
            out.println(LocalDate.now() + " | Pesquisa realizada: " + queryCriteria);
        } catch (IOException e) {
            System.err.println("Erro ao gravar histórico de auditoria: " + e.getMessage());
        }
    }
//busca hasta encontrar coincidencias
    public List<User> searchUsersAdvanced(String region, LocalDate regDate, String nameSub) {
        registerSearchHistory(String.format("Utilizadores [Região: %s, Data: %s, Substring: %s]", region, regDate, nameSub));
        List<User> results = new ArrayList<>();
        for (Integer id : usersTable.keys()) {
            User u = usersTable.get(id);
            if (region != null && !region.isEmpty() && !u.getRegion().equalsIgnoreCase(region)) continue;
            if (regDate != null && u.getRegistrationDate().isBefore(regDate)) continue;
            if (nameSub != null && !nameSub.isEmpty() && !u.getName().toLowerCase().contains(nameSub.toLowerCase())) continue;
            results.add(u);
        }
        return results;
    }

    public List<Artist> searchArtistsAdvanced(String nationality, String genreContentPreference, int minAge, int maxAge, String nameSub) {
        registerSearchHistory(String.format("Artistas [Nacionalidade: %s, Idades: %d-%d]", nationality, minAge, maxAge));
        List<Artist> results = new ArrayList<>();
        for (Integer id : artistsTable.keys()) {
            Artist a = artistsTable.get(id);
            if (nationality != null && !nationality.isEmpty() && !a.getNationality().equalsIgnoreCase(nationality)) continue;
            if (nameSub != null && !nameSub.isEmpty() && !a.getName().toLowerCase().contains(nameSub.toLowerCase())) continue;

            int age = Period.between(a.getBirthDate(), LocalDate.now()).getYears();
            if (age < minAge || age > maxAge) continue;

            results.add(a);
        }
        return results;
    }

    public List<Content> searchContentAdvanced(String type, String genre, Integer maxDuration, Integer year, String titleSub) {
        registerSearchHistory(String.format("Conteúdos [Tipo: %s, Género: %s, Ano: %s]", type, genre, year));
        List<Content> results = new ArrayList<>();
        for (Integer id : contentsTable.keys()) {
            Content c = contentsTable.get(id);
            if (type != null && !type.isEmpty() && !c.getType().equalsIgnoreCase(type)) continue;
            if (genre != null && !genre.isEmpty() && !c.getGenre().equalsIgnoreCase(genre)) continue;
            if (maxDuration != null && c.getDurationMinutes() > maxDuration) continue;
            if (year != null && c.getReleaseDate().getYear() != year) continue;
            if (titleSub != null && !titleSub.isEmpty() && !c.getName().toLowerCase().contains(titleSub.toLowerCase())) continue;
            results.add(c);
        }
        return results;
    }

    @Deprecated
    public List<User> searchUsersByRegionAndName(String region, String name) {
        return searchUsersAdvanced(region, null, name);
    }
}