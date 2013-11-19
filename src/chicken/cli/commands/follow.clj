(ns chicken.cli.commands.follow
  (:require [chicken.repository :as rep]
            [clojure.string :refer (split blank?)]
            [chicken.cli.app :refer (pluginCommand trim-split)]))

(def ^:private TOKEN " follows ")

(defn validate [input]
  (let
    [[name followed] (trim-split input TOKEN)]
    (if (or (blank? name) (blank? followed))
      {:errors '("Invalid 'follows' format. Type 'Username follows Username'")}
      :ok)))

(defn follow [input]
  "If username and followed username from input both exist add followed username to the followed set of username"
  (let
    [[name followed] (trim-split input TOKEN)]
    (when (and (rep/existsUser? name) (rep/existsUser? followed))
      (rep/follow name followed))))

(pluginCommand :token TOKEN :do follow :validate validate)
