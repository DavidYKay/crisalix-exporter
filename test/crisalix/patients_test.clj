(ns crisalix.patients-test
  (:require [midje.sweet :refer :all]
            [crisalix.patients :refer :all]))

(fact "I know when I'm done"
      (stop-fetching? 1 1) => true
      (stop-fetching? 2 1) => true
      (stop-fetching? 0 1) => false
      )

(fact "I support the :all keyword"
      (stop-fetching? 100 :all) => false
      )
