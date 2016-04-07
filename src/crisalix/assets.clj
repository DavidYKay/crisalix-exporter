(ns crisalix.assets
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [crisalix.login :refer [cookie-store]]
            [crisalix.network :refer [api-get]]
            [taoensso.timbre :as log]))

(defn zip-str [s]
  (zip/xml-zip 
      (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-metadata
  "Gets all the metadata for a patient"
  [{id :id :as patient}]
  (->> (api-get (format "https://pro.crisalix.com/api/unity/options/%s?type=mammo&guided=false" id)
                {:cookie-store cookie-store :insecure? true})
       :body
       zip-str
       zip/children
       (map (fn [{:keys [tag content]}]
              [tag (first content)]) )
       (into {})))

(defn last-path-component [url]
  (-> (clojure.string/split url #"/")
      last))

(defn texture-url [k3d-url]
  (clojure.string/join "/"
                       (-> (clojure.string/split k3d-url #"/") 
                           butlast
                           (concat ["myTexture.jpg"]))))

(defn asset-urls
  "given the k3d url, produce the other related urls.
  example k3d url: https://pro.crisalix.com/estetix_cabinet/9200/10eceeaa6b9de05c/100/6fa2284e771e9aba/rec/mammo/completebody.k3d"
  [k3d-url]
  {:k3d k3d-url
   :texture (texture-url k3d-url)})

(defn file-name [url]
  (letfn [(first-last [v]
            [(first v) (last v)])]
  (clojure.string/join "/"
                       (->> (clojure.string/split url #"/")
                            (take-last 4)
                            first-last))))

(defn download-asset [url]
  (let [response (api-get url {:as :stream
                               :cookie-store cookie-store
                               :insecure? true})
        path (format "assets/%s" (file-name url))]
    (when response
      (io/make-parents path)
      (io/copy (:body response)
               (io/file path)))))

(defn get-assets
  "Gets the 3d assets for a given patient"
  [{id :id :as patient}]
  (let [{k3d-url :reconstruction_k3d :as metadata} (get-metadata patient)]
    (doseq [[k v] (asset-urls k3d-url)]
      (download-asset v))))

