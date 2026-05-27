// https://docs.digitalasset.com/operate/3.4/reference/console.html

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
}
