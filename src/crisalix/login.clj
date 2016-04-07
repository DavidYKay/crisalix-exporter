(ns crisalix.login
  (:require [crisalix.network :refer [api-get api-post]]
            [reaver :refer [parse extract-from text attr attr* attrs]]))

(defonce cookie-store (clj-http.cookies/cookie-store))


(defn get-webpage []
  (:body (api-get "https://sso.crisalix.com/signin"
                     {:insecure? true})))

(defn extract-token [html]
  (:authenticity-token
   (first (extract-from (parse html) "form#new_user.new_user"
                         [:authenticity-token]
                         "input:nth-child(2)[type=\"hidden\"]" (attr :value)))))

(defn login
  "Performs login and gets a cookie"
  [username password]
  (let [page (get-webpage)
        token (extract-token page)]
    (api-post "https://sso.crisalix.com/users/sign_in"
              {:form-params {"user[email]" username
                             "user[password]" password
                             :authenticity_token token
                             :utf8 "✓"
                             :button nil}
               :cookie-store cookie-store
               :insecure? true})))
