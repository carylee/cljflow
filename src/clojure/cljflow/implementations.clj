(ns cljflow.implementations
  (:import [cljflow.helloworld GreeterActivities])
  (:gen-class
    :name cljflow.implementations.GreeterWorkflowImpl
    :implements [cljflow.helloworld.GreeterWorkflow]
    :prefix wf-))

(defn greeter-activities-impl [] 
  (reify GreeterActivities
    (getName [this] "World")
    (getGreeting [this name] (str "Hello " name "!"))
    (say [this what] (println what))))

(def operations (greeter-activities-impl))

(defn wf-greet [this]
  (let [name (.getName operations)
        greeting (.getGreeting operations name)]
    (.say operations greeting)))
