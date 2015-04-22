(ns donotcall.db
  (:import [javax.naming Context InitialContext])
  (:require [clojure.java.jdbc :as jdbc]))

(def development-db-string "postgresql://localhost:5432/donotcall")

(def db-uri
  (atom
    (try
      (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL") ; will pull environment variables from a Tomcat server
      (catch javax.naming.NoInitialContextException e development-db-string)))) ;; define it for development

(def spec @db-uri)
