package streaming.models;

import java.io.Serializable;

/**
 * Classe abstrata base para todas as entidades do sistema de streaming.
 * Garante a presença de um identificador único obrigatório.
 */
public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    /**
     * Construtor base da entidade.
     * @param id O identificador único da entidade.
     */
    public Entity(int id) {
        this.id = id;
    }

    /**
     * Obtém o identificador único da entidade.
     * @return O id numérico.
     */
    public int getId() { return id; }

    /**
     * Define o identificador único da entidade.
     * @param id O novo id numérico.
     */
    public void setId(int id) { this.id = id; }
}