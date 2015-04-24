(ns donotcall.db
  (:import [javax.naming Context InitialContext])
  (:require [clojure.java.jdbc :as jdbc]))

(def development-db-string "postgresql://localhost:5432/donotcall")

; First check to see if we are running inside Tomcat, if so use InitialContext to get access to DATABASE_URL
; Then check for the DATABASE_URL env variable directly
; Lastly fall back to the development url
(def db-uri
  (atom
    (try
      (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL") ; will pull environment variables from a Tomcat server
      (catch javax.naming.NoInitialContextException e
        (or
          (System/getenv "DATABASE_URL")
          development-db-string)))))

(def spec @db-uri)
