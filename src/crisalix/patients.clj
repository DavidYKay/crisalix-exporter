(ns crisalix.patients
  (:require [crisalix.login :refer [cookie-store]]
            [crisalix.network :refer [api-get]]
            [reaver :refer [parse extract-from text attr attrs]]))

(def host "https://pro.crisalix.com")
(def first-page "/patients?_pjax=%5Bdata-pjax-container%5D")

(defn extract-id
  "take a URL: /mammo/patients/129284/edit and return the ID. I.e 129284"
  [path]
  (Integer/parseInt (nth (clojure.string/split path #"/") 3)))


(defn extract-data [html]
  (extract-from (parse html) "table tbody.patients_list tr"
                [:name :id]
                "td.name_content a" text
                "td.name_content a" #(extract-id (attr % :href))))

(defn disabled? [class-str]
  (contains? (set (clojure.string/split class-str #" ")) "disabled"))

(defn get-page
  "Gets a page of patients from the backend"
  [path]
  (:body (api-get (str host path) {:cookie-store cookie-store :insecure? true})))

(defn next-page [html]
  (first (extract-from (parse html) "div.pagination"
                       [:url :last-page?]
                       ".next_page" (attr :href)
                       ".next_page" #(-> (attr % :class)
                                         disabled?))))

(defn stop-fetching? [cur-page num-pages]
  (if (= num-pages :all)
    false
    (>= cur-page num-pages)))

(defn get-patients
  "Fetch N pages of patients from the site."
  [num-pages]
  (loop [cur-page 1
         url first-page
         accum []]
    (let [html (get-page url)
          patients (extract-data html)
          accum (into accum patients)
          {:keys [url last-page?]} (next-page html)]
      (if (or last-page? (stop-fetching? cur-page num-pages))
        accum
        (recur (inc cur-page)
               url
               accum)))))
