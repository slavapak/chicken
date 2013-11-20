package chicken.cli.commands

trait CliCommand {

  def exec(input: String)

  def token: String

  def validate(input: String): List[String] = Nil

}
