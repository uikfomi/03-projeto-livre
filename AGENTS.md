# Instruções locais

Siga também as instruções em `/home/keyrrison/.codex/RTK.md` quando disponíveis.

# Regras do projeto

- Siga estritamente a arquitetura MVC + DAO: controllers coordenam a interface; regras de negócio ficam nos services; acesso a dados fica nos DAOs; models representam o domínio.
- Use JDBC puro para persistência, com MySQL e HikariCP para conexões. Não utilize ORM nem frameworks de persistência.
- Toda tela sem dados deve apresentar um Empty State adequado. Nunca exiba dados falsos ou mockados nas telas.
- Antes de cada commit, execute uma compilação limpa (`mvn clean test-compile`) e corrija eventuais erros.
- Mantenha credenciais locais e segredos fora do controle de versão. As senhas de seed do schema são apenas para desenvolvimento, não para produção.
- Trabalhe incrementalmente, respeitando o escopo da etapa solicitada.
