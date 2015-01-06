(ns hyper-clj.core)

(defn- keyword-starts-with? [s key]
  (.startsWith (name key) s))

(def ^:private link-key? (partial keyword-starts-with? "link-"))
(def ^:private embedded-key? (partial keyword-starts-with? "embedded-"))
(def ^:private meta-key? (partial keyword-starts-with? "meta-"))

(defn- extract-keyword [re key]
  (keyword (get (re-matches re (name key)) 1)))

(def ^:private extract-embed-keyword (partial extract-keyword #"^embedded-(.*)"))
(def ^:private extract-link-keyword (partial extract-keyword #"^link-(.*)"))
(def ^:private extract-meta-keyword (partial extract-keyword #"^meta-(.*)"))

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
(def ^:private gen-meta (partial extract-config meta-key? identity extract-meta-keyword))

(defn remove-empty-maps [mapo]
  (loop [remaining-keys (keys mapo)
         acc mapo]
    (if (empty? remaining-keys)
      acc
      (let [next-key (first remaining-keys)
            next-val (get mapo next-key)]
        (if (empty? next-val)
          (recur (rest remaining-keys) (dissoc acc next-key))
          (recur (rest remaining-keys) acc))))))

(defmacro defhyper [fn-name args & config]
  (let [config-map (apply hash-map config)
        item-sym (:item config-map)
        item-extras (remove-empty-maps {:_embedded (gen-embedded config-map)
                                        :_links (gen-links config-map)
                                        :_meta (gen-meta config-map)})]
    `(defn ~fn-name [~@args]
       (merge ~item-sym
              ~item-extras))))
