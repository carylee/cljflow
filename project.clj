(defproject cljflow "0.1.0-SNAPSHOT"
  :description "Dummy program using Clojure with SWF Flow Framework"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.freemarker/freemarker "2.3.18"]
                 [cheshire "5.4.0"]
                 [org.clojure/tools.namespace "0.2.8"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [metrics-clojure "2.4.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [net.logstash.log4j/jsonevent-layout "1.7"]
                 [com.amazonaws/aws-java-sdk-swf-libraries "1.9.16" :exclusions [joda-time]]
                 [com.amazonaws/aws-java-sdk-simpleworkflow "1.9.16" :exclusions [joda-time]]
                 [com.amazonaws/aws-java-sdk-flow-build-tools "1.9.16" :exclusions [joda-time]]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :main ^:skip-aot cljflow.core
  :target-path "target/%s"
  :mirrors {"central" {:name "Usermind Nexus"
                       :url "http://nexus.internal.usermind.com:8081/nexus/content/groups/public/"}}
  :repositories {"maven" {:url "http://repo1.maven.org/maven2"}}
  :plugins [[lein-ring "0.8.11"]]
  :aot [cljflow.activities]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}})
