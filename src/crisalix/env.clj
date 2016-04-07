(ns crisalix.env
  (:import [java.io FileReader]))

(defn set-proxy []
  (def HOST "127.0.0.1")

  (System/setProperty "http.proxyHost" HOST)
  (System/setProperty "http.proxyPort" "8888")

  (System/setProperty "https.proxyHost" HOST)
  (System/setProperty "https.proxyPort" "8888")
  )

(defn read-resource-as-stream [filename]
    (FileReader.
      (.getFile (clojure.java.io/resource filename))))

(defn read-resource-as-text [filename]
  (slurp
    (read-resource-as-stream filename)))
