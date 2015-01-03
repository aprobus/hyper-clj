(ns hypermedia.core-spec
  (:require [speclj.core :refer :all]
            [hypermedia.core :refer :all]))

(defhyper plain-representer [item]
  :item item)

(defhyper links-representer [item]
  :item item
  :link-self (str "localhost/" (:id item)))

(defhyper complex-link-representer [item]
  :item item
  :link-submit {:method "POST"
                :href (str "localhost/" (:id item))})

(defhyper collection-representer [page-details words]
  :item page-details
  :embedded-words words)

(defhyper static-representer []
  :item {:a 1}
  :link-self "localhost"
  :embedded-words ["hello" "you"])

(describe "defhypermedia-singular"
  (context "with plain-representer"
    (it "represents the map"
      (should= {:a 1 :_embedded {} :_links {}}
               (plain-representer {:a 1}))))

  (context "with links-representer"
    (it "represents the links"
      (should= {:self {:href "localhost/1"}}
               (:_links (links-representer {:id 1})))))

  (context "with complex-link-representer"
    (it "represents the links"
      (should= {:submit {:method "POST" :href "localhost/1"}}
               (:_links (complex-link-representer {:id 1})))))

  (context "with collection-representer"
    (it "has embedded words"
      (should= {:words ["hello" "you"]}
               (:_embedded (collection-representer {:limit 1 :offset 1} 
                                                   ["hello" "you"])))))

  (context "with static-representer"
    (it "has embedded"
      (should= {:words ["hello" "you"]}
               (:_embedded (static-representer))))

    (it "has links"
      (should= {:self {:href "localhost"}}
               (:_links (static-representer))))))
