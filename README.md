# Ambiente local

## Iniciando uma rede local

Preparando o ambiente:
```
docker network create canton-network-internal
```

Para subir uma rede local simulando dois nós (`participant1` e `participant2`), execute:
```
docker run --rm -it --name canton-network -p 5001:5001 -p 5002:5002 -p 5011:5011 -p 5012:5012 -p 5013:5013 -p 5014:5014 -v ./config:/canton/config -v ./data:/canton/data --network canton-network-internal digitalasset/canton-open-source:2.7.9 -c /canton/config/remote.conf --bootstrap /canton/config/bootstrap-full.canton
```

Após a inicialização da rede, crie participantes em cada um dos nós através do console iniciado pelo container `canton-network`:
```
// Criar a Party "Banco" no Participante 1
val banco = participant1.parties.enable("Banco")

// Criar a Party "Cliente" no Participante 2
val cliente = participant2.parties.enable("Cliente")

// Criar usuário 'admin_banco' que pode ler e escrever como 'Banco'
participant1.ledger_api.users.create(
  id = "admin_banco",
  actAs = Set(banco.toLf),
  primaryParty = Some(banco.toLf)
)

// Criar usuário 'user_cliente' para o segundo participante
participant2.ledger_api.users.create(
  id = "user_cliente",
  actAs = Set(cliente.toLf),
  primaryParty = Some(cliente.toLf)
)
```

Opcional: E agora adicione o primeiro template customizado (smart contract) na rede:
```
participant1.dars.upload("/canton/data/coin-1.0.0.dar")
participant2.dars.upload("/canton/data/coin-1.0.0.dar")
```

## Explorer UI

A Canton disponibiliza uma interface gráfica para interagir com o nó. Execute o container abaixo para inicializar essa interface gráfica:
```
docker run -it --rm --name canton-explorer -p 7575:4000 --network canton-network-internal digitalasset/daml-sdk:2.9.7 daml navigator server canton-network 5002
```
Modifique o último parâmetro (`5002`) para corresponder à porta definida em `ledger-api.port` do respectivo nó alvo, definido no arquivo [`remote.conf`](./config/remote.conf)

# Fazendo deploy de um contrato

Pode-se instalar o SDK do DAML na máquina local ou utilizar um container para acessar a ferramenta de desenvolvimento sem a necessidade de instalação local:
```
docker run -it --rm --name canton-sdk --entrypoint /bin/bash -v ./project:/home/project --network canton-network-internal digitalasset/daml-sdk:2.7.9
```


Agora vamos compilar o projeto:
```
cd /home/project/coin
daml test
daml build
```

Após compilar o contrato, copie o pacote `/project/coin/.daml/dist/coin-1.0.0.dar` para o diretório `/data` e realize o upload no nó desejado através da console do container `canton-network`:
```
participant1.dars.upload("/canton/data/coin-1.0.0.dar")
participant2.dars.upload("/canton/data/coin-1.0.0.dar")
```