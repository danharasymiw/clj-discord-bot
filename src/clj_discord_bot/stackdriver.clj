(ns clj-discord-bot.stackdriver
  (:import (com.google.cloud.logging LogEntry Severity Logging LoggingOptions Payload$StringPayload Logging$WriteOption)
           (com.google.cloud MonitoredResource)
           (java.util Collections)))


(defn level->severity [level]
  (case level
    :info (Severity/INFO)
    :warn (Severity/WARNING)
    :error (Severity/ERROR)
    (Severity/INFO)))

(defn log [data level]
  (let [entry (-> (LogEntry/newBuilder (Payload$StringPayload/of data))
                  (.setLogName "clj-discord-bot")
                  (.setSeverity (level->severity level))
                  (.setResource (-> (MonitoredResource/newBuilder "gce_instance")
                                    (.addLabel "instance_id" "Clojure Bot")
                                    (.build)))
                  (.build))]
    (try
      (with-open [^Logging logging (-> (LoggingOptions/getDefaultInstance)
                                       (.getService))]
        (-> logging
            (.write (Collections/singleton entry) (into-array Logging$WriteOption '()))))
      (catch Exception e
        (println data)))))
