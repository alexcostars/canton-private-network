Preparando o ambiente:
```
docker network create canton-network-internal
```

Subindo a rede local:
```
docker run --rm -it --name canton-network -p 5001:5001 -p 5002:5002 -p 5011:5011 -p 5012:5012 -p 5013:5013 -p 5014:5014 -v ./config:/canton/config -v ./data:/canton/data --network canton-network-internal digitalasset/canton-open-source:2.3.20 -c /canton/config/remote.conf --bootstrap /canton/config/bootstrap.canton
```

Subindo a interface gráfica:
```
docker run -it --rm --name canton-explorer -p 7575:4000 --network canton-network-internal digitalasset/daml-sdk:2.9.7 daml navigator server canton-network 5002
```

Agora dentro do terminal:
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

# Fazendo deploy de um contrato

Subindo o SDK:
```
docker run -it --rm --name canton-sdk --entrypoint /bin/bash -v ./project:/home/project --network canton-network-internal digitalasset/daml-sdk:2.3.20
```


Agora vamos compilar o projeto:
```
cd /home/project/coin
daml build
daml test
-- daml ledger upload-dar --host canton-network --port 5002 --timeout 60 .daml/dist/coin-1.0.0.dar
```

Agora que compilamos o contrato, vamos publicá-lo via console do Canton (é preciso publicar para todos os participants que vão interagir com esse contrato):
```
participant1.dars.upload("/canton/data/coin-1.0.0.dar")
participant2.dars.upload("/canton/data/coin-1.0.0.dar")
```