# Setup

Para todos os containers será utilizada a rede:
```
docker network create canton-network
```

## Iniciando uma rede local

Para subir uma rede local com dois participantes (`participant1` e `participant2`), execute:
```
docker run -it --rm --name canton-dev --network canton-network -p 5003:5003 -p 5013:5013 -v "$(pwd)/config/developer/components.conf:/app/additional-config.conf" -v "$(pwd)/config/developer/bootstrap-full.scala:/app/bootstrap.sc" -v "$(pwd)/config/developer/shared-volume:/canton/data" -e LOG_LEVEL_STDOUT=INFO europe-docker.pkg.dev/da-images/public/docker/canton-base:3.4.11
```

Caso prefira iniciar a rede sem paties pré-carregados, utilize o arquivo `bootstrap-basic.scala` em vez do `bootstrap-full.scala`.

## Iniciando uma rede produtiva

Para subir uma rede produtiva que possua containers separados para cada nó (`participant1` e `participant2`) e um container para o `synchronizer` (`sequencers` + `mediators`), execute:
```
cd config/production
docker compose up
```

# Fazendo deploy de um contrato

Pode-se instalar o SDK do DAML na máquina local ou utilizar um container para acessar a ferramenta de desenvolvimento sem a necessidade de instalação local:
```
docker run -it --rm --name daml-sdk -v "$(pwd)/project:/home/project" ubuntu:latest
apt update && apt install -y curl openjdk-17-jdk
curl https://get.daml.com | sh
export PATH=$PATH:/root/.daml/bin
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