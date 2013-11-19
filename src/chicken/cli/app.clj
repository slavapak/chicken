(ns chicken.cli.app
  (:import (java.util Date)
           (java.text SimpleDateFormat)
           (java.io File))
  (:require [chicken.repository :as rep]
            [clojure.string :refer (split)]
            [clojure.tools.namespace.find])
  (:gen-class))

(declare prompt dispatch parseCommand timeFormat readTimeline registerCommands optionalValidate)

(def commands (atom #{}))

(defn -main []
  (registerCommands)
  (loop [input (prompt)]
    (when (and input (not= input "exit"))
      (dispatch input)
      (recur (prompt)))))

;This was not a part of task but this was so interesting that I implemented loading commands (read, follow, post) from classpath.
;This gives you the advantage is that to create new command (like, ignore, etc) you only need to implement it in single file
;and do not need to change app.clj file
(defn registerCommands []
  "Load commands from chicken.cli.commands"
  (->> (re-pattern (System/getProperty "path.separator"))
    (split (System/getProperty "java.class.path"))
    (filter #(.endsWith % ".jar"))
    (map #(File. %))
    (clojure.tools.namespace.find/find-namespaces)
    (filter #(.startsWith (str %) "chicken.cli.commands"))
    (map require)
    (doall)))

(defn- prompt []
    (.print System/out "> ")
    (read-line))

(defn- dispatch [input]
  "Parse input and if it contains command token execute that command.
  If that command also provides validator function, validate input beforehand"
  (let [command (parseCommand input)
        validation (optionalValidate command input)]
    (if (= validation :ok)
      ((:do command) input) 
      (doseq [e (:errors validation)]
        (println e)))))


(defn parseCommand [input]
  "Commands are stored as set of maps.
  Each command is a map with keys 
  :token - which is a token
  :do - a link to command implementing function
  :validate - a link to validator function, which is optional"
  ;here we parse input and select the command which appears first (like post command in "Lewis -> Alice follows Rabbit" instead of follow)
  ;if no explicit token found (like "Alice"), return command which will run readTimeline function
  (->> @commands
    (map #(assoc % :index (.indexOf input (:token %))))
    (filter #((complement neg?) (:index %)))
    (#(if-not (empty? %)
        (apply min-key :index %)
        {:do readTimeline}))))

(defn optionalValidate [command input]
  "If command (which is interally a Cljoure map) contains key :validate
  return result of invocation of function that is stored under that key against input.
  If there is no :validate key in command return :ok"
 (if (:validate command)
   ((:validate command) input)
   :ok))

(defn readTimeline [input]
  "Considers whole trimmed input as a username.
  If username exists, prints it's timeline sorted in desc order"
  (let [username (.trim input)]    
    (println)
    (if (rep/existsUser? username)
      (doseq [post (rep/getTimeline username)]
          (printf "%s (%s)\n" (:text post) (timeFormat (:timestamp post))))
      (println "Incorrect input."))
    (println)))

(def ^:private dateFormat (SimpleDateFormat. "EEE, d MMM yyyy HH:mm:ss"))

(defn timeFormat [time]
  (let [interval (- (System/currentTimeMillis) time)
        seconds (int (/ interval 1000))
        minutes (int (/ seconds 60))
        hours (int (/ minutes 60))
        moreThanADayAgo (> hours 24)]
    (cond
      moreThanADayAgo (.format dateFormat (Date. time))
      (> hours 1) (str hours " hours ago")
      (= hours 1) (str "a hour ago")
      (> minutes 1) (str minutes " minutes ago")
      (= minutes 1) (str "a minute ago")
      (> seconds 1) (str seconds " seconds ago")
      :else "a second ago")))

(defn pluginCommand [& {:as command}]
  (swap! commands conj command))

(defn trim-split [input delimiter]
  (map #(.trim %) 
       (split input (re-pattern delimiter))))
