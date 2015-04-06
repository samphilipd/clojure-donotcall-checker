(ns donotcall.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0    :as pool]))

(def development-db-string "postgresql://localhost:5432/donotcall")

(def db-uri
  (java.net.URI.
    (try
      (. (. (InitialContext.) lookup "java:comp/env") lookup "DATABASE_URL")
      (catch javax.naming.NoInitialContextException e development-db-string)))) ;; define it for development

(def user-and-password
  (if (nil? (.getUserInfo db-uri))
    nil (clojure.string/split (.getUserInfo db-uri) #":")))

(def spec
  (pool/make-datasource-spec
    {:classname "org.postgresql.Driver"
    :subprotocol "postgresql"
    :user (get user-and-password 0)
    :password (get user-and-password 1)
    :subname (if (= -1 (.getPort db-uri))
                (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))}))
