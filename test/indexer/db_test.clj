(ns indexer.db-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer [deftest testing is]]
    [datalevin.core :as dl]
    [babashka.fs :as fs]
    [indexer.db :as sut]))

(def output "tmp")

(defn- cleanup! []
  (when (fs/exists? output)
    (fs/delete-tree output)))

(deftest build-test
  (cleanup!)

  (testing "it builds a database file from the input dir contents"
    (let [input "test/indexer/resources/test-build/"
          _ (sut/build {:input input :output output})
          db (dl/db (dl/get-conn output))]
      (is (= 2 (count (fs/list-dir output))))
      (is (= 2 (dl/q '[:find (pull ?e [*]) :where [?e :key _]]
                     db)))))

  (cleanup!))
