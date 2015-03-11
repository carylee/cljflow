(ns cljflow.core
  (:require [cljflow.implementations :as implementations])
  (:import [com.amazonaws ClientConfiguration]
           [cljflow.helloworld GreeterWorkflow GreeterActivities GreeterActivitiesClientImpl GreeterWorkflowClientExternalFactoryImpl]
           [com.amazonaws.services.simpleworkflow AmazonSimpleWorkflowClient]
           [com.amazonaws.services.simpleworkflow.flow ActivityWorker WorkflowWorker DecisionContextProviderImpl]
           [com.amazonaws.services.simpleworkflow.flow.core Functor Promise Task])
  (:gen-class))

(def endpoint "https://swf.us-east-1.amazonaws.com")
(def socket-timeout (* 70 1000))
(def domain "helloWorldWalkthrough")

(defn get-clock []
  (-> (DecisionContextProviderImpl.)
      .getDecisionContext
      .getWorkflowClock))

(defn promise-value [^Promise p]
  (when p (.get p)))

(defn create-task
  "Takes a function and any number of promises and returns an AWS Flow async Task
   that calls that function with the value of the promises once they complete."
  [f & promises]
  (let [promises (into-array Promise promises)]
    (proxy [Task] [promises]
      (doExecute []
        (apply f (map promise-value promises))))))

(defn create-functor
  "Takes a function and any number of promises and returns an AWS Flow Async Functor
   that calls that function with the value of the promises once they complete."
  [f & promises]
  (let [promises (into-array Promise promises)]
    (proxy [Functor] [promises]
      (doExecute [] 
        (apply f (map promise-value promises))))))

(defn get-greeting [name]
  (str "Hello " name "!"))

(defn greeter-activities-impl [] 
  (reify GreeterActivities
    (getName [this] "World")
    (getGreeting [this name] (get-greeting name))
    (say [this what] (println what))))

; Simple, synchronous hello world workflow implementation
(deftype GreeterWorkflowImpl []
  GreeterWorkflow
  (greet [this]
    (let [activities-client (GreeterActivitiesClientImpl.)
          name (.getName activities-client)
          greeting (.getGreeting activities-client name)]
      (.say activities-client greeting))))

; Asynchronous hello world workflow implementation
(deftype GreeterWorkflowAsyncImpl []
  GreeterWorkflow
  (greet [this]
    (let [activities-client (GreeterActivitiesClientImpl.)]
      (->> (.getName activities-client)
           (create-functor #(.getGreeting activities-client %))
           (create-task #(.say activities-client %))))))

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
      (.addWorkflowImplementationType GreeterWorkflowAsyncImpl)
      (.start))))

(defn -main
  "Runs the worfklow and the worker"
  [& args]
  (greeter-main)
  (greeter-worker))
