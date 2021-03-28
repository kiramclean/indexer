(ns indexer.core
  (:gen-class)
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.cli :refer [parse-opts]]
   [indexer.db :as db]))

(def cli-options
  [["-i" "--input" "Input directory (source of files to index)"
    :default (fs/absolutize (io/file ""))
    :parse-fn #(fs/absolutize (io/file %))
    :validate [#(and (fs/exists? %) (fs/directory? %)) "Directory must exist"]]
   ["-o" "--output" "Output name (for generated database)"
    :default (fs/absolutize "db")
    :parse-fn #(fs/absolutize (io/file %))
    :validate [(complement fs/exists?) "Database already exists"]]
   ["-h" "--help" "Show this help screen"]
   ["-?" nil "Show this help screen"
    :id :help]])

(defn usage [options-summary]
  (->> ["Indexer is a a utility to index the contents of your directory into a file-based database"
        ""
        "Version: 0.0.0"
        ""
        "Usage: indexer action [options]"
        ""
        "Options:"
        "  " options-summary
        ""
        "Actions:"
        "  help     Show this help screen"
        "  build    Indexes the content of the given input directory in a database stored as a file with the given output name"
        ]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}

      ;; custom validation on arguments
      (and (= 1 (count arguments))
           (#{"build" "help"} (first arguments)))
      {:action (first arguments) :options options :summary summary}

      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [action options exit-message ok? summary]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (case action
        "help" (usage summary)
        "build" (db/build! options)))))

(comment
  (require '[clojure.tools.deps.alpha.repl :refer [add-libs]]))
