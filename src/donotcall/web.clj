(ns donotcall.web
  (:require [donotcall.core :refer :all]
            [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [alex-and-georges.debug-repl :refer :all]
            [clojure.java.jdbc :as sql])
  (:import [javax.naming Context InitialContext])
  (:gen-class))

(def db
  (try
    (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL")
    (catch javax.naming.NoInitialContextException e "postgresql://localhost:5432/donotcall"))) ;; define it for development

(defn number-exists?
  "Returns the number as a string if it exists, otherwise return false"
  [number]
  (let [result (first (sql/query db ["SELECT number FROM do_not_call_phones WHERE number = ?" number]))]
    (if result
      {number :number}
      false)))

(defn check
  [number]
    (if (number-exists? number)
      (str "{number: " number "}")
      "{}"))

(defroutes routes
  (GET "/donotcall/:number" [number] (check number))
  (GET "/debuginfo" [] (str "db: " db "\nDATABASE_URL: " (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL"))))

(defn -main []
  (ring/run-jetty #'routes {:port 3001 :join? false}))
