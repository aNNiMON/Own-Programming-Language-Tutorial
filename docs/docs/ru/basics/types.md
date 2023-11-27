# Типы

В OwnLang есть такие типы:

 * Number - числа (охватывает как целые, так и вещественные числа)
 * String - строки
 * Array - массивы
 * Map - объекты (ассоциативные массивы)
 * Function - функции

Поскольку OwnLang - динамически типизируемый язык программирования, это значит, что явно объявлять типы не нужно.

```own
x = 10 // целое число
y = 1.61803 // вещественное число
z = "abcd" // строка
```

Если какая-либо функция предполагает использование строк в качестве аргументов, а были переданы числа, то значения автоматически приведутся к строке.

```own
x = 90
print x
```