(ns chicken.cli.commands.wall-test
  (:require [clojure.test :refer :all]
            [chicken.repository :as rep]
            [chicken.domain]
            [chicken.cli.commands.wall :refer :all])
  (:import (chicken.domain Post)))

(deftest testValidate
  (are [x y] (= x y)
    :ok    (validate "Alice wall")
    :ok    (validate "  Alice   wall   "))
  (are [x] (:errors x)
    (validate "wall Alice")
    (validate "Alicewall")))

(deftest testWall
  (testing "prints all posts returned by get wall"
    (with-redefs [rep/getWall (fn [_] (list
                                        (Post. "second" "Alice" 2)
                                        (Post. "first" "Bob" 1)))
                  chicken.cli.app/timeFormat  (fn [_] "mock time")]
      (is (=
            "\nAlice - second (mock time)
  Bob - first (mock time)\n\n"
            (with-out-str
              (wall "Alice wall"))))))
  (testing "Prints posts with appended and prepended line-feed.
           Each line has format 'Username - Message text (formatted time)'"
    (with-redefs [rep/getWall (fn [_] (list
                                        (Post. "first" "Bob" 1)))
                  chicken.cli.app/timeFormat  (fn [_] "mock time")]
      (is (= "\nBob - first (mock time)\n\n"
            (with-out-str
              (wall "Alice wall")))))))
