(ns image-resizer.core
  (:require [image-resizer.cli :as cli]
            [clojure.java.shell :refer [sh]]))

(defn magick-installed?
  []
  (zero? (:exit (sh "which" "convert"))))

(defn -main [& args]
  (when-not (magick-installed?)
    (println "Convert is required.")
    (println "Please install it first.")
    (System/exit 1))
  (let [{:keys [options errors summary]}
        (cli/parse-args args)]

    (cond
      (:help options)
      (println summary)

      errors
      (doseq [error errors]
        (println error))

      :else
      (cli/run {:options options})))
  (System/exit 0))
