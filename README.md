# insurance-policy-service

## Como iniciar a aplicação

### 1. Subindo os serviços com Docker

Execute o seguinte comando na raiz do projeto:

```bash
docker-compose up --build
```

Importante: Em outro terminal, após a aplicação iniciar, execute:
```bash
docker exec -it insurance-policy-service java -jar app.jar db migrate config.yml
```

Esse comando iniciará os seguintes serviços:

- **API Principal (`insurance-policy-service`)**  
  Responsável por receber as requisições da aplicação.

- **MySQL Database (`insurance-policy-service-database`)**  
  Banco de dados onde serão persistidas as informações.

- **API de Fraudes (`fraud-api-mock-backend`)**  
  Mock simples que retorna respostas estáticas.  
  Para alterar a classificação de risco retornada, edite o campo `classification` no `index.js`.

- **PubSub Redis (`redis-streams`)**  
  Simulação de um sistema de publicação.  
  Para visualizar as mensagens recebidas, execute em outro terminal:

```bash
docker exec -it redis-streams redis-cli
```

---

### 2. Testando a API

#### 🔐 Autenticação

Todas as requisições precisam de um token no header:

```
Authorization: Bearer valid-token-1
```

#### 📫 Requisição de exemplo (necessário substituir os IDs pelos que foram gerados)

```bash
curl --location 'http://localhost:8085/policy_requests' --header 'Authorization: Bearer valid-token-1' --header 'Content-Type: application/json' --data '{
  "productId": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
  "category": "AUTO",
  "salesChannel": "MOBILE",
  "paymentMethod": "CREDIT_CARD",
  "totalMonthlyPremiumAmount": 75.25,
  "insuredAmount": 275000.50,
  "coverages": {
    "Roubo": 100000.25,
    "Perda Total": 100000.25,
    "Colisão com Terceiros": 75000.00
  },
  "assistances": [
    "Guincho até 250km",
    "Troca de Óleo",
    "Chaveiro 24h"
  ]
}'


curl --location 'http://localhost:8085/policy_requests/38e64c3a-54fa-4ba6-96ee-1d4fb2e141f5' \
--header 'Authorization: Bearer valid-token-1'
```

#### 📥 Resposta esperada

```json
{
  "id": "3538c861-ecae-4b60-8f43-7ac2d234f7af",
  "customerId": "11111111-1111-1111-1111-111111111111",
  "productId": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
  "category": "AUTO",
  "salesChannel": "MOBILE",
  "paymentMethod": "CREDIT_CARD",
  "status": "VALIDATED",
  "createdAt": 1750116859.000000000,
  "finishedAt": null,
  "totalMonthlyPremiumAmount": 75.25,
  "insuredAmount": 275000.50,
  "coverages": {
    "Roubo": 100000.25,
    "Perda Total": 100000.25,
    "Colisão com Terceiros": 75000.00
  },
  "assistances": [
    "Chaveiro 24h",
    "Guincho até 250km",
    "Troca de Óleo"
  ],
  "history": [
    {
      "policyId": "3538c861-ecae-4b60-8f43-7ac2d234f7af",
      "status": "RECEIVED",
      "timestamp": 1750116860.000000000
    },
    {
      "policyId": "3538c861-ecae-4b60-8f43-7ac2d234f7af",
      "status": "VALIDATED",
      "timestamp": 1750116860.000000000
    }
  ]
}
```

---

### 3. Testes automatizados

Os testes são executados automaticamente durante o `docker-compose up`, apenas para fins de ilustração.

---

## ✅ Requisitos Funcionais

1. Receber solicitações de apólice de seguro via API REST, persistir e retornar ID e timestamp.
2. Processar solicitações consultando a API de Fraudes e aplicar regras conforme classificação de risco.
3. Permitir consulta de solicitações por ID ou ID do cliente.
4. Processar eventos de pagamento e autorização de subscrição.
5. Cancelar a solicitação, desde que a apólice ainda não tenha sido emitida.
6. Atualizar o estado da solicitação conforme o ciclo de vida.
7. Publicar eventos de alteração de estado para notificar outros serviços.

---

## 📌 Premissas

1. Simulação de autenticação via token (`Authorization: Bearer <token>`), identificando o `clientId` ou `paymentServiceId` a partir de uma lista mockada.
2. Camadas principais:
    - **Resource**: rotas e autenticação.
    - **Service**: regras de negócio.
    - **Validator/ClassifierService**: validação e classificação.
    - **DAO**: persistência.
    - **API**: comunicação com serviços externos.
3. Uso de **injeção de dependências** para construção das classes.
4. Publicação de eventos centralizada após mudança de estado (pub sem lógica de sub).
5. A API de fraude sempre retorna o mesmo mock — edite o `index.js` para alterar o comportamento.
6. Cobertura com testes unitários e de integração para as camadas principais.
7. Testes de DAO com banco de dados isolado para inserção e exclusão de dados.
8. Assumi que a API de fraudes pode ser chamada de forma síncrona. Mas, eventualmente, em produção seria interessante também tratá-la como um PubSub, dependendo de como é seu funcionamento, visto que a chamada a ela pode falhar, ou não ocorrer em tempo imediato
9. Deixei também uma collections do Postman que pode ser utilizada. É necessário ajustar os IDS conforme novas apólices são criadas
