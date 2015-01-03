(ns hypermedia.core)

(defn- keyword-starts-with? [s key]
  (.startsWith (name key) s))

(def link-key? (partial keyword-starts-with? "link-"))
(def embedded-key? (partial keyword-starts-with? "embedded-"))

(defn- extract-keyword [re key]
  (keyword (get (re-matches re (name key)) 1)))

(def extract-embed-keyword (partial extract-keyword #"^embedded-(.*)"))
(def extract-link-keyword (partial extract-keyword #"^link-(.*)"))

(defn- get-full-link-spec [link-spec]
  (if (map? link-spec)
    link-spec
    {:href link-spec}))

(defn- gen-embedded [config-map]
  (loop [embedded-keys (filter embedded-key? (keys config-map))
         acc {}]
    (if (empty? embedded-keys)
      acc
      (let [embed-key (first embedded-keys)]
        (recur (rest embedded-keys)
               (assoc acc
                      (extract-embed-keyword embed-key)
                      (get config-map embed-key)))))))

(defn- gen-links [config-map]
  (loop [link-keys (filter link-key? (keys config-map))
         acc {}]
    (if (empty? link-keys)
      acc
      (let [link-key (first link-keys)
            link-spec (get-full-link-spec (get config-map link-key))]
        (recur (rest link-keys)
               (assoc acc
                      (extract-link-keyword link-key)
                      link-spec))))))

(defmacro defhyper [fn-name args & config]
  (let [config-map (apply hash-map config)
        item-sym (:item config-map)
        links (gen-links config-map)
        embedded (gen-embedded config-map)]
    `(defn ~fn-name [~@args]
       (merge ~item-sym
              {:_embedded ~embedded
               :_links ~links}))))
