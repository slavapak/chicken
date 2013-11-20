(ns chicken.cli.commands.wall
  (:require [chicken.repository :as rep]
            [chicken.cli.app :refer (pluginCommand timeFormat)]))

(def ^:private TOKEN " wall")

(defn validate [input]
  (if (not (.endsWith (.trim input) TOKEN))
    {:errors '("Invalid 'wall' format. Type 'Username wall'")}
    :ok))

(defn wall [input]
  "Print wall for username from input if it exists"
  (let
    [name (.trim (.substring input 0 (.indexOf input TOKEN)))
     wall (rep/getWall name)]
    (println)
    (doseq [post wall]
      (printf "%s - %s (%s)\n" (:author post) (:text post) (timeFormat (:timestamp post))))
    (println)))

(pluginCommand :token TOKEN :do wall :validate validate)
