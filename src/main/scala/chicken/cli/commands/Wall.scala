package chicken.cli.commands

import chicken.cli.App

case class Wall() extends CliCommand {

  def token = " wall"

  override def validate(input: String) = {
    if (!input.trim.endsWith(token))
      "Invalid 'wall' format. Type 'Username wall'" :: Nil
    else
      Nil
  }

  def exec(input: String) = {
    val name = input.substring(0, input.indexOf(token))
    println()
    App.rep.getWall(name).
      map(p => "%s - %s (%s)".format(p.author, p.text, App.timeFormat(p.timestamp))).
      foreach(println)
    println()
  }

}
