type ParticipantLike = com.digitalasset.canton.console.LocalParticipantReference
type PartyLike = { def toLf: AnyRef }

// import com.daml.ledger.api.v1.commands._
// import com.daml.lf.value.Value
// import com.daml.lf.data.TemplateId


// teste-erro-proposital

def createPartyAndUser(participant: ParticipantLike, partyName: String): Unit = {
  val party = participant.parties.enable(partyName)
  utils.retry_until_true {
    participant.ledger_api.parties.list().exists(_.party == party)
  }
  participant.ledger_api.users.create(
    id = s"admin_${partyName.toLowerCase}",
    actAs = Set(party.toLf),
    primaryParty = Some(party.toLf)
  )
}

def main(): Unit = {

  bootstrap.synchronizer(
    synchronizerName = "mySynchronizer",
    sequencers = Seq(sequencer1),
    mediators = Seq(mediator1),
    synchronizerOwners = Seq(sequencer1),
    synchronizerThreshold = 1,
    staticSynchronizerParameters = StaticSynchronizerParameters.defaultsWithoutKMS(ProtocolVersion.latest)
  )

  participant1.synchronizers.connect_local(sequencer1, "mySynchronizer")
  participant2.synchronizers.connect_local(sequencer1, "mySynchronizer")

  // Inicializar a identidade do Sequencer como um nó de domínio
  // val localDomainId = sequencer1.setup.node_id()

  // Conectar participantes ao domínio
  // participant1.domains.connect("sequencerLocal", "http://localhost:5018")
  // participant2.domains.connect("sequencerLocal", "http://localhost:5018")
  // participant1.domains.connect_local(sequencer1)
  // participant2.domains.connect_local(sequencer1)

  // /app/user-bootstrap.sc

  // Registrar o Mediador no domínio (necessário para transações)
  // sequencer1.topology.mediators.add(mediator1.id)


  // Listar identidades para confirmar sucesso
  // nodes.local.foreach(n => println(s"${n.name}: ${n.health.status}"))

  // sys.exit(1)

  // Criar os Parties e os usuários correspondentes para cada Party
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Alex")
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Abigail")
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Alice")

  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Brian")
  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Bella")
  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Barbara")

  // // Upload DAR to participants
  val coinPackageId = participant1.dars.upload("/canton/data/coin-1.0.0.dar")
  // participant1.dars.upload("/canton/data/coin-1.0.0.dar")
  println(s"PACKAGE ID: ${coinPackageId}")

  participant1.dars.list().foreach(n => println(n))


  // // Get party references
  // val aliceParty = participant1.parties.enable("Alice")

  // // Create a Coin instance
  // participant1.ledger_api.commands.submit(
  //   actAs = Seq(aliceParty),
  //   commands = Seq(
  //     Create(
  //       templateId = TemplateId(coinPackageId, "Main", "Coin"),
  //       arguments = Map(
  //         "issuer" -> aliceParty.toLf,
  //         "owner" -> aliceParty.toLf,
  //         "name" -> Value.ValueText("Test Coin"),
  //         "value" -> Value.ValueNumeric(BigDecimal(100.0))
  //       )
  //     )
  //   ),
  //   workflowId = "",
  //   commandId = "create-coin",
  //   applicationId = "bootstrap"
  // )
}