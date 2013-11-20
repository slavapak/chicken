package chicken.cli.commands

import chicken.cli.App

case class Follow() extends CliCommand {

  def token = " follows "

  override def validate(input: String) = {
    val trimSplit = App.trimSplit(input, token)
    if (trimSplit.length != 2 ||
      trimSplit(0) == null || trimSplit(0).isEmpty ||
      trimSplit(1) == null || trimSplit(1).isEmpty)
      "Invalid 'follows' format. Type 'Username follows Username'" :: Nil
    else
      Nil
  }

  def exec(input: String) = {
    val trimSplit = App.trimSplit(input, token)
    val name = trimSplit(0)
    val followed = trimSplit(1)
    if (App.rep.isUserExist(name) && App.rep.isUserExist(followed))
      App.rep.follow(name, followed)
  }

}
