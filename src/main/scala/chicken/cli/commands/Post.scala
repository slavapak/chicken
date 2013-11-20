package chicken.cli.commands

import chicken.cli.App
import chicken.domain.User

case class Post() extends CliCommand {

  def token: String = " -> "

  override def validate(input: String): List[String] = {
    val trimSplit = App.trimSplit(input, token)
    if (trimSplit.length != 2 ||
      trimSplit(0) == null || trimSplit(0).isEmpty ||
      trimSplit(1) == null || trimSplit(1).isEmpty)
      "Invalid 'post' format. Type 'Username -> message'" :: Nil
    else
      Nil
  }

  def exec(input: String): Unit = {
    val trimSplit = App.trimSplit(input, token)
    val name = trimSplit(0)
    val message = trimSplit(1)
    if (!App.rep.isUserExist(name))
      App.rep.saveUser(User(name))
    App.rep.savePost(chicken.domain.Post(message, name, System.currentTimeMillis()))
  }

}
