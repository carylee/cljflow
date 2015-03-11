(ns cljflow.core
  (:require [cljflow.implementations :as implementations])
  (:import [com.amazonaws ClientConfiguration]
           [cljflow.helloworld GreeterActivities GreeterActivitiesClientImpl GreeterWorkflowClientExternalFactoryImpl]
           [com.amazonaws.services.simpleworkflow AmazonSimpleWorkflowClient]
           [com.amazonaws.services.simpleworkflow.flow ActivityWorker WorkflowWorker])
  (:gen-class))

(def endpoint "https://swf.us-east-1.amazonaws.com")
(def socket-timeout (* 70 1000))
(def domain "helloWorldWalkthrough")

(defn greeter-activities-impl [] 
  (reify GreeterActivities
    (getName [this] "World")
    (getGreeting [this name] (str "Hello " name "!"))
    (say [this what] (println what))))

(deftype GreeterWorkflowImpl []
  cljflow.helloworld.GreeterWorkflow
  (greet [this]
    (let [activities-client (GreeterActivitiesClientImpl.)
          name (.getName activities-client)
          greeting (.getGreeting activities-client name)]
      (.say activities-client greeting))))

(defn greeter-main []
  (let [config (doto (ClientConfiguration.) (.withSocketTimeout socket-timeout))
        service (doto (AmazonSimpleWorkflowClient. config) (.setEndpoint endpoint))
        factory (GreeterWorkflowClientExternalFactoryImpl. service domain)
        workflow-execution-id "someID"
        workflow-client (.getClient factory workflow-execution-id)]
    (.greet workflow-client)))

(defn greeter-worker []
  (let [config (doto (ClientConfiguration.) (.withSocketTimeout socket-timeout))
        service (doto (AmazonSimpleWorkflowClient. config) (.setEndpoint endpoint))
        taskListToPoll "HelloWorldList"]
    (doto (ActivityWorker. service domain taskListToPoll)
      (.addActivitiesImplementation (greeter-activities-impl))
      (.start))
    (doto (WorkflowWorker. service domain taskListToPoll)
      (.addWorkflowImplementationType GreeterWorkflowImpl)
      (.start))))

(defn -main
  "Runs the worfklow and the worker"
  [& args]
  (greeter-main)
  (greeter-worker))
