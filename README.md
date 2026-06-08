# Sistema de Utilizacao e Recomendacao de Streaming

## 1. Enquadramento e Definicao do Problema
Este projeto consiste no desenvolvimento de uma aplicacao Java standalone que simula o nucleo de um sistema de utilizacao e recomendacao para uma plataforma de streaming. 
O sistema modela e gere informacao relativa a utilizadores, conteudos multimedia (filmes e series) e artistas, garantindo a eficiencia no armazenamento de dados e na execucao de algoritmos de analise de redes heterogeneas e multi-relacionais.

O trabalho encontra-se estruturado em packages de classes distribuidas por camadas logicas, autonomizando a gestao da base de dados, o motor de grafos e as unidades de teste funcional.

---

## 2. Arquitetura do Sistema e Modelacao (Fase 1)
A modelacao do dominio de dados foi desenvolvida atraves de uma abordagem estrita de Programacao Orientada aos Objetos (POO):

* **Heranca e Reutilizacao:** Implementacao da classe abstrata `Entity` como superclasse de dados para encapsular os atributos transversais `ID` (identificador unico) e `Name`. As entidades `User`, `Artist` e `Content` herdam diretamente desta estrutura base.
* **Valizacao de Consistencia :** Mecanismos de integridade referencial garantem que a remocao de uma entidade da estrutura principal invoca a sua eliminacao total do sistema de grafos. Os dados eliminados sao preventivamente arquivados no ficheiro de seguranca `archivado_eliminados.txt`.

---

## 3. Estruturas de Dados e Complexidade Algoritmica
Para dar cumprimento aos requisitos de desempenho e otimizacao de memoria exigidos, a aplicacao suporta-se na biblioteca `algs4` da Universidade de Princeton:

| Componente / Requisito Funcional | Estrutura de Dados Utilizada | Complexidade (Pior Caso) | Justificacao Tecnica e Operacional |
| :--- | :--- | :--- | :--- |
| **Indexacao e Procura Geral (R2)** | `ST` (Symbol Table / HashMap) | $O(1)$ | Aplicada ao mapeamento direto de entidades onde a ordenacao nao constitui um requisito estrutural. |
| **Pesquisas Avancadas e Datas (R3)** | `RedBlackBST` (Arvore Rubro-Negra) | $O(\log N)$ | Garantia de balanceamento perfeito para pesquisas por substrings, regioes e intervalos cronologicos. |
| **Rede de Interacoes (R7)** | `EdgeWeightedDigraph` | $O(1)$ Insercao | Modela a rede heterogenea e direcionada de ligacoes (edges) entre utilizadores e conteudos. |
| **Dicionario de Nos do Grafo** | `ST<String, Integer>` | $O(\log N)$ | Traducao obrigatoria das chaves alfanumericas das entidades para indices numericos continuos. |

---

## 4. Algoritmos de Grafos e Analise de Relacoes (Fase 2)
A classe `GraphManager` operacionaliza as consultas estruturais sobre o grafo de interacoes:

* **Caminhos Mais Curtos (R8.a):** Utilizacao do algoritmo de **Dijkstra** (`DijkstraSP`) para determinar o caminho mais curto de interacoes ou ligacoes entre dois utilizadores ou entre artistas com base em subgrafos restritos.
* **Analise de Conexao (R8.c):** Aplicacao de algoritmos de pesquisa em profundidade (**DFS** - *Depth-First Search*) para verificar as propriedades de conectividade do grafo ou de subgrafos gerados por filtros de regiao ou genero.
* **Filtros Temporais e Estatisticas (R8.e, R8.f, R8.g):** Algoritmos de varrimento linear sobre o conjunto de arestas (`masterGraph.edges()`), validando as restricoes cronologicas atraves dos metadados registados em `edgeRegistry`.

---

## 5. Persistencia de Dados e Auditoria
* **Importacao e Exportacao de Ficheiros (R10):** Leitura de ficheiros de texto planos `.txt` para o povoamento inicial das tabelas de simbolos e do grafo[cite: 92]. [cite_start]Registo automatizado do historico de pesquisas efetuadas na GUI em `búsquedas_historial.txt`.
* **Serializacao Binaria (R11):** Mecanismo de exportacao e importacao do estado completo da memoria do sistema (estruturas de dados e aplicacao de grafos combinadas) para o ficheiro binario `system_state.dat`.

---

## 6. Casos de Teste (Test Cases)
* O sistema inclui unidades de teste isoladas atraves de funcoes `static` dedicadas:
* **Injecao de Dados Controlada:** O ficheiro `Main.java` estabelece programaticamente uma massa de dados heterogenea fixa para validacao imediata dos algoritmos de recomendacao e verificacao de integridade das arvores.
