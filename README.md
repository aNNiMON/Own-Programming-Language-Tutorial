# OwnLang

[![Build Status](https://travis-ci.org/aNNiMON/Own-Programming-Language-Tutorial.svg?branch=latest)](https://travis-ci.org/aNNiMON/Own-Programming-Language-Tutorial)

OwnLang - dynamic functional programming language inspired by Scala and Python. Available for PC, Android and Java ME devices.

## Installing

| Free | Pro | Desktop |
| :--: | :-: | :-----: |
| [![Free](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.annimon.ownlang.free) | [![Pro](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.annimon.ownlang) | [v1.4.0](https://github.com/aNNiMON/Own-Programming-Language-Tutorial/releases/tag/v1.4.0)

Also available as AUR package:

```
yay -S ownlang
```

## Key features

#### Higher-order functions

Functions are values, so we can store them to variables for operating.

```scala
operations = {
  "+" : def(a,b) = a+b,
  "-" : def(a,b) = a-b,
  "*" : def(a,b) = a*b,
  "/" : ::division
}
def division(v1, v2) {
  if (v2 == 0) return "error"
  return v1 / v2
}

for operation : operations {
  println operation(2, 3)
}
```

#### Pattern Matching

Pattern matching with value pattern, tuple pattern, list pattern and optional condition.

```scala
def factorial(n) = match n {
  case 0: 1
  case n if n < 0: 0
  case _: n * factorial(n - 1)
}

def fizzbuzz(limit = 100) {
  for i = 1, i <= limit, i++ {
    println match [i % 3 == 0, i % 5 == 0] {
      case (true, false): "Fizz"
      case (false, true): "Buzz"
      case (true, true): "FizzBuzz"
      case _: i
    }
  }
}
```

#### Functional data operations

Operate data in functional style.

```scala
use ["std", "functional"]

nums = [1,2,3,4,5,6,7,8,9,10]
nums = filter(nums, def(x) = x % 2 == 0)
// Squares of even numbers
squares = map(nums, def(x) = x * x)
foreach(squares, ::echo)
// Sum of squares
sum = reduce(squares, 0, def(x, y) = x + y)
println "Sum: " + sum
// Same using stream
println "Sum: " + stream(range(1, 11))
  .filter(def(x) = x % 2 == 0)
  .map(def(x) = x * x)
  .reduce(0, def(x, y) = x + y)
```

#### Operator overloading

Why not?

```scala
use ["std", "types", "math"]

def `..`(a, b) = range(a, b)
def `**`(a, b) = int(pow(a, b))
for y : 1 .. 10 {
  println sprintf("2 ^ %d = %d", y, 2 ** y)
}
```

#### Network module

Easy async HTTP requests with `http` module.

```scala
use "std"
use "http"
use "functional"

// GET request
http("https://api.github.com/events", def(r) {
  use "json"
  events = jsondecode(r)
})

// POST request
http("http://jsonplaceholder.typicode.com/users", "POST", {
  "name": "OwnLang",
  "versionCode": 10
}, ::echo)

// PATCH request
http("http://jsonplaceholder.typicode.com/users/2", "PATCH", {"name": "Patched Name"}, ::patch_callback)

def patch_callback(v) {
  println v
}
```

## Language specification

[English](https://annimon.com/docs/ownlang/en/) and [Russian](https://annimon.com/docs/ownlang/ru/)

[Examples](examples/)


## Build

Build using Gradle `./gradlew dist`

or take a look to [latest release](https://github.com/aNNiMON/Own-Programming-Language-Tutorial/releases/latest) for binaries.


## Run

To run script use command

`java -jar OwnLang.jar -f input.own`

or 

`java -jar OwnLang.jar < input.own`


## License

MIT - see [MIT licence information](LICENSE)
