(ns chicken.cli.commands.post
  (:require [chicken.domain]
            [chicken.repository :as rep]
            [clojure.string :refer (split blank?)]
            [chicken.cli.app :refer (pluginCommand trim-split)])
  (:import [chicken.domain User Post]))

(def ^:private TOKEN " -> ")

(defn validate [input]
  (let
    [[name message] (trim-split input TOKEN)]
    (if (or (blank? name) (blank? message))
      {:errors '("Invalid 'post' format. Type 'Username -> message'")}
      :ok)))

(defn post [input]
  "Save post from input.
  If username from input does not exist, save username."
  (let
    [[name message] (trim-split input TOKEN)]
    (when-not (rep/existsUser? name)
      (rep/saveUser (User. name #{})))
    (rep/savePost (Post. message name (System/currentTimeMillis)))))

(pluginCommand :token TOKEN :do post :validate validate)
