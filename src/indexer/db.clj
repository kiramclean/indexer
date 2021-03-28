(ns indexer.db
  (:require
   [datalevin.core :as dl]
   [indexer.extract :as ex]
   [indexer.load :as ld]))

(defn- create-db
  "Create DB on disk and connect to it, assume write permission to create given dir"
  [output]
  (dl/get-conn output))

(defn insert [output-dir data]
  (let [conn (create-db output-dir)]
    (try
      (dl/transact! conn data)
      (finally
        (dl/close conn)))))

(defn build!
  "Index the contents of input-dir into a file-based database"
  [{:keys [input output]}]
  (->> input
       ex/extract
       (insert output)))

(comment
  (def input "/Users/kira/code/projects/indexer/test/indexer/resources")
  (def output "/Users/kira/code/projects/indexer/tmp/db")
  (def args {:input input
             :output output})

  (build args)

  (d/q '[:find (pull ?e [*])
         :where
         [?e :file/parent ?p]
         [(str/includes? ?p "test")]]
       db)

  (d/q '[:find (pull ?e [*])
         :where
         [?e :key _]]
       db))
