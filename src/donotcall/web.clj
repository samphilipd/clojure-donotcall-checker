(ns donotcall.web
  (:require [compojure.core :refer [defroutes GET]]
            [donotcall.db :as db]
            [ring.adapter.jetty :as ring]
            [alex-and-georges.debug-repl :refer :all]
            [clojure.java.jdbc :as sql])
  (:gen-class))

(defn number-exists?
  "Returns the number as a string if it exists, otherwise return false"
  [number]
  (let [result (first (sql/query db/spec ["SELECT number FROM do_not_call_phones WHERE number = ?" number]))]
    (if result
      {number :number}
      false)))

(defn check
  [number]
    (if (number-exists? number)
      {:status 200
       :body (str "{number: " number "}")}
      {:status 404
       :body "{}"}))

(defroutes routes
  (GET "/donotcall/:number" [number] (check number)))

(defn -main []
  (ring/run-jetty #'routes {:port 3000 :join? false}))
