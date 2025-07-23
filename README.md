# Desafio de Microserviços - NTT DATA

Este projeto é uma implementação de uma arquitetura de microsserviços baseada em Java com Spring Boot e Spring Cloud, desenvolvida como parte do desafio técnico da NTT DATA. O sistema simula um catálogo de produtos e um serviço de criação de pedidos, com comunicação, descoberta de serviços e um ponto de entrada único via API Gateway.

## Arquitetura

O sistema é composto por quatro serviços independentes que trabalham em conjunto:

1.  **Service Discovery (Eureka Server):** Atua como um catálogo de serviços. Todos os outros microsserviços se registram nele, permitindo que se encontrem dinamicamente na rede sem a necessidade de hardcodar IPs e portas.
2.  **Product Service:** Responsável por gerenciar o catálogo de produtos (criar, listar, buscar). Possui seu próprio banco de dados em memória (H2).
3.  **Order Service:** Responsável por gerenciar os pedidos. Ele se comunica com o `Product Service` para obter informações dos produtos ao criar um novo pedido.
4.  **API Gateway:** É o ponto de entrada único para todas as requisições externas. Ele é responsável por rotear as requisições para o serviço correto (`Product` ou `Order`) e por garantir a segurança, validando um token de autenticação.

```mermaid
graph TD
    subgraph "Cliente Externo"
        Client[Cliente API]
    end

    subgraph "Infraestrutura de Microsserviços"
        Client --&gt; Gateway[API Gateway <br> Porta: 8300 <br> (Segurança e Roteamento)]
        
        Gateway --&gt; |/products/**| ProductSvc[Product Service <br> Porta: 8100]
        Gateway --&gt; |/orders/**| OrderSvc[Order Service <br> Porta: 8200]
        
        ProductSvc --&gt; H2[(H2 Database)]
        OrderSvc --&gt; |Busca produtos| ProductSvc
        
        subgraph "Service Discovery"
            Eureka[Eureka Server <br> Porta: 8761]
        end
        
        ProductSvc --&gt; |Registra-se| Eureka
        OrderSvc --&gt; |Registra-se| Eureka
        Gateway --&gt; |Registra-se| Eureka
    end
````

-----

## Pré-requisitos

Para compilar e rodar esta aplicação, você precisará de:

  * **Java JDK 17** ou superior.
  * **Apache Maven 3.8** ou superior.
  * **Git** para clonar o repositório.
  * Um cliente de API como **Postman**, **Insomnia** ou **cURL** para testar os endpoints.

-----

## Como Instalar e Rodar

Siga os passos abaixo para colocar toda a arquitetura no ar.

### 1\. Clone os Repositórios

Primeiro, clone os 4 projetos para sua máquina local.

### 2\. Empacote cada Projeto

Você precisará gerar o arquivo `.jar` executável para cada serviço. Abra um terminal e execute o seguinte comando na pasta raiz de **cada um dos 4 projetos**:

```bash
mvn clean package
```

### 3\. Execute os Serviços

É crucial iniciar os serviços na ordem correta. Você precisará de **4 terminais abertos**, um para cada serviço.

**Terminal 1: Service Discovery (Eureka)**

```bash
# Navegue até a pasta do projeto discovery-service
java -jar target/discovery-service-0.0.1-SNAPSHOT.jar
```

*Aguarde ele iniciar. Você pode acessar o painel em `http://localhost:8761`.*

**Terminal 2: Product Service**

```bash
# Navegue até a pasta do projeto product-service
java -jar target/product-service-0.0.1-SNAPSHOT.jar
```

**Terminal 3: Order Service**

```bash
# Navegue até a pasta do projeto order-service
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

**Terminal 4: API Gateway**

```bash
# Navegue até a pasta do projeto api-gateway
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

Após alguns segundos, todos os serviços (`PRODUCT-SERVICE`, `ORDER-SERVICE`, `API-GATEWAY`) devem aparecer como `UP` no painel do Eureka.

-----

## Guia da API (Endpoints e Payloads)

Todas as requisições devem ser feitas para o **API Gateway** na porta `8300`.

### Autenticação

Todas as rotas são protegidas. É necessário enviar um token de autenticação no cabeçalho de cada requisição.

  * **Header:** `Authorization`
  * **Valor:** `Bearer umtokenlegal`

### 1\. Endpoints de Produtos (`/products`)

#### Criar um Novo Produto

  * `POST /products`
  * **Payload (Corpo):**
    ```json
    {
        "name": "Teclado Mecânico Sem Fio",
        "description": "Teclado compacto 75% com switches brown.",
        "price": 450.00
    }
    ```
  * **Resposta de Sucesso:** `201 Created` com o header `Location` apontando para o novo recurso.

#### Listar Todos os Produtos

  * `GET /products`
  * **Resposta de Sucesso (`200 OK`):**
    ```json
    [
        {
            "id": 1,
            "name": "Teclado Mecânico Sem Fio",
            "description": "Teclado compacto 75% com switches brown.",
            "price": 450.00
        }
    ]
    ```

#### Buscar um Produto por ID

  * `GET /products/{id}`
  * **Resposta de Sucesso (`200 OK`):**
    ```json
    {
        "id": 1,
        "name": "Teclado Mecânico Sem Fio",
        "description": "Teclado compacto 75% com switches brown.",
        "price": 450.00
    }
    ```

### 2\. Endpoints de Pedidos (`/orders`)

#### Criar um Novo Pedido

  * `POST /orders`
  * **Payload (Corpo):** Uma lista de itens, cada um com `productId` e `quantity`.
    ```json
    [
        {
            "productId": 1,
            "quantity": 2
        }
    ]
    ```
  * **Resposta de Sucesso:** `201 Created` com o header `Location` apontando para o novo pedido.
  * **Resposta de Erro (`400 Bad Request`):** Se a lista de itens estiver vazia ou algum item tiver quantidade menor ou igual a zero.

#### Listar Todos os Pedidos

  * `GET /orders`
  * **Resposta de Sucesso (`200 OK`):**
    ```json
    [
        {
            "orderId": 1,
            "items": [
                {
                    "product": {
                        "id": 1,
                        "name": "Teclado Mecânico Sem Fio",
                        "description": "Teclado compacto 75% com switches brown.",
                        "price": 450.00
                    },
                    "quantity": 2
                }
            ],
            "totalPrice": 900.00,
            "orderDate": "2025-07-23T15:45:10.123456"
        }
    ]
    ```

### Buscar um Pedido por ID
  * `GET /orders`
  * **Resposta de Sucesso (`200 OK`):**

    ```json
    {
        "orderId": 1,
        "items": [
            {
                "product": {
                    "id": 1,
                    "name": "Teclado Mecânico Sem Fio",
                    "description": "Teclado compacto 75% com switches brown.",
                    "price": 450.00
                },
                "quantity": 2
            }
        ],
        "totalPrice": 900.00,
        "orderDate": "2025
