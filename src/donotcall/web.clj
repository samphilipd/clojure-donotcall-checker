(ns donotcall.web
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler]
            [donotcall.db :as db]
            [ring.adapter.jetty :as ring]
            [alex-and-georges.debug-repl :refer :all]
            [clojure.java.jdbc :as sql]
            [clojure.data.json :as json]
            [clojure.string :as s])
  (:gen-class))

(def batch-limit 1000 )

(defn number-exists?
  "Returns the number as a string if it exists, otherwise return false"
  [number]
  (let [result (first (sql/query db/spec ["SELECT number FROM do_not_call_phones WHERE number = ?" number]))]
    (if result
      {number :number}
      false)))

(defn generate-?s
  "Generates question marks. E.g. for n=3 return \"?, ?, ?\""
  [n]
  (clojure.string/join ", " (take n (repeat "?"))))

(defn multiple-number-sql-query
  [array-of-numbers]
  (let [n (count array-of-numbers)]
    (concat [
      (str "SELECT number FROM do_not_call_phones WHERE number IN ("
           (generate-?s n)
           ")")
    ] array-of-numbers)))

(defn format-numbers-result
  "Takes something like this:  [{:number 1}, {:number 2}]
  and makes it look like this: {:numbers [1, 2]}"
  [result]
  {:numbers (map :number result)})

(defn matching-numbers
  "Takes a comma separated list numbers and returns a map of {number boolean}
  for each number with true if it is found and false if not"
  [numbers]
  (let [array-of-numbers (s/split numbers #",")]
    (if (> (count array-of-numbers) batch-limit)
      {:errors (str "you may only request up to " batch-limit " numbers in one request")}
      (format-numbers-result (sql/query db/spec (multiple-number-sql-query array-of-numbers))))))

(defn check
  [number]
    (if (number-exists? number)
      {:status 200
       :body (str "{\"number\": \"" number "\"}")}
      {:status 404
       :body "{}"}))

(defn check-multiple
  "Takes a comma-separated list of numbers and returns json with true/false
  depending on whether each number was found in the do not call list"
  [numbers]
  (if numbers
    {:status 200
     :body (json/write-str (matching-numbers numbers))}
    {:status 400
     :body (json/write-str {:error "invalid parameter"})}))


(defroutes routes
  (GET "/donotcall/:number" [number] (check number))
  (GET "/batch*" {{numbers :numbers} :params} (check-multiple numbers)))

(def handler (-> routes compojure.handler/api))

(defn -main []
  (ring/run-jetty #'handler {:port 3001 :join? false}))
