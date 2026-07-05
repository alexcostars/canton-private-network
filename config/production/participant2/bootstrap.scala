// https://docs.digitalasset.com/operate/3.4/reference/console.html

def main(): Unit = {
  participant2.synchronizers.connect("mySynchronizer", "http://canton-synchronizer:5018")
}
