(ns chicken.cli.commands.post-test
  (:require [clojure.test :refer :all]
            [chicken.repository :as rep]
            [chicken.cli.commands.post :refer :all]))


(deftest testValidate
  (are [x y] (= x y)
      :ok (validate "Alice -> I like Rabbit") 
      :ok (validate "   Alice     ->     I like Rabbit   "))
  (are [x] (:errors x)
      (validate "Alice->Bob")       
      (validate "-> Alice Bob")       
      (validate "Alice Bob ->")))

(deftest testPost
  (testing "When user exists save it's message"
    (def savePostCount (atom 0))
    (with-redefs [rep/existsUser? (fn [a] true)
                  rep/saveUser (fn [a] ((throw (AssertionError. "Must not be called"))))
                  rep/savePost (fn [post] 
                                 (do
                                            (swap! savePostCount inc)
                                            (is (= "Alice" (:author post)))
                                            (is (= "I like Rabbit" (:text post)))))]
      (post "Alice -> I like Rabbit"))
    (is (= 1 @savePostCount)))

  (testing "When user does not exist save user and it's message"
    (def saveUserCount (atom 0))
    (def savePostCount (atom 0))
    (with-redefs [rep/existsUser? (fn [_] false)
                  rep/saveUser (fn [user] (do
                                            (swap! saveUserCount inc)
                                            (is (= "Alice" (:name user)))
                                            ))
                  rep/savePost (fn [post] (do
                                            (swap! savePostCount inc)
                                            (is (= "Alice" (:author post)))
                                            (is (= "I like Rabbit" (:text post)))))]
      (post "Alice -> I like Rabbit"))
    (is (= 1 @saveUserCount))
    (is (= 1 @savePostCount))))
