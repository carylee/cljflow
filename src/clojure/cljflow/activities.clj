(ns cljflow.activities
  (:import [cljflow.helloworld.activities HelloWorldActivities
            HelloWorldWorkflow]))

(defn hello-world-activities-impl [] 
  (reify HelloWorldActivities
    (printHello [this name]
      (println "Hello" name "!"))))

(defn hello-world-workflow-impl []
  (let [client (hello-world-activities-impl)]
    (reify HelloWorldWorkflow
      (helloWorld [this name]
        (.printHello client)))))
