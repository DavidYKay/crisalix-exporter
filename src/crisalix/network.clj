(ns crisalix.network
  (:require [clj-http.client :as client]
            [slingshot.slingshot :refer [try+ throw+]]
            [taoensso.timbre :as log]))

(defn api-call [f]
  (try+
   (f)
   (catch [:status 403] {:keys [request-time headers body]}
     (log/warn "403" request-time headers))
   (catch [:status 404] {:keys [request-time headers body]}
     (log/warn "NOT Found 404" request-time headers body))
   (catch [:status 500] {:keys [request-time headers body]}
     (log/warn "Server Error 500" request-time headers body))
   (catch Object _
     (log/error (:throwable &throw-context) "unexpected error")
     (throw+))))

(defn api-get [url params]
  (api-call #(client/get url params)))

(defn api-post [url params]
  (api-call #(client/post url params)))
