(ns donotcall.web
  (:require [donotcall.core :refer :all]
            [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [alex-and-georges.debug-repl :refer :all]
            [hiccup.page :as page]
            [clojure.java.jdbc :as sql]))

(def db "postgresql://localhost:5432/donotcall")

(defn number-exists?
  "Returns the number as a string if it exists, otherwise return false"
  [number]
  (let [{number :number} (first (sql/query db [(str "select * from do_not_call_phones where number = " number)]))]
    (if number
      number
      false)))

(defn check
  [number]
  (page/html5
    [:head
      [:title "Hello World"]]
    [:body
      (if (number-exists? number)
        (str "{number: " number "}")
        "{}")]))

(defroutes routes
  (GET "/check/:number" [number] (check number)))

(defn -main []
  (defonce server
    (ring/run-jetty #'routes {:port 3001 :join? false})))
