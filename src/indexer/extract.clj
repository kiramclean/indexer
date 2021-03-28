(ns indexer.extract
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [frontmatter.core :as fm]))

(defn get-files [input-dir]
  (->> input-dir
       io/file
       file-seq
       (remove fs/directory?)
       (remove fs/hidden?)))

(defn- flatten-front-matter [{:keys [body frontmatter]}]
  (cond-> frontmatter
    (not (str/blank? body)) (assoc :file/content body)))

(defn extract-metadata [file]
  (-> file
      fm/parse
      flatten-front-matter
      (assoc :file/parent (-> file fs/parent fs/absolutize str))
      (assoc :file/name (fs/file-name file))
      (assoc :file/ext (fs/extension file))))

(defn extract [input-dir]
  (->> input-dir
       get-files
       (map extract-metadata)))
