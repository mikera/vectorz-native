(ns mikera.test-vectorz
  (:use [clojure.core matrix])
  (:require [criterium.core :as c]))

(defn rmatrix [m n]
  (mikera.vectorz.nativeimpl.BlasMatrix/create (int m) (int n)))

(set-current-implementation :vectorz)

(comment 
  (let [size 512
        m (rmatrix size size)
        n (transpose (rmatrix size size))]
     (c/quick-bench (mmul m n)))
  )