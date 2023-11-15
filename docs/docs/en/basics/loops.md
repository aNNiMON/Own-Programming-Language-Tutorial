# Loops

## while loop

```own
while condition {
   body
}
```

Parentheses in condition are not necessary.

```own
i = 0
while i < 5 {
  print i++
}

// or

i = 0
while (i < 5) {
  print i++
}
```

## do-while loop

```own
do {
   body
} while condition
```

Parentheses in condition are not necessary.

```own
i = 0
do {
  print i++
} while i < 5

// or

i = 0
do {
  print i++
} while (i < 5)
```

## for loop

```own
for initializing, condition, increment {
   body
}

for (initializing, condition, increment) {
   body
}
```

```own
for i = 0, i < 5, i++
  print i++

// or

for (i = 0, i < 5, i++) {
  print i++
}
```

## foreach loop

Iterates elements of an string, array or map.

Iterating over string:

```own
for char : string {
   body
}
for char, code : string {
   body
}
```

Iterating over array:

```own
for value : array {
   body
}
for value, index : array {
   body
}
```

Iterating over map:

```own
for key, value : map {
   body
}
for (key, value : map) {
   body
}
```

Parentheses are not necessary.

@[code](../../code/basics/loops1.own)
