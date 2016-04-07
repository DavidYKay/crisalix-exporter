(ns crisalix.core
  (:require [crisalix.assets :refer [get-assets]]
            [crisalix.login :refer [login]]
            [crisalix.patients :refer [get-patients]]
            [taoensso.timbre :as log]))

(def default-num-pages :all)

(defn -main [& args]
  (let [[username password num-pages] args
        num-pages (if num-pages
                    (Integer/parseInt num-pages)
                    default-num-pages)]
    (log/info "logging in")
    (login username password)
    (log/info "logged in")
    (doseq [patient (get-patients num-pages)]
      (log/info "fetching patient assets: " patient)
      (get-assets patient))))
