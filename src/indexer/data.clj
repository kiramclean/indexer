(ns indexer.data
  (:require
    [clojure.java.io :as io]
    [datoteka.core :as fs]
    [clojure.string :as str]))

(defn- dot-file? [path]
  (str/starts-with? (fs/name path) "."))

(defn get-files [input-dir]
  (->> input-dir
       io/file
       file-seq
       (remove fs/directory?)
       (remove dot-file?)))
