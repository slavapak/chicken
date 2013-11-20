(ns chicken.cli.app-test
  (:require [clojure.test :refer :all]
            [chicken.domain]
            [chicken.cli.app :refer :all]
            [chicken.repository :as rep])
  (:import (chicken.domain User Post)
           (java.util Date)))

(deftest testParseCommand
  (reset! commands #{{:token " token " :description "command"}
                     {:token " anotherToken " :description "another command"}})
  (testing "Returns copy of command which token is met in input with added index of that token in input"
    (is (= {:token " token " :description "command" :index 9}
          (parseCommand "Line with token in it"))))

  (testing "When several command tokens are met in input returns command for the leftmost token"
    (is (= {:token " token " :description "command" :index 9}
          (parseCommand "Line with token and anotherToken"))))

  (testing "When no command tokens are met in string return default command in which under :do key default command
  function is stored"
    (is (contains? (parseCommand "Line without command tokens") :do))))

(deftest testOptionalValidate
  (testing "when command does not have :validate key return ok"
    (is (= :ok
          (optionalValidate {:token " > " :description "command without validator"} "input"))))
  (testing "when command has :validate key return the result of invocation of the function stored under that key against
  input"
    (is (= "input is input"
          (optionalValidate {:description "command with validator" :validate #(str "input is " %)} "input")))))

(deftest testReadTimeline
  (testing "Consider whole input as username"
    (with-redefs [rep/existsUser? (fn [input] (do
                                                (is (= "Whole input" input))
                                                false))]
      (with-out-str (readTimeline "Whole input"))))

  (testing "When username does not exist print 'Incorrect input'"
    (with-redefs [rep/existsUser? (fn [_] false)]
      (is (= "\nIncorrect input.\n\n"
            (with-out-str (readTimeline "Username that does not exist"))))))

  (testing "When username from input exists return result of rep/getTimeline for it"
    (with-redefs [rep/existsUser? (fn [_] true)
                  rep/getTimeline (fn [_] (list (Post. "second" "Alice" 2)
                                            (Post. "first" "Alice" 1)))
                  timeFormat (fn [_] "mock time")]
      (is (= "\nsecond (mock time)\nfirst (mock time)\n\n"
            (with-out-str (readTimeline "input"))))))

  (testing "Printed input is prepended and appended with line-feed,
           and each line of it has format 'Message text (formatted time)'"
    (with-redefs [rep/existsUser? (fn [_] true)
                  rep/getTimeline (fn [_] (list (Post. "first" "Alice" 1)))
                  timeFormat (fn [_] "mock time")]
      (is (= "\nfirst (mock time)\n\n"
            (with-out-str (readTimeline "input")))))))
