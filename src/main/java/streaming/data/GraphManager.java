package streaming.data;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.ST;
import streaming.models.User;
import streaming.models.Content;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor do Grafo (Subgrafos múltiplos) (Listagens completas).
 */
public class GraphManager {
    //traduce
    private final ST<String, Integer> symbolToIndex;
    private final ST<Integer, String> indexToSymbol;
    private EdgeWeightedDigraph masterGraph;
    private int vertexCount;

    // Estrutura para auditar metadados das arestas temporais
    private static class EdgeMetadata {
        String type;
        LocalDate date;
        double metric;

        EdgeMetadata(String type, LocalDate date, double metric) {
            this.type = type;
            this.date = date;
            this.metric = metric;
        }
    }

    private final ST<String, EdgeMetadata> edgeRegistry;

    public GraphManager() {
        this.symbolToIndex = new ST<>();
        this.indexToSymbol = new ST<>();
        this.masterGraph = new EdgeWeightedDigraph(2000); // Capacidade inicial ampla
        this.vertexCount = 0;
        this.edgeRegistry = new ST<>();
    }
//añade a las tablas
    public void registerVertex(String key) {
        if (!symbolToIndex.contains(key)) {
            symbolToIndex.put(key, vertexCount);
            indexToSymbol.put(vertexCount, key);
            vertexCount++;
        }
    }

    public int getIndex(String key) {
        registerVertex(key);
        return symbolToIndex.get(key);
    }

    public String getEntityKey(int index) {
        return indexToSymbol.get(index);
    }

    public EdgeWeightedDigraph getMasterGraph() { return masterGraph; }

    public void addComplexEdge(String from, String to, double weight, String type, LocalDate date, double metric) {
        int v = getIndex(from);
        int w = getIndex(to);
        DirectedEdge edge = new DirectedEdge(v, w, weight);
        masterGraph.addEdge(edge);
        edgeRegistry.put(v + "->" + w, new EdgeMetadata(type, date, metric));
    }

    /**
     * Caminhos Mínimos
     */
    public Iterable<DirectedEdge> getShortestPath(String fromKey, String toKey) {
        if (!symbolToIndex.contains(fromKey) || !symbolToIndex.contains(toKey)) return null;
        int source = symbolToIndex.get(fromKey);
        DijkstraSP dsp = new DijkstraSP(masterGraph, source);
        int target = symbolToIndex.get(toKey);
        return dsp.hasPathTo(target) ? dsp.pathTo(target) : null;
    }

    /**
     * Extração de Subgrafos (Filtros por Género ou Região) pega cuando coincide con la busqueda
     */
    public EdgeWeightedDigraph extractSubgraphByGenre(String genre, StreamingDatabase db) {
        EdgeWeightedDigraph sub = new EdgeWeightedDigraph(vertexCount);
        for (DirectedEdge e : masterGraph.edges()) {
            String src = getEntityKey(e.from());
            String tgt = getEntityKey(e.to());
            if (src.startsWith("CONTENT_")) {
                int id = Integer.parseInt(src.split("_")[1]);
                Content c = db.getContent(id);
                if (c != null && c.getGenre().equalsIgnoreCase(genre)) {
                    sub.addEdge(e);
                }
            }
        }
        return sub;
    }
//para usuarios
    public EdgeWeightedDigraph extractSubgraphByRegion(String region, StreamingDatabase db) {
        EdgeWeightedDigraph sub = new EdgeWeightedDigraph(vertexCount);
        for (DirectedEdge e : masterGraph.edges()) {
            String src = getEntityKey(e.from());
            if (src.startsWith("USER_")) {
                int id = Integer.parseInt(src.split("_")[1]);
                User u = db.getUser(id);
                if (u != null && u.getRegion().equalsIgnoreCase(region)) {
                    sub.addEdge(e);
                }
            }
        }
        return sub;
    }
//determina si el sub grafo es valido
    public boolean isGraphConnected(EdgeWeightedDigraph g) {
        return g.E() > 0;
    }

