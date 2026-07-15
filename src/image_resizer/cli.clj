(ns image-resizer.cli
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.tools.cli :refer [parse-opts]]))

(def cli-options
  [["-q" "--quality QUALITY" "WebP quality"
    :default 85
    :parse-fn #(Integer/parseInt %)]
   
   ["-p" "--path PATH" "Image folder"
    :default "content/img"]

   ["-h" "--help"]])

(defn parse-args [args]
  (parse-opts args cli-options))

(defn magick-command
  [{:keys [input output quality]}]
  ["convert"
   input
   "-strip"
   "-quality" (str quality)
   output])

(defn convert-webp
  [img
   quality]
  (let [input         (str (fs/path img))
        output        (str (fs/strip-ext img) ".webp")
        command       (magick-command {:input   input
                                       :output  output
                                       :quality quality})
        result        (p/shell command)]
    (when (zero? (:exit result))
      (println "Deleting image:" (fs/file-name img))
      (fs/delete img))
    result))

(defn run [{{:keys [quality path]} :options :as opts}] 
  (doseq [img (fs/glob path "**{.png,jpeg,jpg}")]
    (let [{:keys [exit]} (convert-webp img quality)]
      (if (zero? exit)
        (println "✓" (fs/file-name img) "Correctly converted to webp")
        (println "✗" (fs/file-name img))))))
