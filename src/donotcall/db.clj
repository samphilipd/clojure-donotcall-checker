(ns donotcall.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource
           [javax.naming Context InitialContext])
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0    :as pool]))

(def development-db-string "postgresql://localhost:5432/donotcall")

(def db-uri
  (try
    (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL") ; will pull environment variables from a Tomcat server
    (catch javax.naming.NoInitialContextException e development-db-string))) ;; define it for development

(def spec
  (atom
    (pool/make-datasource-spec db-uri)))
