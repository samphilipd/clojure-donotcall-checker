(ns donotcall.web
  (:require [donotcall.core :refer :all]
            [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [alex-and-georges.debug-repl :refer :all]
            [clojure.java.jdbc :as sql])
  (:gen-class))

(def db (atom "postgresql://localhost:5432/donotcall")) ;; define it for development

(defn number-exists?
  "Returns the number as a string if it exists, otherwise return false"
  [number]
  (let [result (first (sql/query @db ["SELECT number FROM do_not_call_phones WHERE number = ?" number]))]
    (if result
      {number :number}
      false)))

(defn check
  [number]
    (if (number-exists? number)
      (str "{number: " number "}")
      "{}"))

(defroutes routes
  (GET "/donotcall/:number" [number] (check number)))

(defn -main []
  (if-let [database-url (System/getenv "DATABASE_URL")]
    (reset! db database-url))
  (ring/run-jetty #'routes {:port 3001 :join? false}))
