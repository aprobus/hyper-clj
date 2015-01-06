# Hyper-Clj

A Clojure library for representing hypermedia.

## Usage

Create a representer like this

```clojure
(defhyper represent-book [book author]
  :item book
  :link-self (str "/books" (:id book))
  :embedded-author author
  :meta-info "Meta info")
```

and then call it with 

```clojure
(represent-book {:title "For Whom The Bell Tolls" :id 10} 
                {:name "Ernest Hemingway"})
```

which results in

```clojure
{:title "For Whom The Bell Tolls"
 :_links {:self {:href "/books/10"}}
 :_embedded {:author {:name "Ernest Hemingway"}}
 :_meta {:info "Meta info"}}
```

Any meta/link/embedded is supported. Simply prepend the config with the appropriate word.

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
