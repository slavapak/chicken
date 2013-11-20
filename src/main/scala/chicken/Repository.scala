package chicken

import scala.collection.mutable
import chicken.domain._

class Repository {

  val users = mutable.Map[String, User]()
  val posts = mutable.ListBuffer[Post]()

  def saveUser(user: User) {
    users.put(user.name, user)
  }

  def savePost(post: Post) {
    posts += post
  }

  def follow(name: String, followed: String) {
    users(name).followed.add(followed)
  }

  def isUserExist(username: String) =
    users.keys.exists(_ == username)

  def getTimeline(username: String) =
    posts.filter(_.author == username).
      sortWith(sortByTimeInDescOrderComparator)

  def getWall(username: String) = {
    val userAndFollowed = users(username).followed + username
    posts.filter(p => userAndFollowed.contains(p.author)).
      sortWith(sortByTimeInDescOrderComparator)
  }

  def sortByTimeInDescOrderComparator(a: Post, b: Post) =
    b.timestamp < a.timestamp
}