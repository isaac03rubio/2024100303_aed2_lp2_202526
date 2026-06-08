package streaming;

import streaming.data.*;
import streaming.models.*;
import java.time.LocalDate;

/**
 * Suite Autónoma de Validação e Testes Unitários.
 * Valida o comportamento isolado e os outputs das estruturas sem intervenção manual.
 */
public class StreamingCoreTest {

    public static void main(String[] args) {
        System.out.println("====== INICIANDO UNIDADES DE TESTE AUTOMATIZADAS ======");
        boolean r2Ok = testSymbolTablePersistence();
        boolean r4Ok = testCascadingConsistency();
        boolean r8Ok = testStructuralGraphLogic();

        System.out.println("\n=======================================================");
        if (r2Ok && r4Ok && r8Ok) {
            System.out.println("   RESULTADO FINAL: 100% DOS CASOS DE TESTE COM SUCESSO");
        } else {
            System.out.println("   RESULTADO FINAL: FALHA ENCONTRADA NA SUITE DE TESTES");
        }
        System.out.println("=======================================================");
    }

    public static boolean testSymbolTablePersistence() {
        System.out.print("Inserção e indexação por Árvores Binárias... ");
        StreamingDatabase db = new StreamingDatabase();
        User u = new User(10, "Diana Prince", "diana@ufp.edu.pt", "Braga", LocalDate.now());
        db.addUser(u);

        User recovered = db.getUser(10);
        if (recovered != null && "Diana Prince".equals(recovered.getName())) {
            System.out.println("OK");
            return true;
        }
        System.out.println("FALHA");
        return false;
    }

    public static boolean testCascadingConsistency() {
        System.out.print("[Remoção consistente e integridade estrutural... ");
        StreamingDatabase db = new StreamingDatabase();
        User u = new User(20, "Clark Kent", "clark@ufp.edu.pt", "Porto", LocalDate.now());
        db.addUser(u);

        // Remover
        db.removeUser(20);
        if (db.getUser(20) == null) {
            System.out.println("OK");
            return true;
        }
        System.out.println("FALHA");
        return false;
    }

    public static boolean testStructuralGraphLogic() {
        System.out.print("Conectividade de rede e caminhos mínimos Dijkstra... ");
        StreamingDatabase db = new StreamingDatabase();
        GraphManager gm = new GraphManager();

        gm.addComplexEdge("USER_1", "CONTENT_100", 1.0, "VIEW", LocalDate.now(), 4.5);
        gm.addComplexEdge("CONTENT_100", "ARTIST_500", 0.5, "DIRECTION", LocalDate.now(), 0.0);

        var path = gm.getShortestPath("USER_1", "ARTIST_500");
        if (path != null && gm.isMasterConnected()) {
            System.out.println("OK");
            return true;
        }
        System.out.println("FALHA");
        return false;
    }
}