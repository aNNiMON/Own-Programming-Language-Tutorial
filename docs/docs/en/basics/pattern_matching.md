# Pattern matching

The `match` operator allows to match values by pattern.

@[code](../../code/basics/pattern_matching1.own)

@[code](../../code/basics/pattern_matching2.own)

In this case value and type are checking. If none of `case` branches doesn't match, the body of `case _` branch will executes.


In addition to the constant values, you can set variable name to `case`.

@[code](../../code/basics/pattern_matching3.own)

In this case there is two scenarios:

1.  Variable is already defined. Matching to its value.
2.  Variable is not defined. Assign matching value to it and executes body of the `case` branch.

In the example above, the interpreter sees the first two branches as:

```own
case 10: 
case 20:
```

For the last branch `c` variable is not defined, so assign `c = x` and execute body of the `case c` branch.


## Refinements

`case` branch may have additional comparison

@[code](../../code/basics/pattern_matching4.own)


## Matching arrays

To compare elements of arrays, the following syntax is used:

*   `case []:` executes if there are no elements in array
*   `case [a]:` executes if an array contains one element
*   `case [a :: b]:` executes if an array contains two or more elements
*   `case [a :: b :: c :: d :: e]:` executes if an array contain five or more elements

There are two rules for the last two cases:

*   If variables count matches array elements count - all variables are assigned to the value of the array.

```own
match [0, 1, 2] {
  case [x :: y :: z]: // x = 0, y = 1, z = 2
}
```

*   If array elements count is greater, then the rest of the array will be assigned to the last variable.

```own
match [0, 1, 2, 3, 4] {
  case [x :: y :: z]: // x = 0, y = 1, z = [2, 3, 4]
}
```

An example of a recursive output array

@[code](../../code/basics/pattern_matching5.own)


## Matching array's value

To compare values of array's elements, the following syntax is used:

*   `case (expr1, expr2, expr3):` executes if an array contain 3 elements and first element is equal to expr1 result, second element is equal to expr2 and third element is equal to expr3.
*   `case (expr1, _):` executes if an array contain 2 elements and first element is equal to expr1 result and result of the second element is not importand.

FizzBuzz classical problem can be solved using Pattern Matching:

@[code](../../code/basics/pattern_matching6.own)
