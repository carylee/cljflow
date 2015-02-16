(ns cljflow.core
  (:require [cljflow.implementations :as implementations])
  (:import [com.amazonaws ClientConfiguration]
           [cljflow.implementations GreeterWorkflowImpl]
           [cljflow.helloworld GreeterWorkflowClientExternalFactoryImpl]
           [com.amazonaws.auth BasicAWSCredentials]
           [com.amazonaws.services.simpleworkflow AmazonSimpleWorkflowClient]
           [com.amazonaws.services.simpleworkflow.flow ActivityWorker WorkflowWorker])
  (:gen-class))

(def endpoint "https://swf.us-east-1.amazonaws.com")
(def swf-access-id (System/getenv "AWS_ACCESS_KEY_ID"))
(def swf-secret-key (System/getenv "AWS_SECRET_KEY"))
(def aws-credentials (BasicAWSCredentials. swf-access-id swf-secret-key))
(def socket-timeout (* 70 1000))
(def domain "helloWorldWalkthrough")

(defn greeter-main []
  (let [config (doto (ClientConfiguration.) (.withSocketTimeout socket-timeout))
        service (doto (AmazonSimpleWorkflowClient. aws-credentials config) (.setEndpoint endpoint))
        factory (GreeterWorkflowClientExternalFactoryImpl. service domain)
        greeter (.getClient factory "someID")]
    (.greet greeter)))

(defn greeter-worker []
  (let [config (doto (ClientConfiguration.) (.withSocketTimeout socket-timeout))
        service (doto (AmazonSimpleWorkflowClient. aws-credentials config) (.setEndpoint endpoint))
        taskListToPoll "HelloWorldList"]
    (doto (ActivityWorker. service domain taskListToPoll)
      (.addActivitiesImplementation (implementations/greeter-activities-impl))
      (.start))
    (doto (WorkflowWorker. service domain taskListToPoll)
      (.addWorkflowImplementationType GreeterWorkflowImpl)
      (.start))))

(defn -main
  "Runs the worfklow and the worker"
  [& args]
  (greeter-main)
  (greeter-worker))
