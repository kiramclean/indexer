(ns indexer.extract-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [indexer.extract :as sut]))

(deftest get-files
  (testing "it gets only non-dot files from a directory"
    (let [result (sut/get-files "test/indexer/resources/files")]
      (is (= 2 (count result)))
      (is (= (->> result (map str) set)
             #{"test/indexer/resources/files/test-one.md"
               "test/indexer/resources/files/test-two.md"})))))

(deftest parse-front-matter
  (testing "it extracts yaml front-matter"
    (let [result (sut/extract-metadata "test/indexer/resources/front-matter/yaml.md")]
      (is (contains? result :file/parent))
      (is (= {:title "Hi this is a title"
              :one "One"
              :two "Whee"
              :file/content "Content"
              :file/name "yaml.md"
              :file/ext "md"}
             (dissoc result :file/parent)))))

  (testing "it works when there is no body"
    (let [result (sut/extract-metadata "test/indexer/resources/front-matter/yaml-no-body.md")]

      (is (= {:key "value" :file/name "yaml-no-body.md" :file/ext "md"}
             (dissoc result :file/parent)))))

  (testing "it works with edn"
    (let [result (sut/extract-metadata "test/indexer/resources/front-matter/edn.md")]
      (is (= {:key "value" :file/name "edn.md" :file/ext "md"}
             (dissoc result :file/parent)))))

  (testing "it works with json front-matter"
    (let [result (sut/extract-metadata "test/indexer/resources/front-matter/json.md")]
      (is (= {:key "value" :file/name "json.md" :file/ext "md"}
             (dissoc result :file/parent))))))
