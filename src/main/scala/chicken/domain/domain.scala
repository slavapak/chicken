package chicken.domain

import scala.collection.mutable

case class User(name: String, followed: mutable.Set[String] = mutable.Set.empty[String])
case class Post(text: String, author: String, timestamp: Long)
