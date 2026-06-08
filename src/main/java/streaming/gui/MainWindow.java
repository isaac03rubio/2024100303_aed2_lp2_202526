package streaming.gui;

import streaming.data.*;
import streaming.models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * MainWindow - Interface Gráfica
 */
public class MainWindow extends JFrame {

    private final StreamingDatabase db;
    private final GraphManager gm;

    private final JTextArea outputArea;
    private final JTextField txtUserId, txtUserName, txtUserEmail, txtUserRegion;
    private final JTextField searchRegion, searchName;
    private final JComboBox<String> genreBox;

    // Campos para os filtros temporais
    private final JTextField txtTargetContentId;
    private final JTextField txtStartDate;
    private final JTextField txtEndDate;

    public MainWindow(StreamingDatabase db, GraphManager gm) {
        this.db = db;
        this.gm = gm;

        setTitle("UFP Streaming Graph Recommendation Engine - Advanced Academic Version");
        setSize(1250, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(12, 15, 12, 15));
        JLabel title = new JLabel("PANEL DE CONTROL & AUDITORÍA DE DATOS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- PAINEL LATERAL ESQUERDO ---
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        leftContainer.setPreferredSize(new Dimension(400, 0));

        // 1. Panel Formulario
        JPanel crudPanel = new JPanel(new GridLayout(5, 2, 5, 6));
        crudPanel.setBorder(BorderFactory.createTitledBorder("Gestão de Utilizadores (CRUD)"));
        crudPanel.setBackground(Color.WHITE);
        crudPanel.add(new JLabel(" ID Utilizador:"));
        txtUserId = new JTextField("1");
        crudPanel.add(txtUserId);
        crudPanel.add(new JLabel(" Nome:"));
        txtUserName = new JTextField("Ana Silva");
        crudPanel.add(txtUserName);
        crudPanel.add(new JLabel(" Email:"));
        txtUserEmail = new JTextField("ana@ufp.edu.pt");
        crudPanel.add(txtUserEmail);
        crudPanel.add(new JLabel(" Região:"));
        txtUserRegion = new JTextField("Porto");
        crudPanel.add(txtUserRegion);
        JButton btnAdd = new JButton("Gravar / Editar");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        crudPanel.add(btnAdd);
        JButton btnDelete = new JButton("Remover");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        crudPanel.add(btnDelete);
        leftContainer.add(crudPanel);

        leftContainer.add(Box.createVerticalStrut(10));

        // 2. Panel Filtros Simples
        JPanel queryPanel = new JPanel(new GridLayout(4, 2, 5, 6));
        queryPanel.setBorder(BorderFactory.createTitledBorder("Herramientas de Consulta Avanzada"));
        queryPanel.setBackground(Color.WHITE);
        queryPanel.add(new JLabel(" Región del Usuario:"));
        searchRegion = new JTextField("Porto");
        queryPanel.add(searchRegion);
        queryPanel.add(new JLabel(" Substring del Nombre:"));
        searchName = new JTextField("Ana");
        queryPanel.add(searchName);
        queryPanel.add(new JLabel(" Género de Contenido:"));
        genreBox = new JComboBox<>(new String[]{"Sci-Fi", "Action", "Drama", "Documentary"});
        queryPanel.add(genreBox);
        JButton btnSearch = new JButton("Buscar Usuarios");
        btnSearch.setBackground(new Color(46, 204, 113));
        btnSearch.setForeground(Color.WHITE);
        queryPanel.add(btnSearch);
        JButton btnSubgraph = new JButton("Filtrar Contenidos");
        btnSubgraph.setBackground(new Color(155, 89, 182));
        btnSubgraph.setForeground(Color.WHITE);
        queryPanel.add(btnSubgraph);
        leftContainer.add(queryPanel);

        leftContainer.add(Box.createVerticalStrut(10));

        // 3. PANEL: CONSULTAS TEMPORAIS AVANÇADAS
        JPanel temporalPanel = new JPanel(new GridLayout(7, 2, 5, 6));
        temporalPanel.setBorder(BorderFactory.createTitledBorder("Consultas de Rede & Visualização"));
        temporalPanel.setBackground(Color.WHITE);
        temporalPanel.add(new JLabel(" ID Conteúdo Alvo:"));
        txtTargetContentId = new JTextField("100");
        temporalPanel.add(txtTargetContentId);
        temporalPanel.add(new JLabel(" Data Início (AAAA-MM-DD):"));
        txtStartDate = new JTextField("2026-05-01");
        temporalPanel.add(txtStartDate);
        temporalPanel.add(new JLabel(" Data Fim (AAAA-MM-DD):"));
        txtEndDate = new JTextField("2026-05-31");
        temporalPanel.add(txtEndDate);

        JButton btnR8e = new JButton("Views Doc");
        btnR8e.setBackground(new Color(52, 73, 94));
        btnR8e.setForeground(Color.WHITE);
        temporalPanel.add(btnR8e);

        JButton btnR8f = new JButton("User/Série");
        btnR8f.setBackground(new Color(52, 73, 94));
        btnR8f.setForeground(Color.WHITE);
        temporalPanel.add(btnR8f);

        JButton btnR8g = new JButton("Seguidores");
        btnR8g.setBackground(new Color(52, 73, 94));
        btnR8g.setForeground(Color.WHITE);
        temporalPanel.add(btnR8g);

        JButton btnRecommend = new JButton("Recomendar");
        btnRecommend.setBackground(new Color(241, 196, 15));
        btnRecommend.setForeground(Color.BLACK);
        temporalPanel.add(btnRecommend);

        JButton btnViewGraph = new JButton("Visualizar Grafo");
        btnViewGraph.setBackground(new Color(41, 128, 185));
        btnViewGraph.setForeground(Color.WHITE);
        temporalPanel.add(btnViewGraph);

        temporalPanel.add(new JLabel(""));

        leftContainer.add(temporalPanel);
        mainPanel.add(leftContainer, BorderLayout.WEST);

        // --- PANEL CENTRAL: OUTPUT ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Resultados del Motor Algorítmico"));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(25, 25, 25));
        outputArea.setForeground(new Color(57, 255, 20));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- ASIGNACIÓN DE EVENTOS (LISTENERS) ---
        btnAdd.addActionListener(e -> triggerSave());
        btnDelete.addActionListener(e -> triggerDelete());
        btnSearch.addActionListener(e -> triggerSearch());
        btnSubgraph.addActionListener(e -> triggerSubgraphAnalysis());
        btnR8e.addActionListener(e -> triggerR8e());
        btnR8f.addActionListener(e -> triggerR8f());
        btnR8g.addActionListener(e -> triggerR8g());
        btnRecommend.addActionListener(e -> triggerRecommendations());
        btnViewGraph.addActionListener(e -> triggerGraphVisualization());

        add(mainPanel);
    }

    private void triggerSave() {
        try {
            int id = Integer.parseInt(txtUserId.getText().trim());
            User u = new User(id, txtUserName.getText().trim(), txtUserEmail.getText().trim(), txtUserRegion.getText().trim(), LocalDate.now());
            db.addUser(u);
            gm.registerVertex("USER_" + id);
            outputArea.setText("[CRUD] Gravação efetuada com sucesso nas tabelas Hash/BST para o ID: " + id);
        } catch (Exception ex) {
            outputArea.setText("❌ Erro ao gravar utilizador: " + ex.getMessage());
        }
    }

    private void triggerDelete() {
        try {
            int id = Integer.parseInt(txtUserId.getText().trim());
            db.removeUser(id);
            outputArea.setText("[CONSISTÊNCIA] Utilizador ID " + id + " removido completamente e arquivado em 'data/archivado_eliminados.txt'.");
        } catch (Exception ex) {
            outputArea.setText("❌ Erro ao remover: " + ex.getMessage());
        }
    }

    private void triggerSearch() {
        outputArea.setText("====== EJECUTANDO BÚSQUEDA BINARIA BST EN USUARIOS ======\n");
        String regionInput = searchRegion.getText().trim();

        String nameInput = searchName.getText().trim();

        var res = db.searchUsersAdvanced(regionInput, null, nameInput);
        int count = 0;
        for (User u : res) {
            outputArea.append(String.format(" -> ID: %d | Nombre: %s | Región: %s | Correo: %s\n", u.getId(), u.getName(), u.getRegion(), u.getEmail()));
            count++;
        }
        outputArea.append("\n Búsqueda finalizada. Coincidencias encontradas: " + count + "\n Historial registrado en 'data/búsquedas_historial.txt' (R10).");
    }

    private void triggerSubgraphAnalysis() {
        String genre = (String) genreBox.getSelectedItem();
        outputArea.setText("====== EXTRAÇÃO AUTOMÁTICA DE SUBGRAFO POR GÉNERO ======\n");
        var sub = gm.extractSubgraphByGenre(genre, db);
        outputArea.append(" -> Arestas/Ligações recolhidas no subgrafo filtrado: " + sub.E() + "\n");
        outputArea.append(" -> O subgrafo gerado cumpre o critério de fracamente conexo?: " + (gm.isGraphConnected(sub) ? "SIM" : "NÃO") + "\n");
    }

    private void triggerR8e() {
        try {
            int cId = Integer.parseInt(txtTargetContentId.getText().trim());
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end = LocalDate.parse(txtEndDate.getText().trim());

            outputArea.setText("====== CONSULTA: ESTATÍSTICAS DE VIEW DE DOCUMENTÁRIO ======\n");
            int total = gm.countDocumentaryViewsInPeriod(cId, start, end);
            outputArea.append(String.format(" -> O conteúdo ID %d registou um total de %d visualizações no intervalo [%s a %s].\n", cId, total, start, end));
        } catch (Exception ex) {
            outputArea.setText("❌ Formato de parâmetros inválido para: " + ex.getMessage());
        }
    }

    private void triggerR8f() {
        try {
            String genre = (String) genreBox.getSelectedItem();
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end = LocalDate.parse(txtEndDate.getText().trim());

            outputArea.setText("====== CONSULTA: UTILIZADORES QUE VIRAM SÉRIES POR GÉNERO ======\n");
            var users = gm.getUsersWhoWatchedSeriesByGenreInPeriod(genre, start, end, db);
            int count = 0;
            for (User u : users) {
                outputArea.append(String.format(" -> %s (ID: %d) consumiu uma Série de %s neste intervalo.\n", u.getName(), u.getId(), genre));
                count++;
            }
            outputArea.append("\n -> Total de correspondências obtidas: " + count);
        } catch (Exception ex) {
            outputArea.setText("❌ Erro na consulta: " + ex.getMessage());
        }
    }

    private void triggerR8g() {
        try {
            int uId = Integer.parseInt(txtUserId.getText().trim());
            int cId = Integer.parseInt(txtTargetContentId.getText().trim());
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end = LocalDate.parse(txtEndDate.getText().trim());

            outputArea.setText("====== CONSULTA: LISTAGEM E ASSOCIAÇÃO DE SEGUIDORES (RETORNO ITERABLE) ======\n");
            var followers = gm.getFollowersWhoWatchedSameContent(uId, cId, start, end, db);
            int count = 0;
            for (User f : followers) {
                outputArea.append(String.format(" -> Seguidor Mapeado: %s (ID: %d) assistiu ao conteúdo no período.\n", f.getName(), f.getId()));
                count++;
            }
            outputArea.append("\n -> Total de seguidores estruturais validados: " + count);
        } catch (Exception ex) {
            outputArea.setText("❌ Erro na consulta: " + ex.getMessage());
        }
    }

    private void triggerRecommendations() {
        try {
            int id = Integer.parseInt(txtUserId.getText().trim());
            outputArea.setText("====== RECOMENDAÇÕES POR PROXIMIDADE ESTRUTURAL NO GRAFO ======\n");
            var suggestions = RecommendationEngine.getStructuralRecommendations(id, db, gm);
            int count = 0;
            for (Content c : suggestions) {
                outputArea.append(" -> Recomendado por Vizinhança: " + c.getName() + " [" + c.getGenre() + "]\n");
                count++;
            }
            if (count == 0) outputArea.append(" -> Nenhuma recomendação baseada em conexões diretas encontrada para este perfil.");
        } catch (Exception ex) {
            outputArea.setText("❌ Erro ao processar motor de recomendações: " + ex.getMessage());
        }
    }

    private void triggerGraphVisualization() {
        JDialog graphDialog = new JDialog(this, "Visualizador de Relações Estruturais do Grafo", true);
        graphDialog.setSize(650, 480);
        graphDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblInfo = new JLabel(" MAPA FUNCIONAL DE ADJACÊNCIAS E RELAÇÕES REGISTADAS NO DÍGRAFO:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblInfo, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        var graph = gm.getMasterGraph();
        int totalEdges = 0;

        for (edu.princeton.cs.algs4.DirectedEdge e : graph.edges()) {
            String srcName = gm.getEntityKey(e.from());
            String tgtName = gm.getEntityKey(e.to());

            if (srcName != null && tgtName != null) {
                listModel.addElement(String.format(" 🔗  %s   ==================>   %s   [Custo/Peso: %.1f]", srcName, tgtName, e.weight()));
                totalEdges++;
            }
        }

        if (totalEdges == 0) {
            listModel.addElement(" ⚠️ O Grafo encontra-se vazio. Nenhuma relação relacional mapeada.");
        }

        JList<String> edgeList = new JList<>(listModel);
        edgeList.setFont(new Font("Consolas", Font.PLAIN, 12));
        panel.add(new JScrollPane(edgeList), BorderLayout.CENTER);

        JLabel lblSummary = new JLabel(" Mapeamento em Tempo Real - Total de conexões ativas detetadas: " + totalEdges);
        lblSummary.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSummary.setForeground(new Color(41, 128, 185));
        panel.add(lblSummary, BorderLayout.SOUTH);

        graphDialog.add(panel);
        graphDialog.setVisible(true);
    }
}