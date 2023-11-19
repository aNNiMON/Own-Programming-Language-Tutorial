import modules from './modules'
export default {
  '/': {
    text: {'en': 'OwnLang', 'ru': 'OwnLang'},
    pages: [
      'README.md',
      'links.md'
    ]
  },

  '/basics/': {
    text: {'en': 'Basics', 'ru': 'Основы'},
    pages: [
      'comments.md',
      'strings.md',
      'types.md',
      'loops.md',
      'functions.md',
      'destructuring_assignment.md',
      'pattern_matching.md',
      'string_functions.md',
      'array_functions.md'
    ]
  },

  '/modules/': {
    text: {'en': 'Modules', 'ru': 'Модули'},
    pages: modules
  }
}