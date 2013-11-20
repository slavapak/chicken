package chicken.cli

import java.text.SimpleDateFormat
import scala.collection.mutable
import chicken.cli.commands.{Wall, Follow, CliCommand}
import chicken.Repository


object App {

  //since it is a small program (:)) we won't inject any dependencies and will just share repository through App
  val rep: Repository = new Repository()

  private val commands: mutable.Set[CliCommand] = mutable.Set(chicken.cli.commands.Post(), Follow(), Wall())

  private val dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")

  def main(args: Array[String]) {
    var input = prompt()
    while (input != null && input != "exit") {
      dispatch(input)
      input = prompt()
    }
  }

  def prompt() = {
    print("> ")
    readLine()
  }

  def dispatch(input: String) {
    val command = parseCommand(input)
    val errors = command.validate(input)
    if (errors == Nil)
      command.exec(input)
    else
      errors.foreach(println)
  }

  def parseCommand(input: String) = {
    val filtered =
      commands.map(c =>  c -> input.indexOf(c.token)).
      filter(_._2 > -1)
    if (filtered.nonEmpty)
        filtered.minBy(_._2)._1
    else
      Timeline()
  }

  private case class Timeline() extends CliCommand {
    def exec(input: String) {
      println()
      val username = input.trim
      if (rep.isUserExist(username))
        rep.getTimeline(username).
          map(p => "%s (%s)".format(p.text, timeFormat(p.timestamp))).
          foreach(println)
      else
        println("Incorrect input.")
      println()
    }

    def token =
      throw new UnsupportedOperationException("Timeline is a default command. It has no token and should not be parsed")
  }


  def timeFormat(time: Long) = {
    val interval = System.currentTimeMillis() - time
    val seconds = interval / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val moreThanADayAgo = hours > 24
    if (moreThanADayAgo)
      dateFormat.format(time)
    else if (hours > 1)
      hours + " hours ago"
    else if (hours == 1)
      "a hour ago"
    else if (minutes > 1)
      minutes + " minutes ago"
    else if (minutes == 1)
      "a minute ago"
    else if (seconds > 1)
      seconds + " seconds ago"
    else
      "a second ago"
  }

  def trimSplit(input: String, delimiter: String) =
    input.split(delimiter).map(_.trim)

}