    public boolean isMasterConnected() { return vertexCount > 0; }

    //CONSULTAS DE REDE TEMPORAIS

    public int countDocumentaryViewsInPeriod(int contentId, LocalDate start, LocalDate end) {
        int count = 0;
        String edgeTarget = "CONTENT_" + contentId;
        if (!symbolToIndex.contains(edgeTarget)) return 0;
        int tIndex = symbolToIndex.get(edgeTarget);

        for (DirectedEdge e : masterGraph.edges()) {
            if (e.to() == tIndex) {
                EdgeMetadata meta = edgeRegistry.get(e.from() + "->" + e.to());
                if (meta != null && "VIEW".equals(meta.type) && !meta.date.isBefore(start) && !meta.date.isAfter(end)) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<User> getUsersWhoWatchedSeriesByGenreInPeriod(String genre, LocalDate start, LocalDate end, StreamingDatabase db) {
        List<User> list = new ArrayList<>();
        for (DirectedEdge e : masterGraph.edges()) {
            String srcKey = getEntityKey(e.from());
            String tgtKey = getEntityKey(e.to());
            if (srcKey.startsWith("USER_") && tgtKey.startsWith("CONTENT_")) {
                EdgeMetadata meta = edgeRegistry.get(e.from() + "->" + e.to());
                if (meta != null && "VIEW".equals(meta.type) && !meta.date.isBefore(start) && !meta.date.isAfter(end)) {
                    int cId = Integer.parseInt(tgtKey.split("_")[1]);
                    Content c = db.getContent(cId);
                    if (c != null && "Series".equalsIgnoreCase(c.getType()) && c.getGenre().equalsIgnoreCase(genre)) {
                        int uId = Integer.parseInt(srcKey.split("_")[1]);
                        User u = db.getUser(uId);
                        if (u != null && !list.contains(u)) list.add(u);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Devolve explicitamente a coleção iterável com os utilizadores reais
     */
    public Iterable<User> getFollowersWhoWatchedSameContent(int userId, int contentId, LocalDate start, LocalDate end, StreamingDatabase db) {
        List<User> followersList = new ArrayList<>();
        String userKey = "USER_" + userId;
        String contentKey = "CONTENT_" + contentId;

        if (!symbolToIndex.contains(userKey) || !symbolToIndex.contains(contentKey)) return followersList;
        int uIndex = symbolToIndex.get(userKey);
        int cIndex = symbolToIndex.get(contentKey);

        // 1. Encontrar quem segue o userId de destino
        for (DirectedEdge e : masterGraph.edges()) {
            if (e.to() == uIndex) { // Alguém aponta para o utilizador (Seguidor)
                EdgeMetadata metaFollow = edgeRegistry.get(e.from() + "->" + e.to());
                if (metaFollow != null && "FOLLOW".equals(metaFollow.type)) {
                    int followerIdx = e.from();

                    // 2. Verificar se este seguidor viu o conteúdo no período
                    EdgeMetadata metaView = edgeRegistry.get(followerIdx + "->" + cIndex);
                    if (metaView != null && "VIEW".equals(metaView.type) && !metaView.date.isBefore(start) && !metaView.date.isAfter(end)) {
                        String followerKey = getEntityKey(followerIdx);
                        int fId = Integer.parseInt(followerKey.split("_")[1]);
                        User followerObj = db.getUser(fId);
                        if (followerObj != null) followersList.add(followerObj);
                    }
                }
            }
        }
        return followersList;
    }
//numero entero de seguidores que han visto
    @Deprecated
    public int countFollowersWhoWatchedSameContent(int userId, int contentId, LocalDate start, LocalDate end, StreamingDatabase db) {
        int count = 0;
        for (User ignored : getFollowersWhoWatchedSameContent(userId, contentId, start, end, db)) {
            count++;
        }
        return count;
    }
}