# 📩 API de Comunicação

API REST desenvolvida com **Java e Spring Boot** para gerenciamento de comunicações, permitindo registrar mensagens, consultar status de envio, cancelar comunicações e consultar o histórico de mensagens.

O projeto também possui integração com uma API externa de notificações para realizar o envio de e-mails.

## 🚀 Funcionalidades

- **Agendamento de comunicação:** registra uma comunicação com os dados do destinatário, mensagem, data e horário.
- **Envio de e-mail:** integração com a API de Notificação utilizando OpenFeign.
- **Consulta de status:** permite consultar o status de uma comunicação pelo e-mail do destinatário.
- **Cancelamento:** permite alterar o status de uma comunicação para CANCELADO.
- **Histórico de comunicações:** consulta todas as comunicações registradas ou filtra pelo e-mail do destinatário.
- **Tratamento de exceções:** tratamento centralizado de erros da aplicação.
- **Mapeamento de objetos:** conversão entre DTOs e entidades utilizando MapStruct.
- **Testes automatizados:** testes unitários e de controller utilizando JUnit e Mockito.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 11 | Linguagem de programação |
| Spring Boot 2.7 | Framework principal |
| Spring Web | Construção da API REST |
| Spring Data JPA | Persistência de dados |
| MySQL 8 | Banco de dados |
| Spring Cloud OpenFeign | Comunicação entre APIs |
| MapStruct | Mapeamento de objetos |
| Lombok | Redução de código boilerplate |
| Maven | Gerenciamento de dependências |
| JUnit 5 | Testes automatizados |
| Mockito | Simulação de dependências |
| Docker | Containerização |
| Swagger / OpenAPI | Documentação da API |

## 📡 Endpoints

A API utiliza o prefixo `/comunicacao`.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/comunicacao/agendar` | Registra uma comunicação |
| GET | `/comunicacao?emailDestinatario=` | Consulta o status |
| GET | `/comunicacao/historico` | Consulta o histórico |
| PATCH | `/comunicacao/cancelar?emailDestinatario=` | Cancela uma comunicação |

### 1. Registrar comunicação

**POST** `/comunicacao/agendar`

Exemplo de requisição:

```json
{
  "dataHoraEnvio": "23-11-2026 17:30:00",
  "nomeTarefa": "Lembrete de compromisso",
  "nomeDestinatario": "Bruno",
  "emailDestinatario": "bruno@email.com",
  "telefoneDestinatario": "48999999999",
  "mensagem": "Não esqueça do seu compromisso!",
  "modoDeEnvio": "EMAIL"
}
```

Resposta esperada:

```json
{
  "dataHoraEnvio": "23-11-2026 17:30:00",
  "nomeTarefa": "Lembrete de compromisso",
  "nomeDestinatario": "Bruno",
  "emailDestinatario": "bruno@email.com",
  "telefoneDestinatario": "48999999999",
  "mensagem": "Não esqueça do seu compromisso!",
  "modoDeEnvio": "EMAIL",
  "statusEnvio": "ENVIADO"
}
```
### 2. Consultar status

**GET** `/comunicacao?emailDestinatario=bruno@email.com`

Retorna os dados da comunicação e seu status atual.

### 3. Consultar histórico

**GET** `/comunicacao/historico`

Retorna todas as comunicações cadastradas, ordenadas pela data de envio.

Também é possível filtrar pelo e-mail:

**GET** `/comunicacao/historico?emailDestinatario=bruno@email.com`

### 4. Cancelar comunicação

**PATCH** `/comunicacao/cancelar?emailDestinatario=bruno@email.com`

Altera o status da comunicação para `CANCELADO`.

## 📋 Validações e regras

Os dados utilizados no cadastro incluem:

- Data e hora da comunicação.
- Nome do destinatário.
- E-mail do destinatário.
- Mensagem.
- Modo de envio.

Os modos de envio definidos no projeto são:

```java
EMAIL,
SMS,
PUSH,
WHATSAPP
```

Atualmente, a integração efetivamente implementada é para envio de e-mails.

## 🗄️ Banco de dados

O projeto utiliza MySQL 8 para persistência das comunicações.

Configuração utilizada:

- Banco: `comunicacao1`
- Porta: `3306`
- Usuário padrão: `root`

As tabelas são gerenciadas pelo Hibernate através da configuração JPA.

## 🧪 Testes

O projeto utiliza JUnit 5 e Mockito para verificar o comportamento dos controllers e serviços.

Para executar os testes:

```bash
./mvnw test
```

Os testes contemplam cenários como:

- Cadastro de comunicação.
- Consulta de status.
- Consulta de histórico.
- Cancelamento de comunicação.
- Tratamento de exceções.
- Integração simulada com a API de Notificação.

## 📖 Documentação Swagger

A documentação interativa da API está disponível através do Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

A especificação OpenAPI pode ser acessada em:

```text
http://localhost:8080/v3/api-docs
```


## 👨‍💻 Autor

Desenvolvido como projeto de estudo para aplicação de conceitos de desenvolvimento Back-end com Java, Spring Boot, APIs REST, integração entre microsserviços, persistência de dados e testes automatizados.
