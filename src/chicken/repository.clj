(ns chicken.repository)

(def users (atom {}))
(def posts (atom '()))

(defn saveUser [user]
  (swap! users assoc (:name user) user))

(defn savePost [post]
  (swap! posts conj post))

(defn existsUser? [name]
  (if (some #(= % name) (keys @users))
    true
    false))

(defmacro sort-by-time-desc [seq]
  `(sort-by :timestamp #(compare %2 %1) ~seq))

(defn getTimeline [username]
  "Returns all posts only by username sorted by :timestamp in desc order"
  (->> @posts
    (filter #(= (:author %) username))
    (sort-by-time-desc)))

(defn getWall [username]
  "Returns all posts by username and users which it follows sorted by :timestamp in desc order"
  (if-let [followed (:followed (@users username))]
    (let [followed+user (merge followed username)
          wall (filter #(contains? followed+user (:author %)) @posts)]
      (sort-by-time-desc wall))
    '()))

(defn follow [username followedName]
  "Adds followedName to :followed set of username"
  (swap! users update-in [username :followed]
            merge followedName))

