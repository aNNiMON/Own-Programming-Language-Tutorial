# Types

OwnLang types are:

 * Number - numbers (integer, float)
 * String - strings
 * Array - arrays
 * Map - objects (an associative arrays)
 * Function - functions

Since OwnLang is dynamic programming language, which means that explicitly declare the types is not necessary.

```own
x = 10 // integer
y = 1.61803 // float
z = "abcd" // string
```

If some function requires string as argument, but number was passed, then numeric value will automatically converts to string.

```own
x = 90
print x // Ok, 90 converts to "90"
```