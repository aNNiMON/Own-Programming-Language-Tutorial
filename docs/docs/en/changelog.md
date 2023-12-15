# Changelog

## 1.5.0

- Added modules `zip`, `gzip`, `okhttp`
- Added functions `std::getBytes`, `std::stringFromBytes`, `std::stripMargin`
- Added JProgressBar, JTextArea, JScrollPane to `forms`, methods for JButton, JTextField and WindowListener
- Added function `joining` to `functional::stream`
- Added array properties: `arr.length`, `arr.isEmpty()`, `arr.joinToString(...)`
- Added null coalesce operator `??`
- Added basic support for classes
- Strict string to number conversion
- `for` supports iterating strings and arrays with index:  
  `for ch : "test"`  
  `for ch, code : "test"`  
  `for el : arr`  
  `for el, index : arr`
- Pretty-print for `jsonencode`:  
  `jsonencode(obj)` — minified json  
  `jsonencode(obj, 2)` — pretty-print json with 2 spaces indent
- Ability to set options for yaml parser/dumper
- Fixed mysql connection in `jdbc`
- Fixed `str::range` for reversed ranges
- Fixed files::readBytes with offset and length
- Fixed matching class constructor in `java::new`. Ability to instantiate classes with `new` operator
- Other minor changes


## 1.4.0

- Added modules `downloader`, `regex`
- Added functions `std::arraySplice`, `std::default`
- Added constant `std::OwnLang` which stores language version and platform metadata
- Added `peek`, `sorted` to StreamValue
- An ability to import several modules `use ["std", "types", "math"]`
- String internal fields support (length, lower, upper, chars, trim(), startsWith(s), endsWith(s), matches(s), contains(s), equalsIgnoreCase(s), isEmpty()). Also support for extensions: `"%d. %s".sprintf(1, "OwnLang")` -> `sprintf("%d. %s", 1, "OwnLang")`
- Added  kawaii-operator `^^`
- Improved REPL mode. Now command history (up key) supported on all platforms. Added autocompletion by Tab key.
- Improved error output
- Updated examples


## 1.3.0

- Function and function call chaining support (`func().func()` and `func()()`)
- Added `takewhile`, `dropwhile`, `stream` functions to `functional` module
- Added `parseInt`, `parseLong`, `toHexString` functions to `std` module
- Added `copy` function to `files` module
- Added `socket`, `base64`, `java`, `forms`, `jdbc` modules
- Improved optimization
- Updated examples
- Minor fixes and improvements

## 1.2.0

- Added `canvasfx`, `date`, `yml`, `aimp` modules
- Updated `std`, `math`, `files`, `functional` modules
- Added `std::ARGS` constant for accessing command-line arguments
- Added REPL mode, Beautifier, Linter, Optimizer
- Fixed error recovering in parser and deadlock in lexer
- Added merging objects operation `map1 + map2`
- Fixed variables scope
- Speed up files reading
- Added NumberValue cache
- Updated Netbeans plugin
- Added examples and help
