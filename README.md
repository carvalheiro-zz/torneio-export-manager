# Torneio Export Manager

Serviço de alta performance para geração de planilhas de torneios (Beach Tennis/Super 8).

### Tech Stack
- Java 21 (LTS)
- Spring Boot 3.x / Quarkus
- Apache POI 5.2.x

### Funcionalidades
- **Ranking Dinâmico**: Fórmulas `SUMPRODUCT` e `COUNTIF` para saldo de games.
- **Pódio Inteligente**: Formatação condicional com lógica de *Dense Rank* (Ouro, Prata, Bronze).
- **UX**: Congelamento de painéis e proteção de integridade de fórmulas.