(defproject cljflow "0.1.0-SNAPSHOT"
  :description "Hello World example using Clojure & AWS SWF Flow Framework"
  :url "http://github.com/carylee/cljflow"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.freemarker/freemarker "2.3.18"]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [joda-time/joda-time "2.3"]
                 [com.amazonaws/aws-java-sdk-swf-libraries "1.9.16" :exclusions [joda-time]]
                 [com.amazonaws/aws-java-sdk-simpleworkflow "1.9.16" :exclusions [joda-time]]
                 [com.amazonaws/aws-java-sdk-flow-build-tools "1.9.16" :exclusions [joda-time]]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :main ^:skip-aot cljflow.core
  :target-path "target/%s"
  :repositories {"maven" {:url "http://repo1.maven.org/maven2"}}
  :profiles {:uberjar {:aot :all}})
