(ns indexer.core
  (:gen-class)
  (:require
   [babashka.fs :as fs]
   [cli-matic.core :refer [run-cmd]]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [expound.alpha :as ex]))

(s/def ::input-dir fs/exists?)
(ex/defmsg ::input-dir "Directory must exist.")

(s/def ::output-name (complement fs/exists?))
(ex/defmsg ::output-name "Output file already exists. Choose a different name or delete the existing file.")

(defn- build-index
  "Index the contents of input-dir into an RDF-life database (i.e., a ttl file)"
  [{:keys [input-dir output-name]}]
  (println "INPUT DIR::: " input-dir)
  (println "OUTPUT NAME::: " output-name))

(def cli-config
  {:app         {:command     "indexer"
                 :description "A utility to index the contents of your directory into a .ttl file"
                 :version     "0.0.0"}
   :global-opts [{:option  "input"
                  :short   "i"
                  :as      "Input directory (source of files to index)"
                  :type    :string
                  :spec    ::input-dir
                  :default (fs/absolutize (io/file ""))}
                 {:option  "output"
                  :short   "o"
                  :as      "Output name (for generated database)"
                  :type    :string
                  :spec    ::output-name
                  :default "db"}]
   :commands    [{:command     "build" :short "b"
                  :description ["Indexes the content of the given input-dir as RDF in a file with the given output name."]
                  :runs        build-index}]})

(defn -main
  [& args]
  (run-cmd args cli-config))
