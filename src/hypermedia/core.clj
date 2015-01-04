(ns hypermedia.core)

(defn- keyword-starts-with? [s key]
  (.startsWith (name key) s))

(def ^:private link-key? (partial keyword-starts-with? "link-"))
(def ^:private embedded-key? (partial keyword-starts-with? "embedded-"))

(defn- extract-keyword [re key]
  (keyword (get (re-matches re (name key)) 1)))

(def ^:private extract-embed-keyword (partial extract-keyword #"^embedded-(.*)"))
(def ^:private extract-link-keyword (partial extract-keyword #"^link-(.*)"))

(defn- get-full-link-spec [link-spec]
  (if (map? link-spec)
    link-spec
    {:href link-spec}))

(defn- extract-config [key-filter-fn map-value-fn keyword-fn config-map]
  (loop [config-keys (filter key-filter-fn (keys config-map))
         acc {}]
    (if (empty? config-keys)
      acc
      (let [config-key (first config-keys)
            config-val (map-value-fn (get config-map config-key))
            acc-key (keyword-fn config-key)]
        (recur (rest config-keys) 
               (assoc acc acc-key config-val))))))

(def ^:private gen-embedded (partial extract-config embedded-key? identity extract-embed-keyword))
(def ^:private gen-links (partial extract-config link-key? get-full-link-spec extract-link-keyword))

(defmacro defhyper [fn-name args & config]
  (let [config-map (apply hash-map config)
        item-sym (:item config-map)
        links (gen-links config-map)
        embedded (gen-embedded config-map)]
    `(defn ~fn-name [~@args]
       (merge ~item-sym
              {:_embedded ~embedded
               :_links ~links}))))
