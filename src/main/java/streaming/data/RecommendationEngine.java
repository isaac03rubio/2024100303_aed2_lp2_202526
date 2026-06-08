package streaming.data;

import edu.princeton.cs.algs4.DirectedEdge;
import streaming.models.Content;
import streaming.models.User;
import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {
    public static List<Content> getStructuralRecommendations(int userId, StreamingDatabase db, GraphManager gm) {
        List<Content> recs = new ArrayList<>();
        String userKey = "USER_" + userId;
        int uIdx = gm.getIndex(userKey);

        for (DirectedEdge e : gm.getMasterGraph().edges()) {
            if (e.from() == uIdx) {
                String targetKey = gm.getEntityKey(e.to());
                if (targetKey.startsWith("CONTENT_")) {
                    int cId = Integer.parseInt(targetKey.split("_")[1]);
                    Content c = db.getContent(cId);
                    if (c != null) recs.add(c);
                }
            }
        }
        return recs;
    }
}

//extraer recomendaciones estructurales analizando las aristas que parten del vértice del usuario