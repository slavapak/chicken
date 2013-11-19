(ns chicken.cli.commands.follow-test
  (:require [clojure.test :refer :all]
            [chicken.repository :as rep]
            [chicken.cli.commands.follow :refer :all]))

(deftest testValidate
  (are [x y] (= x y)
       :ok    (validate "Alice follows Bob")
       :ok    (validate "  Alice    follows    Bob   "))
  (are [x] (:errors x)
       (validate "AlicefollowsBob")
       (validate "follows Alice Bob")
       (validate "Alice Bob follows")))

(deftest testFollow
  (def followCount (atom 0))
  (with-redefs [rep/existsUser? (fn [name] true)
                rep/follow (fn [a b] (do
                                       (swap! followCount inc)
                                       (is (= a "Alice"))
                                       (is (= b "Bob"))))]
    (follow "Alice follows Bob")
    (is (= 1 @followCount)))

  (testing "When username does not exit do nothing"
    (with-redefs [rep/existsUser? (fn [a] (not= a "Alice"))
                  rep/follow (fn [a b] (throw (AssertionError. "Must not be called")))]
      (follow "Alice follows Bob")))

  (testing "When followedUser does not exit do nothing"
    (with-redefs [rep/existsUser? (fn [a] (not= a "Bob"))
                  rep/follow (fn [a b] (throw (AssertionError. "Must not be called")))]
      (follow "Alice follows Bob")))
  )
