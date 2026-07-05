type ParticipantLike = com.digitalasset.canton.console.LocalParticipantReference
type PartyLike = { def toLf: AnyRef }

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

def importDarTemplates(participant: ParticipantLike, dirPath: String): Unit = {
  val darTemplates = java.nio.file.Files.list(java.nio.file.Paths.get(dirPath))
    .filter(new java.util.function.Predicate[java.nio.file.Path] {
      def test(path: java.nio.file.Path): Boolean = path.toString.endsWith(".dar")
    }
    )
  .toArray()

  darTemplates.foreach { path =>
    val darTemplatePath = path.toString
    participant.dars.upload(darTemplatePath)
    println(s"Imported template from: $darTemplatePath to ${participant}")
  }
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

  // Criar os Parties e os usuários correspondentes para cada Party
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Alex")
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Abigail")
  createPartyAndUser(participant1.asInstanceOf[ParticipantLike], "Alice")

  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Brian")
  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Bella")
  createPartyAndUser(participant2.asInstanceOf[ParticipantLike], "Barbara")

  // Importar os templates no node
  importDarTemplates(participant1.asInstanceOf[ParticipantLike], "/canton/data/")
  importDarTemplates(participant2.asInstanceOf[ParticipantLike], "/canton/data/")
}
