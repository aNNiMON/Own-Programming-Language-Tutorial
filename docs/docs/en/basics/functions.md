# Functions definition

To define function uses the `def` keyword:

```own
def function(arg1, arg2) {
  println arg1
}
```

## Shorthand definition

There is short syntax fot function body:

```own
def repeat(str, count) = str * count
```

Which is equivalent to:

```own
def repeat(str, count) {
  return str * count
}
```

## Default arguments

Function arguments can have default values.

```own
def repeat(str, count = 5) = str * count
```

In this case only `str` argument is required.

```own
repeat("*")     //  *****
repeat("+", 3)  //  +++
```

Default arguments can't be declared before required arguments.

```own
def repeat(str = "*", count) = str * count
```

Causes parsing error: `ParseError on line 1: Required argument cannot be after optional`

## Inner functions

You can define function in other function.

@[code](../../code/basics/fibonacci.own)
