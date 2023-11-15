# String functions

Fields:
  - `length` - string length
  - `lower` - lower case string
  - `upper` - upper case string
  - `chars` - ASCII characters array

Functions:
  - `trim()` - removes any leading and trailing whitespaces in string
  - `startsWith(str, offset = 0)` - checks whether the string starts with the substring str at offset
  - `endsWith(str)` - checks whether the string ends with the str
  - `matches(regex)` - checks whether the string matches regex pattern
  - `contains(str)` - checks whether the string contains substring str
  - `equalsIgnoreCase(str)` - checks equality of two strings ignore case (tEsT = TEST)
  - `isEmpty()` - returns true, if the string is empty

In addition, there are automatic function extensions available if the function accepts a string as the first argument:

@[code](../../code/basics/string_functions1.own)
