package streaming.main;

import streaming.data.*;
import streaming.gui.MainWindow;
import streaming.models.*;
import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        StreamingDatabase db = new StreamingDatabase();
        GraphManager gm = new GraphManager();

        // 1. Carregamento em lote obrigatório
        StorageRepository.loadAllFromTxt("data", db, gm);

        // 2. Injetar massa de dados complexa para simular relações heterogéneas reais
        User u1 = new User(1, "Ana Silva", "ana@ufp.edu.pt", "Porto", LocalDate.now());
        User u2 = new User(2, "Bruno Costa", "bruno@ufp.edu.pt", "Lisboa", LocalDate.now());
        u2.followUser(1);

        db.addUser(u1);
        db.addUser(u2);

        Content c1 = new Content(100, "Interstellar", "Sci-Fi", "Movie", LocalDate.of(2014, 11, 7));
        Content c2 = new Content(101, "Inception", "Sci-Fi", "Movie", LocalDate.of(2010, 7, 16));
        db.addContent(c1);
        db.addContent(c2);

        Artist a1 = new Artist(500, "Christopher Nolan", "UK", LocalDate.of(1970, 7, 30));
        db.addArtist(a1);

        // Conectar arestas heterogéneas multi-relacionais reais com classificações e datas
        gm.addComplexEdge("USER_1", "CONTENT_100", 1.0, "VIEW", LocalDate.of(2026, 5, 15), 4.8);
        gm.addComplexEdge("USER_2", "CONTENT_100", 1.0, "VIEW", LocalDate.of(2026, 5, 20), 5.0);
        gm.addComplexEdge("CONTENT_100", "ARTIST_500", 0.5, "DIRECTION", LocalDate.of(2014, 11, 7), 0.0);

        // 3. Lançar Ambiente Visual da Capa de Aplicação
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(db, gm);
            window.setVisible(true);
        });
    }
}