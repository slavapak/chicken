(ns chicken.repository-test
  (:require [clojure.test :refer :all]
            [chicken.domain]
            [chicken.repository :refer :all])
  (:import (chicken.domain User Post)
           (java.util Date)))

(deftest testSaveUser
  (reset! users {})
  (let [alice (User. "Alice" #{})
        bob (User. "Bob" #{})]
    (saveUser alice)
    (is (= {"Alice" alice} @users))
    (saveUser bob)
    (is (= {"Alice" alice "Bob" bob} @users))))

(deftest testSavePost
  (reset! posts '())
  (let [post1 (Post. "first post" "Alice" (Date.))
        post2 (Post. "second post" "Bob" (Date.))]
    (savePost post1)
    (is (= (list post1) @posts))
    (savePost post2)
    (is (= (list post2 post1) @posts))))

(deftest testExistsUser?
  (reset! users {"Alice" (User. "Alice" #{})})
  (is (existsUser? "Alice"))
  (not (existsUser? "Bob")))

(deftest test-sort-by-time-desc
  (is 
    (= 
      (sort-by-time-desc '({:timestamp 1} {:timestamp 2})) 
      '({:timestamp 2} {:timestamp 1}))))

(deftest testGetTimeline
  (reset! posts (list (Post. "first" "Alice" 1)
                      (Post. "second" "Bob" 2)
                      (Post. "third" "Alice" 3)))
  (let [timeline (getTimeline "Alice")]
    (testing "returns post by username only"
      (is (= timeline
             (list (Post. "third" "Alice" 3) (Post. "first" "Alice" 1)))))
    (testing "returns all posts by username"
      (is (= 2 (count timeline))))
    (testing "returns them sorted by time in desc order"
      (is (> (:timestamp (first timeline)) (:timestamp (second timeline)))))))

(deftest testGetWall
  (reset! users {"Alice" (User. "Alice" #{"Bob"})
                          "Bob" (User. "Bob" #{})
                          "Charlie " (User. "Alice" #{})})
  (reset! posts (list (Post. "first" "Alice" 1)
                           (Post. "second" "Bob" 2)
                           (Post. "not in result set" "Charlie" 5)
                           (Post. "third" "Alice" 3)
                           (Post. "fourth" "Bob" 4)))
  (let [wall (getWall "Alice")]
    (testing "returns posts only by username and users it follows"
      (is (= #{"Alice" "Bob"} (reduce #(merge %1 (:author %2)) #{} wall))))
    (testing "returns all posts by username and users it follows"
      (is (= 4 (count wall))))
    (testing "return posts sorted by time in descending order"
      (let [timestamps (map :timestamp wall)]
        (is (every? #(> (first %) (second %)) 
                    (map vector timestamps (rest timestamps))))))))

(deftest testFollow
  (reset! users {"Alice" (User. "Alice" #{})})
  (follow "Alice" "Bob")
  (is (= #{"Bob"} (get-in @users ["Alice" :followed])))
  (follow "Alice" "Charlie")
  (is (= #{"Bob" "Charlie"} (get-in @users ["Alice" :followed]))))
