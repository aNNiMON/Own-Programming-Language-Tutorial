# Определение функций

Для определения функции используется ключевое слово `def`. Затем идёт имя, аргументы и тело функции. Пример:

```own
def function(arg1, arg2) {
  println arg1
}
```

## Короткий синтаксис

Возможен короткий синтаксис:

```own
def repeat(str, count) = str * count
```

что равносильно:

```own
def repeat(str, count) {
  return str * count
}
```

## Аргументы по умолчанию

Аргументы функции могут иметь значения по умолчанию.

```own
def repeat(str, count = 5) = str * count
```

В этом случае обязательным будет только аргумент `str`

```own
repeat("*")     //  *****
repeat("+", 3)  //  +++
```

Аргументы по умолчанию обязательно должны идти после обязательных аргументов, если такие были.

```own
def repeat(str = "*", count) = str * count
```

Приведёт к ошибки парсинга: `ParseError on line 1: Required argument cannot be after optional`

## Внутренние функции

Внутри функции можно объявить другую функцию.

@[code](../../code/basics/fibonacci.own)