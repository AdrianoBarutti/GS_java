Como Executar no Eclipse

    Clone o projeto:
    git clone https://github.com/AdrianoBarutti/GS_java.git

    Importe no Eclipse:
    Vá em File > Import > Maven > Existing Maven Projects
    Selecione a pasta clonada e importe

    Garanta que o Java 17 esteja configurado no Eclipse.

    Configure o banco de dados no arquivo:
    src/main/resources/application.properties
    Ajuste o spring.datasource.url, username e password conforme seu ambiente.

    Execute o projeto:
    Clique com o botão direito no projeto > Run As > Spring Boot App

    Acesse a documentação da API via Swagger:
    http://localhost:8080/swagger-ui.html

🔐 Autenticação

Para utilizar os endpoints protegidos, é necessário:

    Criar um registro de usuário acessando o endpoint /auth/register

    O sistema irá gerar uma chave de acesso (token JWT) automaticamente

Use esse token nos headers das requisições autenticadas:

Authorization: Bearer <SEU_TOKEN>

Depois apenas sincronizar as APIS e pronto!!!
