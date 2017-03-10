# Parallel Calculator

It is a simple microservice implemented in Scala.

It has only one endpoint /evaluate.

###Example

```
POST http://127.0.0.1:5555/evaluate
{"expression" : "2+3"}
```

returns

```
{"result" : 5}
```

### Parallelization

The algorithm constructs a binary tree that represents given expression. Then, at depth defined in application.conf,
 the tree is cut into subtrees, and subtrees are evaluated in parallel to Double. Those results are merged to calculate
 the final result.