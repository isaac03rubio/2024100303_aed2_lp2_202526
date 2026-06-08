package streaming.data;

import streaming.models.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Gestor de Persistência e Carregamento de Ficheiros de Texto de Carga Inicial.
 */
public class StorageRepository {

    public static void loadAllFromTxt(String folderPath, StreamingDatabase db, GraphManager gm) {
        loadInitialData(db, gm);
    }

    /**
     * Carregamento em lote
     * Suporta nativamente separadores por vírgula (,)
     */
    public static void loadInitialData(StreamingDatabase db, GraphManager gm) {
        // 1. Carregar Utilizadores
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("[,;]");

                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                String email = tokens[2].trim();
                String region = tokens[3].trim();
                LocalDate date = tokens.length > 4 ? LocalDate.parse(tokens[4].trim()) : LocalDate.now();

                User u = new User(id, name, email, region, date);
                db.addUser(u);
                gm.registerVertex("USER_" + id);
            }
        } catch (IOException e) {
            System.err.println("Aviso no carregamento de utilizadores: " + e.getMessage());
        }

        // 2. Carregar Conteúdos
        try (BufferedReader br = new BufferedReader(new FileReader("data/contents.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("[,;]");

                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                String genre = tokens[2].trim();
                String type = tokens[3].trim();
                LocalDate date = LocalDate.parse(tokens[4].trim());

                int duration = 120; // Valor por defeito
                if (tokens.length > 5) {
                    try {
                        duration = Integer.parseInt(tokens[5].trim());
                    } catch (NumberFormatException ignored) {}
                }

                Content c = new Content(id, name, genre, type, date, duration);
                db.addContent(c);
                gm.registerVertex("CONTENT_" + id);
            }
        } catch (IOException e) {
            System.err.println("Aviso no carregamento de conteúdos: " + e.getMessage());
        }

        // 3. Carregar Artistas
        try (BufferedReader br = new BufferedReader(new FileReader("data/artists.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("[,;]");

                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                String nat = tokens[2].trim();
                LocalDate birth = LocalDate.parse(tokens[3].trim());

                Artist a = new Artist(id, name, nat, birth);
                db.addArtist(a);
                gm.registerVertex("ARTIST_" + id);
            }
        } catch (IOException e) {
            System.err.println("Aviso no carregamento de artistas: " + e.getMessage());
        }

        // Vincula utilizadores simulados (como ID 1 de Porto) a conteúdos do catálogo
        try {
            // Vincula o utilizador 1 (Ana Silva - Porto) assistindo aos novos conteúdos de Sci-Fi
            gm.addComplexEdge("USER_1", "CONTENT_100", 2.5, "VIEW", LocalDate.now(), 169.0);
            gm.addComplexEdge("USER_1", "CONTENT_104", 1.8, "VIEW", LocalDate.now(), 50.0);
            gm.addComplexEdge("USER_1", "CONTENT_106", 3.0, "VIEW", LocalDate.now(), 148.0);
            gm.addComplexEdge("USER_1", "CONTENT_110", 2.7, "VIEW", LocalDate.now(), 164.0);

            // Vincula conexões de artistas com conteúdos para cruzar dados no grafo
            gm.addComplexEdge("ARTIST_500", "CONTENT_100", 1.0, "DIRECTION", LocalDate.now(), 0.0);
            gm.addComplexEdge("ARTIST_507", "CONTENT_108", 1.0, "DIRECTION", LocalDate.now(), 0.0);
        } catch (Exception ignored) {
            // Protegido caso os IDs não coincidam em testes isolados
        }
    }
}